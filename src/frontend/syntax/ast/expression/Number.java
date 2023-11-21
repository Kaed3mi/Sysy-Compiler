package frontend.syntax.ast.expression;

import frontend.lexical.Token;

public class Number implements PrimaryExp {
    private Token token;

    public Number(Token token) {
        this.token = token;
    }

    public String getVal() {
        return token.getContent();
    }

}
