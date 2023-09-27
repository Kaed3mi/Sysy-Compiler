package frontend.syntax.ast.expression;

public class FormatString implements Exp {
    public String string;

    public FormatString(String string) {
        this.string = string;
    }
}
