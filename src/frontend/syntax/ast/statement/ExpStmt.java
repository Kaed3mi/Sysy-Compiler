package frontend.syntax.ast.statement;

import frontend.syntax.ast.expression.Exp;

public class ExpStmt implements Stmt {

    private Exp exp;

    public ExpStmt(Exp exp) {
        this.exp = exp;
    }

    public Exp getExp() {
        return exp;
    }
}
