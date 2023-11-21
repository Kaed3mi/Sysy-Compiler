package frontend;

import exceptions.CompileError;
import exceptions.ErrorBuilder;
import exceptions.ErrorType;
import frontend.lexical.BinaryOperator;
import frontend.lexical.Ident;
import frontend.lexical.Lexeme;
import frontend.lexical.Token;
import frontend.semantic.SymTable;
import frontend.semantic.Symbol;
import frontend.semantic.initialization.ArrayInitialization;
import frontend.semantic.initialization.Initialization;
import frontend.semantic.initialization.VarInitialization;
import frontend.semantic.initialization.ZeroInitialization;
import frontend.syntax.ast.Ast;
import frontend.syntax.ast.BType;
import frontend.syntax.ast.CompUnit;
import frontend.syntax.ast.declaration.*;
import frontend.syntax.ast.expression.Number;
import frontend.syntax.ast.expression.*;
import frontend.syntax.ast.function.FuncCall;
import frontend.syntax.ast.function.FuncDef;
import frontend.syntax.ast.function.FuncFParam;
import frontend.syntax.ast.function.FuncFParams;
import frontend.syntax.ast.statement.*;
import midend.BasicBlock;
import midend.GlobalVar;
import midend.LLvmBuilder;
import midend.LLvmIdent;
import midend.constant.BooleanConstant;
import midend.constant.Constant;
import midend.constant.IntConstant;
import midend.function.ExternFunc;
import midend.function.FuncTable;
import midend.function.Function;
import midend.function.FunctionFParam;
import midend.instruction.*;
import midend.llvm_type.ArrayType;
import midend.llvm_type.BasicType;
import midend.llvm_type.LLvmType;
import midend.llvm_type.PointerType;
import midend.value.Value;
import util.Pair;

import java.util.*;

public class Visitor {
    private final Ast ast;
    private boolean isGlobal;
    private BasicBlock curBasicBlock;
    private Function curFunc;
    private final Stack<Pair<BasicBlock, BasicBlock>> loopStack; // K表示continueDst，V表示breakDst
    private SymTable curSymTable;
    private final FuncTable funcTable;
    private final Evaluator evaluator;

    public Visitor(Ast ast) throws Exception {
        this.ast = ast;
        isGlobal = true;
        curBasicBlock = null;
        curFunc = null;
        loopStack = new Stack<>();
        curSymTable = new SymTable(null);
        funcTable = new FuncTable();
        evaluator = new Evaluator(curSymTable);
    }


    public void visit() throws Exception {
        for (CompUnit compUnit : ast) {
            visitCompUnit(compUnit);
        }
    }

    /**
     * CompUnit ::= { Decl } { FuncDef } MainFuncDef
     */
    private void visitCompUnit(CompUnit compUnit) throws Exception {
        if (compUnit instanceof Decl) {
            visitDecl((Decl) compUnit);
        } else {
            isGlobal = false;
            visitFuncDef((FuncDef) compUnit);
            isGlobal = true;
        }
    }

    /**
     * Decl ::= ConstDecl | VarDecl
     */
    private void visitDecl(Decl decl) throws Exception {
        if (decl instanceof ConstDecl) {
            visitConstDecl((ConstDecl) decl);
        } else {
            visitVarDecl((VarDecl) decl);
        }
    }

    /**
     * ConstDecl ::= 'const' BType ConstDef { ',' ConstDef } ';'
     */
    private void visitConstDecl(ConstDecl constDecl) throws Exception {
        for (ConstDef constDef : constDecl.getConstDefList()) {
            visitDef(constDef, constDecl.getbType());
        }
    }

    /**
     * ConstDef ::= Ident { '[' ConstExp ']' } '=' ConstInitVal
     */
    private void visitConstDef(ConstDef constDef, BType bType) throws Exception {
        Ident ident = constDef.getIdent();
        LLvmType pointeeType = bType.toLLvmType();
        if (curSymTable.isDuplicated(ident)) {
            ErrorBuilder.appendError(new CompileError(ident.getLineNum(), ErrorType.DUPLICATED_IDENT, "const重定义: " + ident));
            return;
        }
        Value pointer = null;
        Initialization constInitialization = null;

        visitConstInitVal((ConstInitVal) constDef.getInitVal());
        // ConstDef一定要有initVal

        curSymTable.append(new Symbol(ident, LLvmType.I32_TYPE, null, pointer));
    }

