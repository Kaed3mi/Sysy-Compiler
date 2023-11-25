package frontend.syntax.ast;

import frontend.lexical.Lexeme;
import frontend.lexical.Token;
import midend.llvm_type.LLvmType;

public class BType {
    public enum Type {
        INT,
    }

    private final Type type;


    public BType(Token token) {
        if (token.getLexeme().isOf(Lexeme.INTTK)) {
            type = Type.INT;
        } else {
            throw new RuntimeException("BType类型错误");
        }
    }

    public LLvmType toLLvmType() {
        if (type.equals(Type.INT)) {
            return LLvmType.I32_TYPE;
        } else {
            throw new RuntimeException("BType转LLvm类型类型错误");
        }
    }
}
