package frontend.syntax;

import frontend.lexical.*;
import frontend.syntax.ast.Ast;
import frontend.syntax.ast.BType;
import frontend.syntax.ast.declaration.*;
import frontend.syntax.ast.expression.Number;
import frontend.syntax.ast.expression.*;
import frontend.syntax.ast.function.*;
import frontend.syntax.ast.statement.*;

import java.util.ArrayList;


public class Parser {

    private final TokenList tokenList;

    public Parser(TokenList tokenList) {
        this.tokenList = tokenList;
    }

    public Ast parse() throws Exception {
        return parseCompUnit();
    }

    /**
     * CompUnit ::= { Decl } { FuncDef } MainFuncDef
     */
    private Ast parseCompUnit() throws Exception {
        Ast ast = new Ast();
        while (tokenList.hasNext()) {
            if (!tokenList.lookingAtFuncDef()) {
                ast.add(parseDecl());
            } else if (tokenList.lookingAtMainFuncDef()) {
                ast.add(parseMainFuncDef());
            } else {
                ast.add(parseFuncDef());
            }
        }
        SyntaxOutputBuilder.appendLine("<CompUnit>");
        return ast;
    }

    /**
     * Decl ::= ConstDecl | VarDecl
     */
    private Decl parseDecl() throws Exception {
        if (tokenList.lookingAtIsOf(Lexeme.CONSTTK)) {
            Decl ret = parseConstDecl();
            SyntaxOutputBuilder.appendLine("<Decl>");
            return ret;
        } else {
            Decl ret = parseVarDecl();
            SyntaxOutputBuilder.appendLine("<Decl>");
            return ret;
        }
    }

    /**
     * ConstDecl ::= 'const' BType ConstDef { ',' ConstDef } ';'
     */
    private ConstDecl parseConstDecl() throws Exception {
        tokenList.skip(); // skip "const"
        BType bType = parseBType();
        ArrayList<ConstDef> constDefList = new ArrayList<>();
        constDefList.add(parseConstDef());
        while (tokenList.lookingAtIsOf(Lexeme.COMMA)) {
            tokenList.skip(); //skip ','
            constDefList.add(parseConstDef());
        }
        tokenList.assertLexemeAndSkip(Lexeme.SEMICN, "Parser-ConstDecl: ;");
        ConstDecl ret = new ConstDecl(bType, constDefList);
        SyntaxOutputBuilder.appendLine("<ConstDecl>");
        return ret;
    }

    /**
     * BType ::= int
     */
    private BType parseBType() throws Exception {
        BType ret = new BType(tokenList.nextToken());
        SyntaxOutputBuilder.appendLine("<BType>");
        return ret;
    }

    /**
     * ConstDef ::= Ident { '[' ConstExp ']' } '=' ConstInitVal
     */
    private ConstDef parseConstDef() throws Exception {
        Ident ident = parseIdent();
        ArrayList<ConstExp> arrayDim = new ArrayList<>();
        while (tokenList.lookingAtIsOf(Lexeme.LBRACK)) {
            tokenList.skip(); // skip '['
            arrayDim.add(parseConstExp());
            tokenList.assertLexemeAndSkip(Lexeme.RBRACK, "Parser-ConstDef: ]");
        }
        tokenList.assertLexemeAndSkip(Lexeme.ASSIGN, "Parser-ConstDef: =");
        ConstInitVal constInitVal = parseConstInitVal();
        ConstDef ret = new ConstDef(ident, arrayDim, constInitVal);
        SyntaxOutputBuilder.appendLine("<ConstDef>");
        return ret;
    }

    /**
     * ConstInitVal → ConstExp | '{' [ ConstInitVal { ',' ConstInitVal } ] '}'
     */
    private ConstInitVal parseConstInitVal() throws Exception {
        if (tokenList.lookingAtIsOf(Lexeme.LBRACE)) {
            ConstInitVal ret = parseConstInitArray();
            SyntaxOutputBuilder.appendLine("<ConstInitVal>");
            return ret;
        } else {
            ConstInitVal ret = parseConstExp();
            SyntaxOutputBuilder.appendLine("<ConstInitVal>");
            return ret;
        }
    }