    /**
     * ConstInitVal → ConstExp | '{' [ ConstInitVal { ',' ConstInitVal } ] '}'
     */
    private void visitConstInitVal(ConstInitVal constInitVal) {

    }

    /**
     * VarDecl ::= BType VarDef { ',' VarDef } ';'
     */
    private void visitVarDecl(VarDecl varDecl) throws Exception {
        for (VarDef varDef : varDecl.getVarDefList()) {
            visitDef(varDef, varDecl.getbType());
        }
    }

    /**
     * 包含普通变量、一维数组、二维数组定义
     * VarDef ::= Ident { '[' ConstExp ']' } | Ident { '[' ConstExp ']' } '=' InitVal
     */
    private void visitDef(Def def, BType bType) throws Exception {
        Ident ident = def.getIdent();
        LLvmType pointeeType = bType.toLLvmType();
        ArrayList<Integer> lengths = new ArrayList<>();
        for (ConstExp exp : def.getArrayDim()) {
            lengths.add(((IntConstant) evaluator.eval(exp)).getVal());
        }
        Collections.reverse(lengths);
        pointeeType = lengths.stream().reduce(pointeeType, ArrayType::new, (type1, type2) -> type2);
        // 维护符号表
        if (curSymTable.isDuplicated(ident)) {
            ErrorBuilder.appendError(new CompileError(ident.getLineNum(), ErrorType.DUPLICATED_IDENT, "var重定义: " + ident));
            return;
        }
        // 处理初始化
        Initialization initialization = null;
        if (def.hasInitVal()) {
            // 数组初始化　と　非数组初始化
            if (def.isArray()) {
                // 需要eval
                initialization = visitInitArray((InitArray) def.getInitVal(), (ArrayType) pointeeType, isGlobal);
                // throw new RuntimeException("数组初始化别急");
            } else {
                if (isGlobal) {
                    // 需要eval
                    Constant constant = evaluator.eval(def.getInitVal());
                    initialization = new VarInitialization((Value) constant);
                } else {
                    Value value = visitInitVal(def.getInitVal());
                    initialization = new VarInitialization(value);
                }
            }
        }

        // 全局变量或局部变量
        Value pointer = null;
        if (isGlobal) {
            if (!def.hasInitVal()) {
                if (def.isArray()) {
                    // 数组
                    initialization = new ZeroInitialization(pointeeType);
                } else {
                    initialization = new VarInitialization(IntConstant.ZERO);
                }
            }
            pointer = new GlobalVar(pointeeType, LLvmIdent.GlobalVarIdent(def.getIdent()), initialization);
            LLvmBuilder.addGlobalVar((GlobalVar) pointer);
        } else {
            pointer = new AllocaInstr(pointeeType, curBasicBlock);
            ((Instr) pointer).setComment(ident);
            curBasicBlock.addInstr((Instr) pointer);
            if (def.hasInitVal()) {
                if (initialization instanceof VarInitialization) {
                    StoreInstr storeInstr = new StoreInstr(((VarInitialization) initialization).initVal(), pointer, curBasicBlock);
                    curBasicBlock.addInstr(storeInstr);
                } else if (initialization instanceof ArrayInitialization) {
                    ArrayList<Initialization> init1s = ((ArrayInitialization) initialization).getInitializations();
                    for (int i = 0; i < init1s.size(); i++) {
                        Initialization init1 = init1s.get(i);
                        if (init1 instanceof ArrayInitialization) {
                            ArrayList<Initialization> init2s = ((ArrayInitialization) init1).getInitializations();
                            for (int j = 0; j < init2s.size(); j++) {
                                VarInitialization init2 = (VarInitialization) init2s.get(j);
                                GetElemPtrInstr getElemPtrInstr = new GetElemPtrInstr(
                                        LLvmType.I32_TYPE, pointer, // 1
                                        new ArrayList<>(List.of(IntConstant.ZERO, new IntConstant(i), new IntConstant(j))), // 2
                                        curBasicBlock   // 3
                                );
                                curBasicBlock.addInstr(getElemPtrInstr);
                                StoreInstr storeInstr = new StoreInstr(init2.initVal(), getElemPtrInstr, curBasicBlock);
                                curBasicBlock.addInstr(storeInstr);
                            }
                        } else {
                            VarInitialization init2 = (VarInitialization) init1;
                            GetElemPtrInstr getElemPtrInstr = new GetElemPtrInstr(
                                    LLvmType.I32_TYPE, pointer, // 1
                                    new ArrayList<>(List.of(IntConstant.ZERO, new IntConstant(i))), // 2
                                    curBasicBlock   // 3
                            );
                            curBasicBlock.addInstr(getElemPtrInstr);
                            StoreInstr storeInstr = new StoreInstr(init2.initVal(), getElemPtrInstr, curBasicBlock);
                            curBasicBlock.addInstr(storeInstr);
                        }
                    }
                }
            }
        }


        curSymTable.append(new Symbol(
                ident,
                pointeeType,
                null,
                pointer
        ));

    }

