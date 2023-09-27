package frontend.syntax.ast.expression;

import frontend.lexical.Token;

import java.util.ArrayList;

public class UnaryExp implements Exp {
    private ArrayList<Token> operators;
    private PrimaryExp primaryExp;
    
    public UnaryExp(ArrayList<Token> operators, PrimaryExp primaryExp) {
        this.operators = operators;
        this.primaryExp = primaryExp;
    }

    public ArrayList<Token> getOperators() {
        return operators;
    }

    public PrimaryExp getPrimaryExp() {
        return primaryExp;
    }
}
