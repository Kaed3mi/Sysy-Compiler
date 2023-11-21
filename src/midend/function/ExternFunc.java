package midend.function;

import frontend.lexical.Ident;
import frontend.lexical.Lexeme;
import frontend.lexical.Token;
import midend.llvm_type.LLvmType;

import java.util.Arrays;

public class ExternFunc extends Function {

//    public ExternFunc(FuncDef funcDef) {
//        super(funcDef);
//    }

    public ExternFunc(LLvmType retType, String funcName, LLvmType... funcFParams) {
        super(retType, funcName);
        Arrays.stream(funcFParams).forEach(e -> this.addFunctionFParam(new FunctionFParam(e)));
    }

    // Token
    private static final Token intToken = new Token(Lexeme.INTTK, "int", -1);
    private static final Token voidToken = new Token(Lexeme.VOIDTK, "void", -1);
    private static final Token getIntToken = new Token(Lexeme.GETINTTK, "getint", -1);
    private static final Token printfToken = new Token(Lexeme.PRINTFTK, "printf", -1);
    private static final Token putChToken = new Token(null, "putch", -1);
    // Ident
    private static final Ident getIntIdent = new Ident(getIntToken);
    private static final Ident printfIdent = new Ident(printfToken);
    private static final Ident putChIdent = new Ident(putChToken);
    // ExternFunction
    public static ExternFunc GET_INT;
    public static ExternFunc PRINTF;
    public static ExternFunc PUT_INT;
    public static ExternFunc PUT_CH;

    static {
//        getIntFunction = new ExternFunc(new FuncDef(new FuncType(intToken), getIntIdent, null, null));
//        printfFunction = new ExternFunc(new FuncDef(new FuncType(voidToken), printfIdent, null, null));
        GET_INT = new ExternFunc(LLvmType.I32_TYPE, "getint");
        PRINTF = new ExternFunc(LLvmType.VOID_TYPE, "printf");

        PUT_INT = new ExternFunc(LLvmType.VOID_TYPE, "putint", LLvmType.I32_TYPE);
        PUT_CH = new ExternFunc(LLvmType.VOID_TYPE, "putch", LLvmType.I32_TYPE);
    }

    private String functionFParasToString() {
        if (functionFParams.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        functionFParams.forEach(e -> sb.append(e.lLvmType()).append(", "));
        sb.deleteCharAt(sb.length() - 1);
        sb.deleteCharAt(sb.length() - 1);
        return sb.toString();
    }

    @Override
    public String toString() {
        return String.format(
                "declare %s %s(%s)",
                lLvmType, lLvmIdent, functionFParasToString());
    }
}
