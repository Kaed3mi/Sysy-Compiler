package frontend.syntax.ast.declaration;

import frontend.lexical.Ident;
import frontend.syntax.ast.expression.ConstExp;

import java.util.ArrayList;

public class VarDef extends Def {
    private final boolean hasInitVal;

    public VarDef(Ident ident, ArrayList<ConstExp> arrayDim, InitVal initVal) {
        super(ident, arrayDim, initVal);
        hasInitVal = true;
    }

    public VarDef(Ident ident, ArrayList<ConstExp> arrayDim) {
        super(ident, arrayDim, null);
        hasInitVal = false;
    }
}
