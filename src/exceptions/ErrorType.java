package exceptions;

// Lexical Error    词法错误: a
// Syntax Error     语法错误: i, j, k
// Semantic Error   语义错误: b, c, d, e, f, g, h, l, m
public enum ErrorType {

    ILLEGAL_CHAR("a"),
    DUPLICATED_IDENT("b"),
    UNDEFINED_IDENT("c"),
    WRONG_ARGUMENTS_AMOUNT("d"),
    WRONG_ARGUMENTS_TYPE("e"),
    EXCESS_RETURN("f"),
    MISSING_RETURN("g"),
    READ_ONLY("h"),
    MISSING_SEMICOLON("i"),
    MISSING_RIGHT_PARENT("j"),
    MISSING_RIGHT_BRACKET("k"),
    MISMATCH_PRINTF("l"),
    WITHOUT_LOOP("m"),
    ;

    public final String code;

    ErrorType(String code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return code;
    }
}