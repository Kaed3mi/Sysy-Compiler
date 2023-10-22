package frontend.syntax.ast.expression;

public class FormatString implements Exp {

    private String string;

    public FormatString(String string) {
        this.string = string;
    }

    @Override
    public String toString() {
        return string;
    }
}
