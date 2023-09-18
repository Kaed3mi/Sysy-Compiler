package frontend.lexical;

public class Token {
    private final Lexeme lexeme;
    private final String content;

    public Token(Lexeme lexeme, String content) {
        this.lexeme = lexeme;
        this.content = content;
    }

    @Override
    public String toString() {
        return String.format("%s %s", lexeme, content);
    }
}