    private ArrayInitialization visitInitArray(InitArray initArray, ArrayType arrayType, boolean eval) {
        ArrayList<Initialization> initializations = new ArrayList<>();
        LLvmType elementType = arrayType.getElementType();
        for (InitVal initVal : initArray.getInitVals()) {
            Initialization initialization;
            if (elementType instanceof ArrayType) {
                initialization = visitInitArray((InitArray) initVal, (ArrayType) elementType, eval);
            } else {
                if (eval) {
                    Constant constant = evaluator.eval(initVal);
                    initialization = new VarInitialization((Value) constant);
                } else {
                    Value value = visitInitVal(initVal);
                    initialization = new VarInitialization(value);
                }
            }
            initializations.add(initialization);
        }
        return new ArrayInitialization(arrayType, initializations, isGlobal);
    }

    /**
     * InitVal ::= Exp | '{' [ InitVal { ',' InitVal } ] '}'
     */
    private Value visitInitVal(InitVal initVal) {
        return visitExp((Exp) initVal);
    }

    /**
     * FuncDef ::= FuncType Ident '(' [FuncFParams] ')' Block
     */
    private void visitFuncDef(FuncDef funcDef) throws Exception {
        Ident ident = funcDef.getIdent();
        // 维护函数表
        if (funcTable.isDuplicated(ident)) {
            ErrorBuilder.appendError(new CompileError(ident.getLineNum(), ErrorType.DUPLICATED_IDENT, "函数重定义: " + ident));
            return;
        }
        // 维护符号表
        curSymTable = new SymTable(curSymTable);
        curFunc = new Function(funcDef.toLLvmType(), funcDef.getIdent().toString());
        curFunc.addBasicBlock(curBasicBlock = new BasicBlock());
        funcTable.append(curFunc);
        // FuncFParams可能不存在
        if (funcDef.getFuncFParams() != null) {
            visitFuncFParams(funcDef.getFuncFParams());
        }
        if (!funcTable.isBroken(ident)) {
            visitBlock(funcDef.getBlock(), false);
        }
        curBasicBlock.forceTerminate();
        curSymTable = curSymTable.getParentTable();
        curFunc = null;
    }

    /**
     * FuncFParams ::= FuncFParam { ',' FuncFParam } // 1.花括号内重复0次 2.花括号内重复多次
     */
    private void visitFuncFParams(FuncFParams funcFParams) throws Exception {
        for (FuncFParam funcFParam : funcFParams.getFuncFParams()) {
            visitFuncFParam(funcFParam);
        }
    }

    /**
     * 函数形参 FuncFParam → BType Ident ['[' ']' { '[' ConstExp ']' }]
     */
    private void visitFuncFParam(FuncFParam funcFParam) throws Exception {
        int dimNum = funcFParam.getArrayDim().size();
        Ident ident = funcFParam.getIdent();
        // TODO 只需要考虑int情况
        LLvmType paramType;
        if (funcFParam.isArray()) {
            ArrayList<Integer> lengths = new ArrayList<>();
            for (ConstExp index : funcFParam.getArrayDim()) {
                lengths.add(((IntConstant) evaluator.eval(index)).getVal());
            }
            Collections.reverse(lengths);
            LLvmType pointeeType = funcFParam.getbType().toLLvmType();
            pointeeType = lengths.stream()
                    .reduce(pointeeType, ArrayType::new, (type1, type2) -> type2);
            paramType = new PointerType(pointeeType);
        } else {
            paramType = funcFParam.getbType().toLLvmType();
        }

        FunctionFParam functionFParam = new FunctionFParam(paramType);
        curFunc.addFunctionFParam(functionFParam);
        // 先alloc
        AllocaInstr paramPointer = new AllocaInstr(paramType, curBasicBlock);
        curBasicBlock.addInstr(paramPointer);
        // 后store
        StoreInstr storeInstr = new StoreInstr(functionFParam, paramPointer, curBasicBlock);
        curBasicBlock.addInstr(storeInstr);
        if (curSymTable.isDuplicated(ident)) {
            ErrorBuilder.appendError(new CompileError(ident.getLineNum(), ErrorType.DUPLICATED_IDENT, "参数重复声明"));
            //funcTable.addBrokenFunction(curFunc.getIdent());
        }

        curSymTable.append(new Symbol(
                funcFParam.getIdent(), paramType, null, paramPointer
        ));
    }

