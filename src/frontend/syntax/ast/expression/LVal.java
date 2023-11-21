package frontend.syntax.ast.expression;

import frontend.lexical.Ident;

import java.util.ArrayList;

public class LVal implements PrimaryExp {
    private Ident ident;
    private ArrayList<Exp> arrayDim;

    public LVal(Ident ident, ArrayList<Exp> arrayDim, int lineNum) {
        this.ident = ident;
        this.arrayDim = arrayDim;
    }

    public Ident getIdent() {
        return ident;
    }

    public ArrayList<Exp> getArrayDim() {
        return arrayDim;
    }

}
