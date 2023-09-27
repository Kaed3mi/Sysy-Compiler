package frontend.syntax.ast;

import exceptions.SyntaxException;
import frontend.lexical.Lexeme;
import frontend.lexical.Token;

public class BType {
    public enum Type {
        INT;
    }

    private final Type type;

    public BType(Token token) throws SyntaxException {
        if (token.getLexeme().isOf(Lexeme.INTTK)) {
            type = Type.INT;
        } else {
            throw new SyntaxException("BType类型错误");
        }
    }
}
