package frontend.lexical;

import java.util.regex.Matcher;

public class Lexer {
    public static int curLine = 1;

    public static TokenList lex(String sourceCode) throws Exception {
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
