package frontend.syntax.ast.declaration;

import frontend.lexical.Ident;
import frontend.syntax.ast.expression.ConstExp;

import java.util.ArrayList;

/**
 * 常数定义 ConstDef ::= Ident { '[' ConstExp ']' } '=' ConstInitVal
 */
public class ConstDef extends Def {
    public ConstDef(Ident ident, ArrayList<ConstExp> arrayDim, InitVal initVal) {
        super(ident, arrayDim, initVal);
    }
}
