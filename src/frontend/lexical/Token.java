package frontend.lexical;

public class Token {
    private final Lexeme lexeme;
    private final String content;
    private final int lineNum;

    public Token(Lexeme lexeme, String content, int lineNum) {
        this.lexeme = lexeme;
        this.content = content;
        this.lineNum = lineNum;
    }

    public Lexeme getLexeme() {
        return lexeme;
    }

    public String getContent() {
        return content;
    }

    /**
     * @return Token所在的源代码行数
     */
    public int getLineNum() {
        return lineNum;
    }

    public boolean isComment() {
        return lexeme.equals(Lexeme.COMMENT_BLOCK) || lexeme.equals(Lexeme.COMMENT_SINGLE_LINE);
    }

//    public InstrOp toInstrOp() {
//        return switch (lexeme) {
//            case PLUS -> InstrOp.ADD;
//            case MINU -> InstrOp.SUB;
//            case MULT -> InstrOp.MUL;
//            case DIV -> InstrOp.DIV;
//            default -> throw new RuntimeException("没你的事儿，去等通知");
//        };
//    }

    @Override
    public String toString() {
        return String.format("%s %s", lexeme, content);
    }
}
