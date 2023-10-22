package frontend.semantic;

import exceptions.CompileError;
import exceptions.ErrorBuilder;
import exceptions.ErrorType;
import frontend.lexical.Ident;
import frontend.lexical.Lexeme;
import frontend.lexical.Token;
import frontend.syntax.ast.function.FuncDef;
import frontend.syntax.ast.function.FuncType;
import midend.Function;

import java.util.HashMap;
import java.util.HashSet;

public class FuncTable {
    private final HashMap<Ident, Function> functionTable;
    private final HashSet<Ident> brokenFunctionTable;

    public FuncTable() throws Exception {
        this.functionTable = new HashMap<>();
        this.brokenFunctionTable = new HashSet<>();
        init();
    }

    private void init() throws Exception {
        Token intToken = new Token(Lexeme.INTTK, "int", -1);
        Token voidToken = new Token(Lexeme.VOIDTK, "void", -1);
        Token getIntToken = new Token(Lexeme.GETINTTK, "getint", -1);
        Ident getIntIdent = new Ident(getIntToken);
        Function getIntFunction = new Function(new FuncDef(new FuncType(intToken), getIntIdent, null, null));
        functionTable.put(getIntIdent, getIntFunction);
        Token printfToken = new Token(Lexeme.PRINTFTK, "printf", -1);
        Ident printfIdent = new Ident(printfToken);
        Function printfFunction = new Function(new FuncDef(new FuncType(voidToken), printfIdent, null, null));
        functionTable.put(printfIdent, printfFunction);
    }

    public void append(Function function) {
        functionTable.put(function.getIdent(), function);
    }

    public boolean isDuplicated(Ident ident) {
        return functionTable.containsKey(ident);
    }

    public Function get(Ident ident) {
        if (!functionTable.containsKey(ident) || brokenFunctionTable.contains(ident)) {
            ErrorBuilder.appendError(new CompileError(ident.getLineNum(), ErrorType.UNDEFINED_IDENT, "没有定义函数"));
            return null;
        }
        return functionTable.get(ident);
    }

    public void addBrokenFunction(Ident ident) {
        brokenFunctionTable.add(ident);
    }

    public boolean isBroken(Ident ident) {
        return brokenFunctionTable.contains(ident);
    }
}