    /**
     * 语句块 Block ::= '{' { BlockItem } '}'
     */
    private void visitBlock(Block block, boolean newSymTable) throws Exception {
        if (newSymTable) {
            curSymTable = new SymTable(curSymTable);
        }
        for (BlockItem blockItem : block.getBlockItems()) {
            visitBlockItem(blockItem);
        }
        if (newSymTable) {
            curSymTable = curSymTable.getParentTable();
        }
    }

    /**
     * 语句块项 BlockItem → Decl | Stmt
     */
    private void visitBlockItem(BlockItem blockItem) throws Exception {
        if (blockItem instanceof Decl) {
            visitDecl((Decl) blockItem);
        } else {
            visitStmt((Stmt) blockItem);
        }
    }

    /**
     * 每种类型的语句都要覆盖
     * 语句 Stmt ::=
     * LVal '=' Exp ';'                                         |
     * [Exp] ';'                                                |          //有无Exp两种情况
     * Block                                                    |
     * 'if' '(' Cond ')' Stmt [ 'else' Stmt ]                   |          // 1.有else 2.无else
     * 'for' '(' [ForStmt] ';' [Cond] ';' [ForStmt] ')' Stmt    |          // 1. 无缺省 2. 缺省第一个ForStmt 3. 缺省Cond 4. 缺省第二个ForStmt
     * 'break' ';' | 'continue' ';'                             |
     * 'return' [Exp] ';'                                       |          // 1.有Exp 2.无Exp
     * LVal '=' 'getint' '(' ')' ';'                            |
     * 'printf' '(' FormatString {',' Exp } ')' ';'                        // 1.有Exp 2.无Exp
     */
    private void visitStmt(Stmt stmt) throws Exception {
        if (stmt instanceof AssignStmt) {
            visitAssignStmt((AssignStmt) stmt);
        } else if (stmt instanceof Block) {
            visitBlock((Block) stmt, true);
        } else if (stmt instanceof ExpStmt) {
            visitExpStmt((ExpStmt) stmt);
        } else if (stmt instanceof IfStmt) {
            visitIfStmt((IfStmt) stmt);
        } else if (stmt instanceof LoopStmt) {
            visitLoopStmt((LoopStmt) stmt);
        } else if (stmt instanceof BreakStmt) {
            visitBreakStmt((BreakStmt) stmt);
        } else if (stmt instanceof ContinueStmt) {
            visitContinueStmt((ContinueStmt) stmt);
        } else if (stmt instanceof ReturnStmt) {
            visitReturnStmt((ReturnStmt) stmt);
        } else if (stmt instanceof PrintfStmt) {
            visitPrintfStmt((PrintfStmt) stmt);
        }
    }

    /**
     * <额外语法>
     * 语句 AssignStmt → LVal '=' ( Exp | getint '(' ')' ) ';'
     */
    private void visitAssignStmt(AssignStmt assignStmt) throws Exception {
        Value left = visitLVal(assignStmt.getlVal(), true);
        Value right = visitExp(assignStmt.getExp());
        StoreInstr storeInstr = new StoreInstr(right, left, curBasicBlock);
        curBasicBlock.addInstr(storeInstr);
    }

    /**
     * <额外语法>
     */
    private void visitExpStmt(ExpStmt expStmt) throws Exception {
        if (expStmt.getExp() != null) {
            visitExp(expStmt.getExp());
        }
    }

