package frontend.syntax.ast.statement;

import frontend.lexical.Ident;
import frontend.lexical.Token;
import frontend.syntax.ast.expression.Exp;
import frontend.syntax.ast.function.FuncCall;

public class GetIntStmt extends FuncCall implements Exp {

    public GetIntStmt() {
        super(new Ident(new Token(null, "getint", 0)), null);
    }
}
