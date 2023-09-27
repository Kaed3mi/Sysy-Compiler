package frontend.syntax.ast.declaration;

import frontend.syntax.ast.expression.ConstExp;

import java.util.ArrayList;

/**
 * 常数定义 ConstDef ::= Ident { '[' ConstExp ']' } '=' ConstInitVal
 */
public class ConstDef extends Def {
    public ConstDef(String ident, ArrayList<ConstExp> arrayDim, InitVal initVal) {
        super(ident, arrayDim, initVal);
    }
}
