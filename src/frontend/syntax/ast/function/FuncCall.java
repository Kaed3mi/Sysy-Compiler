package frontend.syntax.ast.function;

import frontend.syntax.ast.expression.PrimaryExp;

public class FuncCall implements PrimaryExp {
    private String ident;
    private FuncRParams funcRParams;

    public FuncCall(String ident, FuncRParams funcRParams) {
        this.ident = ident;
        this.funcRParams = funcRParams;
    }
}
