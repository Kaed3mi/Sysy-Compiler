package frontend;

import exceptions.CompileError;
import exceptions.ErrorBuilder;
import exceptions.ErrorType;
import frontend.lexical.Ident;
import frontend.semantic.FuncTable;
import frontend.semantic.SymTable;
import frontend.semantic.SymType;
import frontend.semantic.Symbol;
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
import midend.Function;
import midend.llvm_type.LLvmType;
import midend.value.Value;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Stack;

public class Visitor {
    private final Ast ast;
    private BasicBlock curBasicBlock;
    private Function curFunc;
    private Stack<BasicBlock> loopStack;
    private SymTable curSymTable;
    private final FuncTable funcTable;

    public Visitor(Ast ast) throws Exception {
        this.ast = ast;
        curBasicBlock = null;
        curFunc = null;
        loopStack = new Stack<>();
        curSymTable = new SymTable(null);
        funcTable = new FuncTable();
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
            visitFuncDef((FuncDef) compUnit);
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
            visitConstDef(constDef, constDecl.getbType());
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
        } else {
            curSymTable.append(new Symbol(ident, constDef.toSymType(), true));
        }
        visitConstInitVal((ConstInitVal) constDef.getInitVal());
        // ConstDef一定要有initVal
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
            visitVarDef(varDef, varDecl.getbType());
        }
    }

    /**
     * 包含普通变量、一维数组、二维数组定义
     * VarDef ::= Ident { '[' ConstExp ']' } | Ident { '[' ConstExp ']' } '=' InitVal
     */
    private void visitVarDef(VarDef varDef, BType bType) throws Exception {
        Ident ident = varDef.getIdent();
        LLvmType pointeeType = bType.toLLvmType();
        // 维护符号表
        if (curSymTable.isDuplicated(ident)) {
            ErrorBuilder.appendError(new CompileError(ident.getLineNum(), ErrorType.DUPLICATED_IDENT, "var重定义: " + ident));
            return;
        } else {
            curSymTable.append(new Symbol(ident, varDef.toSymType(), false));
        }
        if (varDef.hasInitVal()) {
            visitInitVal(varDef.getInitVal());
        }
        // ConstDef一定要有initVal
    }

    /**
     * InitVal ::= Exp | '{' [ InitVal { ',' InitVal } ] '}'
     */
    private void visitInitVal(InitVal initVal) {

    }

    /**
     * FuncDef ::= FuncType Ident '(' [FuncFParams] ')' Block
     */
    private void visitFuncDef(FuncDef funcDef) throws Exception {
        LLvmType retType = funcDef.toLLvmType();
        Ident ident = funcDef.getIdent();
        // 维护函数表
        if (funcTable.isDuplicated(ident)) {
            ErrorBuilder.appendError(new CompileError(ident.getLineNum(), ErrorType.DUPLICATED_IDENT, "函数重定义: " + ident));
            return;
        }
        // 维护符号表
        curSymTable = new SymTable(curSymTable);
        curFunc = new Function(funcDef);
        funcTable.append(curFunc);
        // FuncFParams可能不存在
        if (funcDef.getFuncFParams() != null) {
            visitFuncFParams(funcDef.getFuncFParams());
        }
        if (!funcTable.isBroken(ident)) {
            visitBlock(funcDef.getBlock(), false);
        }
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
        Symbol symbol = new Symbol(ident, switch (dimNum) {
            case 0 -> SymType.VAR;
            case 1 -> SymType.DIM1;
            case 2 -> SymType.DIM2;
            default -> throw new Exception("你几维数组啊");
        }, false);
        if (curSymTable.isDuplicated(ident)) {
            ErrorBuilder.appendError(new CompileError(ident.getLineNum(), ErrorType.DUPLICATED_IDENT, "参数重复声明"));
            funcTable.addBrokenFunction(curFunc.getIdent());
        }
        curSymTable.append(symbol);
        curFunc.addParam(new Value(switch (dimNum) {
            case 0 -> SymType.VAR;
            case 1 -> SymType.DIM1;
            case 2 -> SymType.DIM2;
            default -> throw new Exception("你几维数组啊");
        }));
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
        visitCond(ifStmt.getCond());
        visitStmt(ifStmt.getThenStmt());
        if (ifStmt.getElseStmt() != null) {
            visitStmt(ifStmt.getElseStmt());
        }
    }

    /**
     * <额外语法>
     * 'while' '(' [Cond] ')' Stmt
     * 'for' '(' [ForStmt] ';' [Cond] ';' [ForStmt] ')' Stmt   // 1. 无缺省 2. 缺省第一个ForStmt 3. 缺省Cond 4. 缺省第二个ForStmt
     */
    private void visitLoopStmt(LoopStmt loopStmt) throws Exception {
        if (loopStmt.getInitStmt() != null) {
            visitForStmt(loopStmt.getInitStmt());
        }
        if (loopStmt.getCond() != null) {
            visitCond(loopStmt.getCond());
        }
        if (loopStmt.getContinueStmt() != null) {
            visitForStmt(loopStmt.getContinueStmt());
        }
        loopStack.push(new BasicBlock());
        visitStmt(loopStmt.getBodyStmt());
        loopStack.pop();
    }

    /**
     * <额外语法>
     * 条件表达式 Cond → LOrExp
     */
    private void visitCond(Cond cond) throws Exception {
        visitExp(cond.getFirst());
        for (Exp exp : cond.getFollows()) {
            visitExp(exp);
        }
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
    }

    /**
     * <额外语法>
     * 'continue' ';'
     */
    private void visitContinueStmt(ContinueStmt continueStmt) {
        if (loopStack.empty()) {
            ErrorBuilder.appendError(new CompileError(continueStmt.getLineNum(), ErrorType.WITHOUT_LOOP, "不在循环中"));
        }
    }

    /**
     * <额外语法>
     * 'return' [Exp] ';'
     */
    private void visitReturnStmt(ReturnStmt returnStmt) throws Exception {
        if (returnStmt.getExp() != null) {
            visitExp(returnStmt.getExp());
        }
    }

    /**
     * <额外语法>
     * 'printf' '(' FormatString {',' Exp } ')' ';'
     */
    private void visitPrintfStmt(PrintfStmt printfStmt) throws Exception {
        for (Exp exp : printfStmt.getArguments()) {
            visitExp(exp);
        }
    }

    /**
     * 左值表达式 LVal → Ident {'[' Exp ']'}
     */
    private Value visitLVal(LVal lVal, boolean isAssign) throws Exception {
        Symbol symbol = curSymTable.find(lVal.getIdent());
        if (symbol == null) {
            return new Value(SymType.VOID);
        }
        if (symbol.isConstant() && isAssign) {
            ErrorBuilder.appendError(new CompileError(lVal.getIdent().getLineNum(),
                    ErrorType.READ_ONLY, "const赋值" + lVal.getIdent()));
        }

        return switch (symbol.getDimNum() - lVal.getArrayDim().size()) {
            case 0 -> new Value(SymType.VAR);
            case 1 -> new Value(SymType.DIM1);
            case 2 -> new Value(SymType.DIM2);
            default -> throw new Exception("没有这个case");
        };
    }

    private Value visitExp(Exp exp) throws Exception {
        if (exp == null) {
            return null;
        }
        if (exp instanceof BinaryExp) {
            return visitBinaryExp((BinaryExp) exp);
        } else if (exp instanceof GetIntStmt) {
            return visitFuncCall((FuncCall) exp);
        } else {
            return visitUnaryExp((UnaryExp) exp);
        }
    }

    private Value visitBinaryExp(BinaryExp binaryExp) throws Exception {
        Value first = visitExp(binaryExp.getFirst());
        for (Exp exp : binaryExp.getFollows()) {
            if (!first.getSymType().equals(visitExp(exp).getSymType())) {
                return new Value(SymType.VOID);
            }
        }
        return first;
    }

    private Value visitUnaryExp(UnaryExp unaryExp) throws Exception {
        return visitPrimary(unaryExp.getPrimaryExp());
    }

    private Value visitPrimary(PrimaryExp primaryExp) throws Exception {
        if (primaryExp instanceof Exp) {
            return visitExp((Exp) primaryExp);
        } else if (primaryExp instanceof LVal) {
            return visitLVal((LVal) primaryExp, false);
        } else if (primaryExp instanceof Number) {
            return visitNumber((Number) primaryExp);
        } else if (primaryExp instanceof FuncCall) {
            return visitFuncCall((FuncCall) primaryExp);
        }
        return new Value(SymType.VAR);
    }

    private Value visitNumber(Number number) {
        return new Value(SymType.VAR);
    }

    private Value visitFuncCall(FuncCall funcCall) throws Exception {
        Function function = funcTable.get(funcCall.getIdent());
        if (function == null) {
            return new Value(SymType.VOID);
        }

        function.checkIllegal(funcCall);
        ArrayList<Value> params = new ArrayList<>();
        if (funcCall.getFuncRParams() != null) {
            for (Exp exp : funcCall.getFuncRParams().getExps()) {
                params.add(visitExp(exp));
            }
        }
        Iterator<Value> it1 = params.iterator();
        Iterator<Value> it2 = function.getParams().iterator();

        while (it1.hasNext() && it2.hasNext()) {
            if (it1.next().getSymType() != it2.next().getSymType()) {
                ErrorBuilder.appendError(new CompileError(funcCall.getIdent().getLineNum(),
                        ErrorType.WRONG_ARGUMENTS_TYPE, "参数类型错误"));
                break;
            }
        }
        if (function.hasReturnVal()) {
            return new Value(SymType.VAR);
        }
        return new Value(SymType.VOID);
    }

}
