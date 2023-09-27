package frontend.syntax.ast.statement;

import frontend.syntax.ast.expression.Exp;

public class Return implements Stmt {
    private Exp exp;

    public Return(Exp exp) {
        this.exp = exp;
    }
}