    /**
     * <额外语法>
     * Stmt ::= 'if' '(' Cond ')' Stmt [ 'else' Stmt ]
     */
    private void visitIfStmt(IfStmt ifStmt) throws Exception {
        BasicBlock thenBlock, elseBlock, followBlock;

        curFunc.addBasicBlock(thenBlock = new BasicBlock());
        if (ifStmt.hasElseStmt()) {
            curFunc.addBasicBlock(elseBlock = new BasicBlock());
        } else {
            elseBlock = null;
        }
        curFunc.addBasicBlock(followBlock = new BasicBlock());
        // visitCond时需要提前知道trueBlock和falseBlock，这里根据是否有elseBlock来判断
        BasicBlock trueBlock = thenBlock, falseBlock = elseBlock == null ? followBlock : elseBlock;
        Value condVal = visitExp(ifStmt.getCond(), trueBlock, falseBlock);
        Instr branchInstr = new BranchInstr(condVal, trueBlock, falseBlock, curBasicBlock);
        curBasicBlock.addInstr(branchInstr);
        // visit thenBlock
        curBasicBlock = thenBlock;
        visitStmt(ifStmt.getThenStmt());
        // 设置thenBlock结束跳转
        Instr jumpInstr = new JumpInstr(followBlock, curBasicBlock);
        curBasicBlock.addInstr(jumpInstr);

        // visit ElseBlock
        if (ifStmt.hasElseStmt()) {
            curBasicBlock = elseBlock;
            visitStmt(ifStmt.getElseStmt());
            // 设置elseBlock结束跳转
            curBasicBlock.addInstr(jumpInstr);
        }

        curBasicBlock = followBlock;
    }

    /**
     * <额外语法>
     * 'while' '(' [Cond] ')' Stmt
     * 'for' '(' [ForStmt] ';' [Cond] ';' [ForStmt] ')' Stmt   // 1. 无缺省 2. 缺省第一个ForStmt 3. 缺省Cond 4. 缺省第二个ForStmt
     */
    private void visitLoopStmt(LoopStmt loopStmt) throws Exception {
        // 循环逻辑：condBlock->(bodyBlock, followBlock)
        BasicBlock condBlock, bodyBlock, continueBlock, followBlock;
        curFunc.addBasicBlock(condBlock = new BasicBlock());
        curFunc.addBasicBlock(bodyBlock = new BasicBlock());
        curFunc.addBasicBlock(continueBlock = new BasicBlock());
        curFunc.addBasicBlock(followBlock = new BasicBlock());
        // 初始化条件后直接跳入循环
        if (loopStmt.getInitStmt() != null) {
            visitForStmt(loopStmt.getInitStmt());
        }
        JumpInstr jumpToCond = new JumpInstr(condBlock, curBasicBlock);
        JumpInstr jumpToContinue = new JumpInstr(continueBlock, curBasicBlock);
        curBasicBlock.addInstr(jumpToCond);
        // visitCondBlock
        curBasicBlock = condBlock;
        Value condVal = BooleanConstant.TRUE;
        if (loopStmt.getCond() != null) {
            condVal = visitExp(loopStmt.getCond(), bodyBlock, followBlock);
        }
        BranchInstr branchInstr = new BranchInstr(condVal, bodyBlock, followBlock, curBasicBlock);
        curBasicBlock.addInstr(branchInstr);
        // visit BodyBlock并将ContinueStmt直接插入BodyBlock末尾

        // 在解析BodyBlock前需要将continueDst和breakDst压入loopStack栈
        loopStack.push(new Pair<>(continueBlock, followBlock));

        curBasicBlock = bodyBlock;
        visitStmt(loopStmt.getBodyStmt());
        curBasicBlock.addInstr(jumpToContinue);
        // 解析ContinueBlock，不能将continue内容直接放到bodyBlock后面，因为可以出现break直接终止bodyBlock，使得出现死循环
        curBasicBlock = continueBlock;
        if (loopStmt.getContinueStmt() != null) {
            visitForStmt(loopStmt.getContinueStmt());
        }
        curBasicBlock.addInstr(jumpToCond);

        loopStack.pop();
        // 结束，继续分析接下来的block
        curBasicBlock = followBlock;
    }

    /**
     * <额外语法>
     * 条件表达式 Cond → LOrExp
     */
    private Value visitCond(Cond cond, BasicBlock... blocks) throws Exception {
        visitExp(cond.getFirst());
        for (Exp exp : cond.getFollows()) {
            visitExp(exp);
        }
        return null;
    }

