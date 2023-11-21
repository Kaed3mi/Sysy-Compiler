package frontend.syntax.ast.expression;

import frontend.lexical.BinaryOperator;
import frontend.lexical.Token;

import java.util.ArrayList;

public class Cond extends BinaryExp {
    public Cond(Exp first, ArrayList<Token> operators, ArrayList<Exp> follows) {
        super(BinaryOperator.LOR, first, operators, follows);
    }

    public Cond(BinaryExp binaryExp) {
        super(BinaryOperator.LOR, binaryExp.first, binaryExp.operators, binaryExp.follows);
    }
}
