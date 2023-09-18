package frontend.lexical;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
                    tokenList.append(new Token(lexeme, content, curLine));

                    currentSourceCode = currentSourceCode.substring(content.length());
                    curLine += leadingNewLineCharNum(currentSourceCode);
                    currentSourceCode = currentSourceCode.trim();
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

    /**
     * @return 字符串开头空白字符串中换行符的数目
     */
    public static int leadingNewLineCharNum(String string) {
        String regex = "(?<content>[(\\s)]*)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(string);
        if (matcher.lookingAt()) {
            String str = matcher.group("content");
            return str.length() - str.replaceAll("\n", "").length();
        }
        return 0;
    }
}