    /**
     * 语句 ForStmt → LVal '=' Exp // 存在即可
     */
    private void visitForStmt(ForStmt forStmt) throws Exception {
        visitAssignStmt(forStmt);
    }

    /**
     * <额外语法>
     * 'break' ';'
     */
    private void visitBreakStmt(BreakStmt breakStmt) {
        if (loopStack.empty()) {
            ErrorBuilder.appendError(new CompileError(breakStmt.getLineNum(), ErrorType.WITHOUT_LOOP, "不在循环中"));
        }
        JumpInstr jumpInstr = new JumpInstr(loopStack.peek().getValue(), curBasicBlock);
        curBasicBlock.addInstr(jumpInstr);
    }

    /**
     * <额外语法>
     * 'continue' ';'
     */
    private void visitContinueStmt(ContinueStmt continueStmt) {
        if (loopStack.empty()) {
            ErrorBuilder.appendError(new CompileError(continueStmt.getLineNum(), ErrorType.WITHOUT_LOOP, "不在循环中"));
        }
        JumpInstr jumpInstr = new JumpInstr(loopStack.peek().getKey(), curBasicBlock);
        curBasicBlock.addInstr(jumpInstr);
    }

    /**
     * <额外语法>
     * 'return' [Exp] ';'
     */
    private void visitReturnStmt(ReturnStmt returnStmt) throws Exception {
        Exp returnExp = returnStmt.getExp();
        Value returnValue = returnExp == null ? null : visitExp(returnExp);
        ReturnInstr returnInstr = new ReturnInstr(returnValue, curBasicBlock);
        curBasicBlock.addInstr(returnInstr);
    }

    /**
     * <额外语法>
     * 'printf' '(' FormatString {',' Exp } ')' ';'
     */
    private void visitPrintfStmt(PrintfStmt printfStmt) {
        String formatString = printfStmt.getFormatString().toString();
        formatString = formatString.substring(1, formatString.length() - 1); // 去除FormatString开头结尾的引号
        formatString = formatString.replaceAll("\\\\n", "\\$").replaceAll("%d", "&");
        Iterator<Exp> expIter = printfStmt.getArguments().iterator();
        formatString.chars().forEach(c -> {
            CallInstr callInstr;
            if (c == '&') {
                ArrayList<Value> intOutput = new ArrayList<>();
                intOutput.add(visitExp(expIter.next()));
                callInstr = new CallInstr(ExternFunc.PUT_INT, intOutput, curBasicBlock);
                curBasicBlock.addInstr(callInstr);
            } else {
                ArrayList<Value> charOutput = new ArrayList<>();
                charOutput.add(c == '$' ? new IntConstant('\n') : new IntConstant(c));
                callInstr = new CallInstr(ExternFunc.PUT_CH, charOutput, curBasicBlock);
                curBasicBlock.addInstr(callInstr);
            }
        });
    }

