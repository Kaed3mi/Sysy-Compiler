package frontend.syntax.ast;

import frontend.lexical.Lexeme;
import frontend.lexical.Token;
import midend.llvm_type.LLvmType;

public class BType {
    public enum Type {
        INT,
    }

    private final Type type;


    public BType(Token token) throws Exception {
        if (token.getLexeme().isOf(Lexeme.INTTK)) {
            type = Type.INT;
        } else {
            throw new Exception("BType类型错误");
        }
    }

    public LLvmType toLLvmType() throws Exception {
        if (type.equals(Type.INT)) {
            return LLvmType.I32_TYPE;
        } else {
            throw new Exception("BType转LLvm类型类型错误");
        }
    }
}