    /**
     * VarDecl ::= BType VarDef { ',' VarDef } ';'
     */
    private VarDecl parseVarDecl() throws Exception {
        BType bType = parseBType();
        ArrayList<VarDef> varDefList = new ArrayList<>();
        varDefList.add(parseVarDef());
        while (tokenList.lookingAtIsOf(Lexeme.COMMA)) {
            tokenList.skip(); // skip ','
            varDefList.add(parseVarDef());
        }
        tokenList.assertLexemeAndSkip(Lexeme.SEMICN, "Parser-VarDecl: ;");
        VarDecl ret = new VarDecl(bType, varDefList);
        SyntaxOutputBuilder.appendLine("<VarDecl>");
        return ret;
    }

    /**
     * 包含普通变量、一维数组、二维数组定义
     * VarDef ::= Ident { '[' ConstExp ']' } | Ident { '[' ConstExp ']' } '=' InitVal
     */
    private VarDef parseVarDef() throws Exception {
        Ident ident = parseIdent();
        ArrayList<ConstExp> arrayDim = new ArrayList<>();
        while (tokenList.lookingAtIsOf(Lexeme.LBRACK)) {
            tokenList.skip(); // skip '['
            arrayDim.add(parseConstExp());
            tokenList.assertLexemeAndSkip(Lexeme.RBRACK, "VarDef: ]");
        }
        InitVal initVal = null;
        if (tokenList.lookingAtIsOf(Lexeme.ASSIGN)) {
            tokenList.skip(); // skip '='
            initVal = parseInitVal();
        }
        VarDef ret = new VarDef(ident, arrayDim, initVal);
        SyntaxOutputBuilder.appendLine("<VarDef>");
        return ret;
    }

    /**
     * InitVal ::= Exp | '{' [ InitVal { ',' InitVal } ] '}'
     */
    private InitVal parseInitVal() throws Exception {
        if (tokenList.lookingAtIsOf(Lexeme.LBRACE)) {
            InitVal ret = parseInitArray();
            SyntaxOutputBuilder.appendLine("<InitVal>");
            return ret;
        } else {
            InitVal ret = parseExp();
            SyntaxOutputBuilder.appendLine("<InitVal>");
            return ret;
        }
    }

    /**
     * FuncDef ::= FuncType Ident '(' [FuncFParams] ')' Block
     */
    private FuncDef parseFuncDef() throws Exception {
        FuncType funcType = parseFuncType();
        Ident ident = parseIdent();
        tokenList.assertLexemeAndSkip(Lexeme.LPARENT, "Parser-FuncDef (");
        FuncFParams funcFParams = tokenList.lookingAtIsOf(Lexeme.INTTK) ? parseFuncFParams() : null;
        tokenList.assertLexemeAndSkip(Lexeme.RPARENT, "Parser-FuncDef )");
        Block block = parseBlock();
        FuncDef ret = new FuncDef(funcType, ident, funcFParams, block);
        SyntaxOutputBuilder.appendLine("<FuncDef>");
        return ret;
    }

    /**
     * 主函数定义 MainFuncDef ::= 'int' 'main' '(' ')' Block
     */
    private FuncDef parseMainFuncDef() throws Exception {
        FuncType funcType = new FuncType(tokenList.nextToken());
        Ident ident = parseIdent();
        tokenList.assertLexemeAndSkip(Lexeme.LPARENT, "Parser-MainFuncDef: (");
        FuncFParams funcFParams = null;
        tokenList.assertLexemeAndSkip(Lexeme.RPARENT, "Parser-MainFuncDef: )");
        Block block = parseBlock();
        FuncDef ret = new FuncDef(funcType, ident, funcFParams, block);
        SyntaxOutputBuilder.appendLine("<MainFuncDef>");
        return ret;
    }

