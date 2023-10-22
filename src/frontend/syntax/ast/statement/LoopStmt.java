package frontend.syntax.ast.statement;

import frontend.syntax.ast.expression.Cond;

public class LoopStmt implements Stmt {

    private boolean isForLoop;
    private ForStmt initStmt;
    private Cond cond;
    private ForStmt continueStmt;
    private Stmt bodyStmt;


    public LoopStmt(ForStmt initStmt, Cond cond, ForStmt continueStmt, Stmt bodyStmt) {
        this.initStmt = initStmt;
        this.cond = cond;
        this.continueStmt = continueStmt;
        this.bodyStmt = bodyStmt;
        this.isForLoop = true;
    }

    public LoopStmt(Cond cond, Stmt bodyStmt) {
        this.initStmt = null;
        this.cond = cond;
        this.continueStmt = null;
        this.bodyStmt = bodyStmt;
        this.isForLoop = false;
    }

    public boolean isForLoop() {
        return isForLoop;
    }

    public ForStmt getInitStmt() {
        return initStmt;
    }

    public Cond getCond() {
        return cond;
    }

    public ForStmt getContinueStmt() {
        return continueStmt;
    }

    public Stmt getBodyStmt() {
        return bodyStmt;
    }
}