    /**
     * 左值表达式 LVal → Ident {'[' Exp ']'}
     */
    private Value visitLVal(LVal lVal, boolean isAssign) {
        Symbol symbol = curSymTable.find(lVal.getIdent());
        if (symbol == null) {
            throw new RuntimeException("not found in symbolTable");
        }
        if (symbol.isConstant() && isAssign) {
            ErrorBuilder.appendError(new CompileError(lVal.getIdent().getLineNum(),
                    ErrorType.READ_ONLY, "const赋值" + lVal.getIdent()));
        }

        Value lValPointer = symbol.pointer();
        LLvmType lValPointeeType = ((PointerType) lValPointer.lLvmType()).pointeeType();
        ArrayList<Value> indexValues = new ArrayList<>();
        indexValues.add(IntConstant.ZERO);
        for (Exp exp : lVal.getArrayDim()) {
            Value dim = visitExp(exp);
            if (lValPointeeType instanceof PointerType) {
                // 参数, 如int a[][...][...]...
                LoadInstr loadInstr = new LoadInstr(lValPointer, curBasicBlock);
                curBasicBlock.addInstr(loadInstr);
                lValPointeeType = ((PointerType) lValPointeeType).pointeeType();
                lValPointer = loadInstr;
                indexValues.clear();
                indexValues.add(dim);
            } else if (lValPointeeType instanceof ArrayType) {
                lValPointeeType = ((ArrayType) lValPointeeType).getElementType();
                indexValues.add(dim);
            } else {
                throw new RuntimeException("");
            }
        }
        if (!lVal.getArrayDim().isEmpty()) {
            lValPointer = new GetElemPtrInstr(lValPointeeType, lValPointer, indexValues, curBasicBlock);
            curBasicBlock.addInstr((Instr) lValPointer);
        }
        if (isAssign) {
            if (!(lValPointer.lLvmType() instanceof PointerType &&
                    ((PointerType) lValPointer.lLvmType()).pointeeType() instanceof BasicType)) {
                throw new RuntimeException("Only BasicType can be assigned.");
            }
            return lValPointer;
        } else {
            if (lValPointeeType instanceof ArrayType) {
                LLvmType destType = new PointerType(((ArrayType) lValPointeeType).getElementType());
                BitCastInstr bitCastInstr =
                        new BitCastInstr(destType, lValPointer, curBasicBlock);
                curBasicBlock.addInstr(bitCastInstr);
                bitCastInstr.setComment(lVal.getIdent());
                return bitCastInstr;
            } else {
                LoadInstr loadInstr = new LoadInstr(lValPointer, curBasicBlock);
                curBasicBlock.addInstr(loadInstr);
                loadInstr.setComment(lVal.getIdent());
                return loadInstr;
            }
        }
    }

    private Value visitExp(Exp exp, BasicBlock... blocks) {
        if (exp == null) {
            return null;
        }
        if (exp instanceof BinaryExp) {
            return visitBinaryExp((BinaryExp) exp, blocks);
        } else if (exp instanceof GetIntStmt) {
            return visitFuncCall((FuncCall) exp);
        } else {
            return visitUnaryExp((UnaryExp) exp);
        }
    }

    /**
     * 由于二元逻辑运算短路涉及到对于块的判断，因此需要传入blocks
     */
    private Value visitBinaryExp(BinaryExp binaryExp, BasicBlock... blocks) {
        Value first;
        BasicBlock trueBlock = null, falseBlock = null;
        BinaryOperator binaryOperator = binaryExp.getBinaryOperate();
        boolean isCond = binaryOperator == BinaryOperator.LOR || binaryOperator == BinaryOperator.LAND || binaryOperator == BinaryOperator.EQ;
        if (blocks.length == 2 && blocks[0] != null && blocks[1] != null) {
            isCond = true;
            trueBlock = blocks[0];
            falseBlock = blocks[1];
        }
        // 用于短路求值建立新的Block

        BasicBlock nextBlock = falseBlock;
        if (binaryOperator == BinaryOperator.LOR && !binaryExp.getFollows().isEmpty()) {
            nextBlock = new BasicBlock();
            curFunc.addBasicBlock(nextBlock);
        }
        if (binaryOperator == BinaryOperator.LOR || binaryOperator == BinaryOperator.LAND) {
            first = visitExp(binaryExp.getFirst(), trueBlock, nextBlock);
        } else {
            first = visitExp(binaryExp.getFirst());
        }

        Iterator<Exp> expIter = binaryExp.getFollows().iterator();
        Iterator<Token> opIter = binaryExp.getOperators().iterator();
        while (expIter.hasNext() && opIter.hasNext()) {
            Exp exp = expIter.next();
            Lexeme op = opIter.next().getLexeme();
            // 遍历，计算二元表达式
            if (op.isOfBinaryOp(BinaryOperator.ADD, BinaryOperator.MUL)) {
                Value nextValue = visitExp(exp);
                first = new AluInstr(LLvmType.I32_TYPE, op.toInstrOp(), first, nextValue, curBasicBlock);
                curBasicBlock.addInstr((Instr) first);
            } else if (op.isOfBinaryOp(BinaryOperator.LOR)) {
                BranchInstr branchInstr = new BranchInstr(first, trueBlock, nextBlock, curBasicBlock);
                curBasicBlock.addInstr(branchInstr);
                curBasicBlock = nextBlock;

                if (opIter.hasNext()) {
                    nextBlock = new BasicBlock();
                    curFunc.addBasicBlock(nextBlock);
                } else {
                    nextBlock = falseBlock;
                }
                first = visitExp(exp, trueBlock, nextBlock);
            } else if (op.isOfBinaryOp(BinaryOperator.LAND)) {
                nextBlock = new BasicBlock();
                curFunc.addBasicBlock(nextBlock);

                BranchInstr branchInstr = new BranchInstr(first, nextBlock, falseBlock, curBasicBlock);
                curBasicBlock.addInstr(branchInstr);
                curBasicBlock = nextBlock;
                first = visitExp(exp);
            } else if (op.isOfBinaryOp(BinaryOperator.EQ, BinaryOperator.REL)) {
                Value nextValue = visitExp(exp);
                IcmpInstr.IcmpOp icmpOp = switch (op) {
                    case EQL -> IcmpInstr.IcmpOp.EQ;
                    case NEQ -> IcmpInstr.IcmpOp.NE;
                    case GRE -> IcmpInstr.IcmpOp.SGT;
                    case GEQ -> IcmpInstr.IcmpOp.SGE;
                    case LSS -> IcmpInstr.IcmpOp.SLT;
                    case LEQ -> IcmpInstr.IcmpOp.SLE;
                    default -> throw new RuntimeException("");
                };
                first = new IcmpInstr(icmpOp, first, nextValue, curBasicBlock);
                curBasicBlock.addInstr((Instr) first);
            }
        }
        if (isCond && first.lLvmType() == LLvmType.I32_TYPE) {
            first = new IcmpInstr(IcmpInstr.IcmpOp.NE, first, IntConstant.ZERO, curBasicBlock);
            curBasicBlock.addInstr((Instr) first);
        }
        return first;
    }

