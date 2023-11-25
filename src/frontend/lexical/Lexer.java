package frontend.lexical;

import exceptions.CompileError;
import exceptions.ErrorBuilder;
import exceptions.ErrorType;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Lexer {

    private final String sourceCode;
    private int curLine;

    public Lexer(String sourceCode) {
        this.sourceCode = sourceCode;
        this.curLine = 1;
    }

    public TokenList lex() {
        TokenList tokenList = new TokenList();
        String currentSourceCode = sourceCode.trim();
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
                // 格式字符串中出现非法字符
                Pattern pattern = Pattern.compile("(?<content>" + "\"(.*?)\"" + ")");
                Matcher matcher = pattern.matcher(currentSourceCode);
                if (matcher.lookingAt()) {
                    curLine = sourceCode.split("\n").length - currentSourceCode.split("\n").length + 1;
                    String content = matcher.group("content");
                    tokenList.append(new Token(Lexeme.STRCON, content, curLine));
                    currentSourceCode = currentSourceCode.substring(content.length()).trim();
                    ErrorBuilder.appendError(new CompileError(curLine, ErrorType.ILLEGAL_CHAR, "格式字符串中出现非法字符: " + content));
                } else {
                    // 你怎么什么都匹配不上
                    throw new RuntimeException("Lexer: 你怎么什么都匹配不上" + sourceCode);
                }
            }
        }
        return tokenList;
    }

}
