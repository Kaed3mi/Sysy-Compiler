package frontend.syntax.ast.statement;

public class ContinueStmt implements Stmt {
    private int lineNum;

    public ContinueStmt(int lineNum) {
        this.lineNum = lineNum;
    }

    public int getLineNum() {
        return lineNum;
    }
}