    /**
     * FuncType ::= 'void' | 'int' // 覆盖两种类型的函数
     */
    private FuncType parseFuncType() throws Exception {
        FuncType ret = new FuncType(tokenList.nextToken());
        SyntaxOutputBuilder.appendLine("<FuncType>");
        return ret;
    }

    /**
     * FuncFParams ::= FuncFParam { ',' FuncFParam } // 1.花括号内重复0次 2.花括号内重复多次
     */
    private FuncFParams parseFuncFParams() throws Exception {
        ArrayList<FuncFParam> funcFParams = new ArrayList<>();
        funcFParams.add(parseFuncFParam());
        while (tokenList.lookingAtIsOf(Lexeme.COMMA)) {
            tokenList.skip(); // skip ','
            funcFParams.add(parseFuncFParam());
        }
        FuncFParams ret = new FuncFParams(funcFParams);
        SyntaxOutputBuilder.appendLine("<FuncFParams>");
        return ret;
    }

    /**
     * FuncFParam ::= BType Ident ['[' ']' { '[' ConstExp ']' }]
     */
    private FuncFParam parseFuncFParam() throws Exception {
        BType bType = parseBType();
        Ident ident = parseIdent();
        ArrayList<ConstExp> arrayDim = new ArrayList<>();
        if (tokenList.lookingAtIsOf(Lexeme.LBRACK)) {
            tokenList.skip(); // skip '['
            arrayDim.add(null); // 当为一位数组时，有一个null占位
            tokenList.assertLexemeAndSkip(Lexeme.RBRACK, "Parser-FuncFParam ]");
            while (tokenList.lookingAtIsOf(Lexeme.LBRACK)) {
                tokenList.skip(); // skip '['
                arrayDim.add(parseConstExp());
                tokenList.assertLexemeAndSkip(Lexeme.RBRACK, "Parser-FuncFParam ]");
            }
        }
        FuncFParam ret = new FuncFParam(bType, ident, arrayDim);
        SyntaxOutputBuilder.appendLine("<FuncFParam>");
        return ret;
    }

    /**
     * 语句块 Block ::= '{' { BlockItem } '}'
     */
    private Block parseBlock() throws Exception {
        tokenList.skip(); // skip "{"
        ArrayList<BlockItem> blockItems = new ArrayList<>();
        while (!tokenList.lookingAtIsOf(Lexeme.RBRACE)) {
            blockItems.add(parseBlockItem());
        }
        int rbraceLineNum = tokenList.lookingAt().getLineNum();
        tokenList.assertLexemeAndSkip(Lexeme.RBRACE, "Parser-Block: }");
        Block ret = new Block(blockItems, rbraceLineNum);
        SyntaxOutputBuilder.appendLine("<Block>");
        return ret;
    }

