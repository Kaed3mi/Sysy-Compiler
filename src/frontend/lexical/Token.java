package frontend.lexical;

import exceptions.SyntaxException;

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

    /**
     * @param lexeme  确保的Lexeme类型
     * @param message 异常输出
     */
    public void assertLexeme(Lexeme lexeme, String message) throws Exception {
        if (!this.lexeme.equals(lexeme)) {
            throw new SyntaxException(message);
        }
    }

    /**
     * @param lexeme 确保的Lexeme类型
     */
    public void assertLexeme(Lexeme lexeme) throws Exception {
        if (!this.lexeme.equals(lexeme)) {
            throw new SyntaxException();
        }
    }

    public boolean isComment() {
        return lexeme.equals(Lexeme.COMMENT_BLOCK) || lexeme.equals(Lexeme.COMMENT_SINGLE_LINE);
    }

    @Override
    public String toString() {
        return String.format("%s %s", lexeme, content);
    }
}
