package frontend.syntax.ast.function;

import frontend.lexical.Ident;
import frontend.syntax.ast.expression.PrimaryExp;

public class FuncCall implements PrimaryExp {
    private Ident ident;
    private FuncRParams funcRParams;

    public FuncCall(Ident ident, FuncRParams funcRParams) {
        this.ident = ident;
        this.funcRParams = funcRParams;
    }

    public Ident getIdent() {
        return ident;
    }

    public FuncRParams getFuncRParams() {
        return funcRParams;
    }
}
