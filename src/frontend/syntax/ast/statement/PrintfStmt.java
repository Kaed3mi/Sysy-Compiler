package frontend.syntax.ast.statement;

import frontend.syntax.ast.expression.Exp;
import frontend.syntax.ast.expression.FormatString;

import java.util.ArrayList;

public class PrintfStmt implements Stmt {
    private FormatString formatString;
    private ArrayList<Exp> arguments;

    public PrintfStmt(FormatString formatString, ArrayList<Exp> arguments) {
        this.formatString = formatString;
        this.arguments = arguments;
    }
}
