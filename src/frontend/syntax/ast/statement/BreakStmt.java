package frontend.syntax.ast.statement;

public class BreakStmt implements Stmt {

    private int lineNum;

    public BreakStmt(int lineNum) {
        this.lineNum = lineNum;
    }

    public int getLineNum() {
        return lineNum;
    }
}
