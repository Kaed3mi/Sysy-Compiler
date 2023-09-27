package frontend.syntax.ast.expression;

import frontend.lexical.Token;

import java.util.ArrayList;

public class AddExp extends BinaryExp implements ConstExp {
    public AddExp(Exp first, ArrayList<Token> operators, ArrayList<Exp> follows) {
        super(first, operators, follows);
    }

    public AddExp(BinaryExp binaryExp) {
        super(binaryExp.first, binaryExp.operators, binaryExp.follows);
    }
}
