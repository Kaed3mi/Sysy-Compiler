package frontend.syntax.ast.function;

import frontend.lexical.Token;

public class FuncType {

    public enum DefType {
        INT, VOID;
    }

    private final DefType defType;

    public FuncType(Token token) throws Exception {
        defType = switch (token.getLexeme()) {
            case INTTK -> DefType.INT;
            case VOIDTK -> DefType.VOID;
            default -> throw new Exception("函数声明缺少返回值");
        };
    }

    public boolean returnsInt() {
        return defType.equals(DefType.INT);
    }
}
