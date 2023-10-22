package frontend.syntax.ast.statement;

import exceptions.CompileError;
import exceptions.ErrorBuilder;
import exceptions.ErrorType;
import frontend.syntax.ast.expression.Exp;
import frontend.syntax.ast.expression.FormatString;

import java.util.ArrayList;

public class PrintfStmt implements Stmt {
    private FormatString formatString;
    private ArrayList<Exp> arguments;
    private int lineNum;

    public PrintfStmt(FormatString formatString, ArrayList<Exp> arguments, int lineNum) {
        this.formatString = formatString;
        this.arguments = arguments;
        this.lineNum = lineNum;
        checkPrintf();
    }

    public void checkPrintf() {
        int formatCharLength = formatString.toString().split("%d").length - 1;
        if (formatCharLength != arguments.size()) {
            ErrorBuilder.appendError(new CompileError(lineNum, ErrorType.MISMATCH_PRINTF, "printf格式字符与参数个数不匹配"));
        }
    }

    public FormatString getFormatString() {
        return formatString;
    }

    public ArrayList<Exp> getArguments() {
        return arguments;
    }

    public int getLineNum() {
        return lineNum;
    }
}