    /**
     * 语句块项 BlockItem → Decl | Stmt
     */
    private BlockItem parseBlockItem() throws Exception {
        if (tokenList.lookingAtIsOf(Lexeme.CONSTTK, Lexeme.INTTK)) {
            BlockItem ret = parseDecl();
            SyntaxOutputBuilder.appendLine("<BlockItem>");
            return ret;
        } else {
            BlockItem ret = parseStmt();
            SyntaxOutputBuilder.appendLine("<BlockItem>");
            return ret;
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
    private Stmt parseStmt() throws Exception {
        Stmt ret;
        Lexeme lookingAt = tokenList.lookingAt().getLexeme();
        switch (lookingAt) {
            case LBRACE -> {
                ret = parseBlock();
                SyntaxOutputBuilder.appendLine("<Stmt>");
                return ret;
            }
            case IFTK -> {
                ret = parseIfStmt();
                SyntaxOutputBuilder.appendLine("<Stmt>");
                return ret;
            }
            case FORTK, WHILETK -> {
                ret = parseLoopStmt();
                SyntaxOutputBuilder.appendLine("<Stmt>");
                return ret;
            }
            case BREAKTK -> {
                int lineNum = tokenList.nextToken().getLineNum(); // skip "break"
                tokenList.assertLexemeAndSkip(Lexeme.SEMICN, "Parser-BreakStmt: ;");
                ret = new BreakStmt(lineNum);
                SyntaxOutputBuilder.appendLine("<Stmt>");
                return ret;
            }
            case CONTINUETK -> {
                int lineNum = tokenList.nextToken().getLineNum(); // skip "continue"
                tokenList.assertLexemeAndSkip(Lexeme.SEMICN, "Parser-ContinueStmt: ;");
                ret = new ContinueStmt(lineNum);
                SyntaxOutputBuilder.appendLine("<Stmt>");
                return ret;
            }
            case RETURNTK -> {
                int lineNum = tokenList.nextToken().getLineNum(); // skip "return"
                Exp returnExp = tokenList.lookingAtExp() ? parseExp() : null;
                tokenList.assertLexemeAndSkip(Lexeme.SEMICN, "Parser-ReturnStmt: ;");
                ret = new ReturnStmt(returnExp, lineNum);
                SyntaxOutputBuilder.appendLine("<Stmt>");
                return ret;
            }
            case PRINTFTK -> {
                ret = parsePrintfStmt();
                SyntaxOutputBuilder.appendLine("<Stmt>");
                return ret;
            }
            case IDENFR -> {
                // AssignStmt or ExpStmt
                if (tokenList.isAssignStmt()) {
                    ret = parseAssignStmt();
                    SyntaxOutputBuilder.appendLine("<Stmt>");
                    return ret;
                }
                ret = parseExpStmt();
                tokenList.assertLexemeAndSkip(Lexeme.SEMICN, "Parser-ExpStmt: ;");
                SyntaxOutputBuilder.appendLine("<Stmt>");
                return ret;
            }
            case SEMICN -> {
                tokenList.skip(); // skip ';'
                ret = new ExpStmt(null);
                SyntaxOutputBuilder.appendLine("<Stmt>");
                return ret;
            }
            default -> {
                // [Exp] ';'  这里的<Stmt>实际上应该让SEMICN输出
                Exp exp = parseExp();
                ret = new ExpStmt(exp);
                // SyntaxOutputBuilder.appendLine("<Stmt>");
                return ret;
            }
        }
    }

    /**
     * 语句 ForStmt → LVal '=' Exp // 存在即可
     */
    private ForStmt parseForStmt() throws Exception {
        LVal lVal = parseLVal();
        tokenList.assertLexemeAndSkip(Lexeme.ASSIGN, "Parser-ForStmt: =");
        Exp exp = parseExp();
        ForStmt ret = new ForStmt(lVal, exp);
        SyntaxOutputBuilder.appendLine("<ForStmt>");
        return ret;
    }

    /**
     * Exp ::= AddExp
     */
    private Exp parseExp() throws Exception {
        Exp ret = parseAddExp();
        SyntaxOutputBuilder.appendLine("<Exp>");
        return ret;
    }

    /**
     * <额外语法>
     * 条件表达式 Cond → LOrExp
     */
    private Cond parseCond() throws Exception {
        BinaryExp binaryExp = parseBinaryExp(BinaryOperate.LOR);
        Cond ret = new Cond(binaryExp);
        SyntaxOutputBuilder.appendLine("<Cond>");
        return ret;
    }

    /**
     * 左值表达式 LVal → Ident {'[' Exp ']'}
     */
    private LVal parseLVal() throws Exception {
        int lineNum = tokenList.lookingAt().getLineNum();
        Ident ident = parseIdent();
        ArrayList<Exp> arrayDim = new ArrayList<>();
        while (tokenList.lookingAtIsOf(Lexeme.LBRACK)) {
            tokenList.skip(); // skip '['
            arrayDim.add(parseExp());
            tokenList.assertLexemeAndSkip(Lexeme.RBRACK, "Parser-LVal ]");
        }
        LVal ret = new LVal(ident, arrayDim, lineNum);
        SyntaxOutputBuilder.appendLine("<LVal>");
        return ret;
    }


    /**
     * 数值 Number ::= IntConst
     */
    private Number parseNumber() {
        Number ret = new Number(tokenList.nextToken());
        SyntaxOutputBuilder.appendLine("<Number>");
        return ret;
    }

    /**
     * 函数实参表 FuncRParams → Exp { ',' Exp }
     */
    private FuncRParams parseFuncRParams() throws Exception {
        ArrayList<Exp> exps = new ArrayList<>();
        exps.add(parseExp());
        while (tokenList.lookingAtIsOf(Lexeme.COMMA)) {
            tokenList.skip(); // skip ','
            exps.add(parseExp());
        }
        FuncRParams ret = new FuncRParams(exps);
        SyntaxOutputBuilder.appendLine("<FuncRParams>");
        return ret;
    }

    /**
     * 常量表达式 ConstExp ::= AddExp
     */
    private ConstExp parseConstExp() throws Exception {
        ConstExp ret = parseAddExp();
        SyntaxOutputBuilder.appendLine("<ConstExp>");
        return ret;
    }

    /**
     * <额外语法>
     * MulExp | AddExp ('+' | '−') MulExp
     */
    private AddExp parseAddExp() throws Exception {
        BinaryExp binaryExp = parseBinaryExp(BinaryOperate.ADD);
        return new AddExp(binaryExp);
    }

    /**
     * <额外语法>
     */
    private BinaryExp parseBinaryExp(BinaryOperate binaryOperate) throws Exception {
        Exp first = parseSubBinaryExp(binaryOperate);
        SyntaxOutputBuilder.appendLine(binaryOperate.getExpSyntax());
        ArrayList<Token> operators = new ArrayList<>();
        ArrayList<Exp> follows = new ArrayList<>();
        while (tokenList.hasNext() && binaryOperate.contains(tokenList.lookingAt().getLexeme())) {
            operators.add(tokenList.nextToken());
            follows.add(parseSubBinaryExp(binaryOperate));
            SyntaxOutputBuilder.appendLine(binaryOperate.getExpSyntax());
        }
        return new BinaryExp(first, operators, follows);
    }

    /**
     * <额外语法>
     */
    private Exp parseSubBinaryExp(BinaryOperate binaryOperate) throws Exception {
        return switch (binaryOperate) {
            case LOR -> parseBinaryExp(BinaryOperate.LAND);
            case LAND -> parseBinaryExp(BinaryOperate.EQ);
            case EQ -> parseBinaryExp(BinaryOperate.REL);
            case REL -> parseBinaryExp(BinaryOperate.ADD);
            case ADD -> parseBinaryExp(BinaryOperate.MUL);
            case MUL -> parseUnaryExp();
        };
    }

    /**
     * <额外语法>
     */
    private UnaryExp parseUnaryExp() throws Exception {
        ArrayList<Token> operators = new ArrayList<>();
        while (tokenList.lookingAt().getLexeme().isOf(Lexeme.PLUS, Lexeme.MINU, Lexeme.NOT)) {
            operators.add(tokenList.nextToken());
            SyntaxOutputBuilder.appendLine("<UnaryOp>");
        }
        PrimaryExp primaryExp = parsePrimaryExp();
        for (int i = 0; i < operators.size() + 1; i++) {
            SyntaxOutputBuilder.appendLine("<UnaryExp>");
        }
        return new UnaryExp(operators, primaryExp);
    }

    /**
     * <额外语法>
     */
    private PrimaryExp parsePrimaryExp() throws Exception {
        Lexeme lookingAt = tokenList.lookingAt().getLexeme();
        if (lookingAt.isOf(Lexeme.LPARENT)) {
            tokenList.skip(); // skip '('
            Exp exp = parseExp();
            tokenList.assertLexemeAndSkip(Lexeme.RPARENT, "Parser-PrimaryExp: )");
            SyntaxOutputBuilder.appendLine("<PrimaryExp>");
            return exp;
        } else if (lookingAt.isOf(Lexeme.INTCON)) {
            PrimaryExp ret = parseNumber();
            SyntaxOutputBuilder.appendLine("<PrimaryExp>");
            return ret;
        } else if (tokenList.lookingAtFuncCall()) {
            PrimaryExp ret = parseFuncCall();
            // SyntaxOutputBuilder.appendLine("<PrimaryExp>");
            return ret;
        } else {
            PrimaryExp ret = parseLVal();
            SyntaxOutputBuilder.appendLine("<PrimaryExp>");
            return ret;
        }
    }

    /**
     * <额外语法>
     */
    private FuncCall parseFuncCall() throws Exception {
        Ident ident = parseIdent();
        tokenList.assertLexemeAndSkip(Lexeme.LPARENT, "Parser-FuncCall: (");
        FuncRParams funcRParams = tokenList.lookingAtExp() ? parseFuncRParams() : null;
        tokenList.assertLexemeAndSkip(Lexeme.RPARENT, "Parser-FuncCall: )");
        return new FuncCall(ident, funcRParams);
    }


    /**
     * <额外语法>
     * Stmt ::= 'if' '(' Cond ')' Stmt [ 'else' Stmt ]
     */
    private IfStmt parseIfStmt() throws Exception {
        tokenList.skip(); // skip "if"
        tokenList.assertLexemeAndSkip(Lexeme.LPARENT, "Parser-IfStmt: (");
        Cond cond = parseCond();
        tokenList.assertLexemeAndSkip(Lexeme.RPARENT, "Parser-IfStmt: (");
        Stmt thenStmt = parseStmt();
        Stmt elseStmt = null;
        if (tokenList.lookingAtIsOf(Lexeme.ELSETK)) {
            tokenList.skip(); // skip "else"
            elseStmt = parseStmt();
        }
        return new IfStmt(cond, thenStmt, elseStmt);
    }

    /**
     * <额外语法>
     * 'while' '(' [Cond] ')' Stmt
     * 'for' '(' [ForStmt] ';' [Cond] ';' [ForStmt] ')' Stmt   // 1. 无缺省 2. 缺省第一个ForStmt 3. 缺省Cond 4. 缺省第二个ForStmt
     */
    private LoopStmt parseLoopStmt() throws Exception {
        if (tokenList.lookingAtIsOf(Lexeme.WHILETK)) {
            tokenList.skip(); // skip "while"
            Cond cond = tokenList.lookingAtExp() ? parseCond() : null;
            tokenList.assertLexemeAndSkip(Lexeme.SEMICN, "Parser-WhileStmt: second ;");
            return new LoopStmt(cond, parseStmt());
        }
        tokenList.assertLexemeAndSkip(Lexeme.FORTK, "Parser-ForStmt: for or while");
        tokenList.assertLexemeAndSkip(Lexeme.LPARENT, "Parser-ForStmt: (");
        ForStmt initStmt = tokenList.lookingAtExp() ? parseForStmt() : null;
        tokenList.assertLexemeAndSkip(Lexeme.SEMICN, "Parser-ForStmt: first ;");
        Cond cond = tokenList.lookingAtExp() ? parseCond() : null;
        tokenList.assertLexemeAndSkip(Lexeme.SEMICN, "Parser-ForStmt: second ;");
        ForStmt continueStmt = tokenList.lookingAtExp() ? parseForStmt() : null;
        tokenList.assertLexemeAndSkip(Lexeme.RPARENT, "Parser-ForStmt: )");
        Stmt body = parseStmt();
        return new LoopStmt(initStmt, cond, continueStmt, body);
    }

    /**
     * <额外语法>
     * 语句 AssignStmt → LVal '=' ( Exp | getint '(' ')' ) ';'
     */
    private AssignStmt parseAssignStmt() throws Exception {
        LVal lVal = parseLVal();
        tokenList.assertLexemeAndSkip(Lexeme.ASSIGN, "Parser-AssignStmt: =");
        Exp exp = tokenList.lookingAtIsOf(Lexeme.GETINTTK) ? parseGetIntStmt() : parseExp();
        tokenList.assertLexemeAndSkip(Lexeme.SEMICN, "Parser-AssignStmt: ;");
        return new AssignStmt(lVal, exp);
    }

    /**
     * <额外语法>
     * 真的是太丑陋了，实际上getint()和printf()应该归为expression比较合适
     */
    private GetIntStmt parseGetIntStmt() throws Exception {
        tokenList.assertLexemeAndSkip(Lexeme.GETINTTK, "Parser-GetIntStmt: getint");
        tokenList.assertLexemeAndSkip(Lexeme.LPARENT, "Parser-GetIntStmt: (");
        tokenList.assertLexemeAndSkip(Lexeme.RPARENT, "Parser-GetIntStmt: )");
        return new GetIntStmt();
    }

    /**
     * <额外语法>
     * 'printf' '(' FormatString {',' Exp } ')' ';'    1.有Exp 2.无Exp
     */
    private PrintfStmt parsePrintfStmt() throws Exception {
        int lineNum = tokenList.lookingAt().getLineNum();
        tokenList.assertLexemeAndSkip(Lexeme.PRINTFTK, "Parser-PrintfStmt: printf");
        tokenList.assertLexemeAndSkip(Lexeme.LPARENT, "Parser-PrintfStmt: (");
        FormatString formatString = parseFormatString();
        ArrayList<Exp> arguments = new ArrayList<>();
        if (tokenList.lookingAtIsOf(Lexeme.COMMA)) {
            while (tokenList.lookingAtIsOf(Lexeme.COMMA)) {
                tokenList.assertLexemeAndSkip(Lexeme.COMMA, "Parser-PrintfStmt: ,");
                arguments.add(parseExp());
            }
        }
        tokenList.assertLexemeAndSkip(Lexeme.RPARENT, "Parser-PrintfStmt: )");
        tokenList.assertLexemeAndSkip(Lexeme.SEMICN, "Parser-PrintfStmt: ;");
        return new PrintfStmt(formatString, arguments, lineNum);
    }

    /**
     * <额外语法>
     */
    private ExpStmt parseExpStmt() throws Exception {
        Exp exp = parseExp();
        return new ExpStmt(exp);
    }


    /**
     * <额外语法>
     * FormatString ::= String
     */
    private FormatString parseFormatString() throws Exception {
        tokenList.assertLexeme(Lexeme.STRCON, "Parser-FormatString: string");
        return new FormatString(tokenList.nextToken().getContent());
    }

    /**
     * <额外语法>
     * Ident ::= 非keyword的字符串
     */
    private Ident parseIdent() {
        return new Ident(tokenList.nextToken());
    }

    /**
     * <额外语法>
     * InitArray ::= '{' [ InitVal { ',' InitVal } ] '}'
     */
    private ConstInitArray parseConstInitArray() throws Exception {
        tokenList.assertLexeme(Lexeme.LBRACE, "Parser-ConstInitArray: {");
        tokenList.skip(); // skip '{'
        ArrayList<ConstInitVal> constInitVals = new ArrayList<>();
        if (!tokenList.lookingAtIsOf(Lexeme.RBRACE)) {
            constInitVals.add(parseConstInitVal());
            while (tokenList.lookingAtIsOf(Lexeme.COMMA)) {
                tokenList.skip(); // skip ','
                constInitVals.add(parseConstInitVal());
            }
        }
        tokenList.assertLexemeAndSkip(Lexeme.RBRACE, "Parser-ConstInitArray: }");
        return new ConstInitArray(constInitVals);
    }

    /**
     * <额外语法>
     * InitArray ::= '{' [ InitVal { ',' InitVal } ] '}'
     */
    private InitArray parseInitArray() throws Exception {
        tokenList.assertLexeme(Lexeme.LBRACE, "Parser-InitArray: {");
        tokenList.skip(); // skip '{'
        ArrayList<InitVal> initVals = new ArrayList<>();
        if (!tokenList.lookingAtIsOf(Lexeme.RBRACE)) {
            initVals.add(parseInitVal());
            while (tokenList.lookingAtIsOf(Lexeme.COMMA)) {
                tokenList.skip(); // skip ','
                initVals.add(parseInitVal());
            }
        }
        tokenList.assertLexemeAndSkip(Lexeme.RBRACE, "Parser-InitArray: }");
        return new InitArray(initVals);
    }

}
