package frontend.syntax.ast.expression;

import java.util.ArrayList;

public class LVal implements PrimaryExp {
    public String ident;
    public ArrayList<Exp> arrayDim;

    public LVal(String ident, ArrayList<Exp> arrayDim) {
        this.ident = ident;
        this.arrayDim = arrayDim;
    }
}
