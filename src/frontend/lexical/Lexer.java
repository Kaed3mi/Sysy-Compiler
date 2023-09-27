package frontend.lexical;

import java.util.regex.Matcher;

public class Lexer {

    private final String sourceCode;
    private int curLine;

    public Lexer(String sourceCode) {
        this.sourceCode = sourceCode;
        this.curLine = 1;
    }

    public TokenList lex() throws Exception {
        TokenList tokenList = new TokenList();
        String currentSourceCode = sourceCode;
        while (!currentSourceCode.isEmpty()) {
            boolean legalLexeme = false;
            for (Lexeme lexeme : Lexeme.values()) {
                Matcher matcher = lexeme.getPattern().matcher(currentSourceCode);
                if (matcher.lookingAt()) {
                    String content = matcher.group("content");
                    curLine = sourceCode.split("\n").length - currentSourceCode.split("\n").length + 1;
                    tokenList.append(new Token(lexeme, content, curLine));

                    currentSourceCode = currentSourceCode.substring(content.length()).trim();
                    legalLexeme = true;
                    break;
                }
            }
            if (!legalLexeme) {
                throw new Exception('\n' + tokenList.toString());
            }
        }
        return tokenList;
    }

}
