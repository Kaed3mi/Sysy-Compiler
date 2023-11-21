package frontend.lexical;

import java.util.List;

public enum BinaryOperator {

    LOR(Lexeme.OR),
    LAND(Lexeme.AND),
    EQ(Lexeme.EQL, Lexeme.NEQ),
    REL(Lexeme.GRE, Lexeme.LSS, Lexeme.GEQ, Lexeme.LEQ),
    ADD(Lexeme.PLUS, Lexeme.MINU),
    MUL(Lexeme.MULT, Lexeme.DIV, Lexeme.MOD);
    private final List<Lexeme> lexemes;

    BinaryOperator(Lexeme... lexemes) {
        this.lexemes = List.of(lexemes);
    }

    public boolean contains(Lexeme lexeme) {
        return lexemes.contains(lexeme);
    }

    public String getExpSyntax() {
        String s = switch (this) {
            case LOR -> "LOrExp";
            case LAND -> "LAndExp";
            case EQ -> "EqExp";
            case REL -> "RelExp";
            case ADD -> "AddExp";
            case MUL -> "MulExp";
        };
        return "<" + s + ">";
    }
}
