package frontend.syntax.ast.function;

import frontend.lexical.Token;
import midend.llvm_type.LLvmType;

public class FuncType {

    public enum DefType {
        INT, VOID;
    }

    private final DefType defType;

    public FuncType(Token token) {
        defType = switch (token.getLexeme()) {
            case INTTK -> DefType.INT;
            case VOIDTK -> DefType.VOID;
            default -> throw new RuntimeException("函数声明缺少返回值");
        };
    }

    public LLvmType toLLvmType() {
        return switch (defType) {
            case INT -> LLvmType.I32_TYPE;
            case VOID -> LLvmType.VOID_TYPE;
        };
    }

    public boolean returnsInt() {
        return defType.equals(DefType.INT);
    }
}
