package frontend.syntax.ast.statement;

import frontend.syntax.ast.expression.Exp;

public class ReturnStmt implements Stmt {
    private Exp exp;
    private int lineNum;

    public ReturnStmt(Exp exp, int lineNum) {
        this.exp = exp;
        this.lineNum = lineNum;
    }

    public Exp getExp() {
        return exp;
    }

    public int getLineNum() {
        return lineNum;
    }
}
