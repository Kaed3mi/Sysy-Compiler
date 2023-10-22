package frontend.syntax.ast.statement;

import frontend.syntax.ast.expression.Exp;
import frontend.syntax.ast.expression.LVal;

public class AssignStmt implements Stmt {
    protected LVal lVal;

    protected Exp exp;

    public AssignStmt(LVal lVal, Exp exp) {
        this.lVal = lVal;
        this.exp = exp;
    }

    public LVal getlVal() {
        return lVal;
    }

    public Exp getExp() {
        return exp;
    }
}
