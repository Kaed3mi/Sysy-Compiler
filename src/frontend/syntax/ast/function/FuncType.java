package frontend.syntax.ast.function;

import exceptions.SyntaxException;
import frontend.lexical.Token;

public class FuncType {

    private enum DefType {
        INT, VOID;
    }

    private final DefType defType;

    public FuncType(Token token) throws Exception {
        defType = switch (token.getLexeme()) {
            case INTTK -> DefType.INT;
            case VOIDTK -> DefType.VOID;
            default -> throw new SyntaxException("函数声明缺少返回值");
        };
    }

    public boolean returnInt() {
        return defType.equals(DefType.INT);
    }
}
