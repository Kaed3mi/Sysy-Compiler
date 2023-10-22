package frontend.syntax.ast.function;

import frontend.syntax.ast.expression.Exp;

import java.util.ArrayList;

public class FuncRParams {
    private ArrayList<Exp> exps;

    public FuncRParams(ArrayList<Exp> exps) {
        this.exps = exps;
    }

    public ArrayList<Exp> getExps() {
        return exps;
    }
}
