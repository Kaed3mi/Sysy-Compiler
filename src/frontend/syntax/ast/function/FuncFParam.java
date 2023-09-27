package frontend.syntax.ast.function;

import frontend.syntax.ast.BType;
import frontend.syntax.ast.expression.ConstExp;

import java.util.ArrayList;

/**
 * 函数形参
 * FuncFParam ::= BType Ident ['[' ']' { '[' ConstExp ']' }]
 */
public class FuncFParam {
    private final BType bType;
    private final String ident;
    private final ArrayList<ConstExp> arrayDim;
    private final boolean isArray;

    public FuncFParam(BType bType, String ident, ArrayList<ConstExp> arrayDim) {
        this.bType = bType;
        this.ident = ident;
        this.arrayDim = arrayDim;
        this.isArray = !arrayDim.isEmpty();
    }
}
