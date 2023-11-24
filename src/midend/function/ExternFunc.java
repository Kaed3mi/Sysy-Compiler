package midend.function;

import midend.llvm_type.LLvmType;

import java.util.Arrays;

public class ExternFunc extends Function {


    public ExternFunc(LLvmType retType, String funcName, LLvmType... funcFParams) {
        super(retType, funcName);
        Arrays.stream(funcFParams).forEach(e -> this.addFunctionFParam(new FunctionFParam(e)));
    }

    public static ExternFunc GET_INT;
    public static ExternFunc PRINTF;
    public static ExternFunc PUT_INT;
    public static ExternFunc PUT_CH;

    static {
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
