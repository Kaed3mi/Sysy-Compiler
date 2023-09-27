package frontend.syntax.ast.statement;

import frontend.syntax.ast.expression.Exp;
import frontend.syntax.ast.expression.LVal;

public class ForStmt extends AssignStmt implements Stmt {
    public ForStmt(LVal lVal, Exp exp) {
        super(lVal, exp);
    }
}
