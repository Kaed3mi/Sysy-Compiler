package frontend.lexical;

import java.util.regex.Pattern;

public enum Lexeme {
    // 注释
    COMMENT_SINGLE_LINE("(?<content>//.*)"),
    COMMENT_BLOCK("(?<content>\\/\\*([\\s\\S]*?)\\*\\/)"),
    INTCON("\\d+"),
    STRCON("\"(.*?)\""),
    MAINTK("(?<content>main)[^\\w]"),
    CONSTTK("(?<content>const)[^\\w]"),
    INTTK("(?<content>int)[^\\w]"),
    BREAKTK("(?<content>break)[^\\w]"),
    CONTINUETK("(?<content>continue)[^\\w]"),
    IFTK("(?<content>if)[^\\w]"),
    ELSETK("(?<content>else)[^\\w]"),
    NOT("(?<content>!)[^=]"),
    AND("&&"),
    OR("\\|\\|"),
    FORTK("(?<content>for)[^\\w]"),
    WHILETK("(?<content>while)[^\\w]"),
    GETINTTK("(?<content>getint)[^\\w]"),
    PRINTFTK("(?<content>printf)[^\\w]"),
    RETURNTK("(?<content>return)[^\\w]"),
    PLUS("\\+"),
    MINU("\\-"),
    VOIDTK("(?<content>void)[^\\w]"),
    MULT("\\*"),
    DIV("(?<content>/)[^/]"),
    MOD("%"),
    LSS("(?<content><)[^=]"),
    LEQ("<="),
    GRE("(?<content>>)[^=]"),
    GEQ(">="),
    EQL("=="),
    NEQ("!="),
    ASSIGN("(?<content>=)[^=]"),
    SEMICN(";"),
    COMMA(","),
    LPARENT("\\("),
    RPARENT("\\)"),
    LBRACK("\\["),
    RBRACK("\\]"),
    LBRACE("\\{"),
    RBRACE("}"),
    IDENFR("(?<content>[A-Za-z_][\\w]*)[^\\w]");
    public final Pattern pattern;

    Lexeme(String regex) {
        if (regex.startsWith("(?<content>")) {
            this.pattern = Pattern.compile(regex);
        } else {
            this.pattern = Pattern.compile("(?<content>" + regex + ")");
        }
    }

    @Override
    public String toString() {
        return name();
    }

    public Pattern getPattern() {
        return pattern;
    }
}
