package frontend.syntax.ast.expression;

import frontend.lexical.Token;

import java.util.ArrayList;

public class BinaryExp implements Exp {

    protected Exp first;
    protected ArrayList<Token> operators;
    protected ArrayList<Exp> follows;

    public BinaryExp(Exp first, ArrayList<Token> operators, ArrayList<Exp> follows) {
        this.first = first;
        this.operators = operators;
        this.follows = follows;
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
