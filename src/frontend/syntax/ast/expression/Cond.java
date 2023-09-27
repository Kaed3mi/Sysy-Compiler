package frontend.syntax.ast.expression;

import frontend.lexical.Token;

import java.util.ArrayList;

public class Cond extends BinaryExp {
    public Cond(Exp first, ArrayList<Token> operators, ArrayList<Exp> follows) {
        super(first, operators, follows);
    }

    public Cond(BinaryExp binaryExp) {
        super(binaryExp.first, binaryExp.operators, binaryExp.follows);
    }
}