    private Value visitUnaryExp(UnaryExp unaryExp) {
        Value first = visitPrimary(unaryExp.getPrimaryExp());
        ArrayList<Token> opIter = unaryExp.getOperators();
        for (int i = opIter.size() - 1; i >= 0; i--) {
            Lexeme op = opIter.get(i).getLexeme();
            switch (op) {
                case PLUS -> {
                    // +1无视当成1
                }
                case MINU -> {
                    first = new AluInstr(LLvmType.I32_TYPE, op.toInstrOp(), IntConstant.ZERO, first, curBasicBlock);
                    curBasicBlock.addInstr((Instr) first);
                }
                case NOT -> {
                    first = new IcmpInstr(
                            IcmpInstr.IcmpOp.EQ, first,
                            first.lLvmType() == LLvmType.I32_TYPE ? IntConstant.ZERO : BooleanConstant.FALSE,
                            curBasicBlock
                    );
                    curBasicBlock.addInstr((Instr) first);
                }
            }
        }
        return first;
    }

    private Value visitPrimary(PrimaryExp primaryExp) {
        if (primaryExp instanceof Exp) {
            return visitExp((Exp) primaryExp);
        } else if (primaryExp instanceof LVal) {
            return visitLVal((LVal) primaryExp, false);
        } else if (primaryExp instanceof Number) {
            return visitNumber((Number) primaryExp);
        } else if (primaryExp instanceof FuncCall) {
            return visitFuncCall((FuncCall) primaryExp);
        }
        throw new RuntimeException("PrimaryExp error");
    }

    private Value visitNumber(Number number) {
        return new IntConstant(number);
    }

    private Value visitFuncCall(FuncCall funcCall) {
        Function function = funcTable.get(funcCall.getIdent());
        if (function == null) {
            throw new RuntimeException("没有在函数表查询到函数");
        }

//        function.checkIllegal(funcCall); // 用来进行错误处理的
        ArrayList<Value> params = new ArrayList<>();
        if (funcCall.getFuncRParams() != null) {
            for (Exp exp : funcCall.getFuncRParams().getExps()) {
                params.add(visitExp(exp));
            }
        }
        Iterator<Value> it1 = params.iterator();
        Iterator<Value> it2 = function.getParams().iterator();

        while (it1.hasNext() && it2.hasNext()) {
            if (it1.next().lLvmType() != it2.next().lLvmType()) {
                ErrorBuilder.appendError(new CompileError(funcCall.getIdent().getLineNum(),
                        ErrorType.WRONG_ARGUMENTS_TYPE, "参数类型错误"));
                break;
            }
        }

        CallInstr callInstr = new CallInstr(function, params, curBasicBlock);
        curBasicBlock.addInstr(callInstr);
        return callInstr;
    }

}
