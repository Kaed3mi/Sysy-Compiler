package frontend.syntax.ast.expression;

import frontend.lexical.BinaryOperator;
import frontend.lexical.Token;

import java.util.ArrayList;

public class BinaryExp implements Exp {
    private final BinaryOperator binaryOperator;

    protected Exp first;
    protected ArrayList<Token> operators;
    protected ArrayList<Exp> follows;

    public BinaryExp(BinaryOperator binaryOperator, Exp first, ArrayList<Token> operators, ArrayList<Exp> follows) {
        this.binaryOperator = binaryOperator;
        this.first = first;
        this.operators = operators;
        this.follows = follows;
    }

    public BinaryOperator getBinaryOperate() {
        return binaryOperator;
    }

    public Exp getFirst() {
        return first;
    }

    public ArrayList<Token> getOperators() {
        return operators;
    }

    public ArrayList<Exp> getFollows() {
        return follows;
    }
}
