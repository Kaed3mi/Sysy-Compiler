package frontend.syntax.ast.statement;

import frontend.syntax.ast.expression.Cond;

public class IfStmt implements Stmt {

    private Cond cond;
    private Stmt thenStmt;
    private Stmt elseStmt;

    public IfStmt(Cond cond, Stmt thenStmt, Stmt elseStmt) {
        this.cond = cond;
        this.thenStmt = thenStmt;
        this.elseStmt = elseStmt;
    }
}
