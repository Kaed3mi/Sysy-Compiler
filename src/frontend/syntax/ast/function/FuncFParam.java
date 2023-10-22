package frontend.syntax.ast.function;

import frontend.lexical.Ident;
import frontend.syntax.ast.BType;
import frontend.syntax.ast.expression.ConstExp;

import java.util.ArrayList;

/**
 * 函数形参
 * FuncFParam ::= BType Ident ['[' ']' { '[' ConstExp ']' }]
 */
public class FuncFParam {
    private final BType bType;
    private final Ident ident;
    private final ArrayList<ConstExp> arrayDim;
    private final boolean isArray;

    public FuncFParam(BType bType, Ident ident, ArrayList<ConstExp> arrayDim) {
        this.bType = bType;
        this.ident = ident;
        this.arrayDim = arrayDim;
        this.isArray = !arrayDim.isEmpty();
    }

    public BType getbType() {
        return bType;
    }

    public Ident getIdent() {
        return ident;
    }

    public ArrayList<ConstExp> getArrayDim() {
        return arrayDim;
    }

    public boolean isArray() {
        return isArray;
    }
}
