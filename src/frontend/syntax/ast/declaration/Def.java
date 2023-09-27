package frontend.syntax.ast.declaration;

import frontend.syntax.ast.expression.ConstExp;

import java.util.ArrayList;

/**
 * 常数定义 ConstDef ::= Ident { '[' ConstExp ']' } '=' ConstInitVal
 * <p>
 * 变量定义 VarDef ::= Ident { '[' ConstExp ']' }     没有初值
 * | Ident { '[' ConstExp ']' } '=' InitVal         有初值
 */
public class Def {
    protected final String ident;
    protected final ArrayList<ConstExp> arrayDim;
    protected final boolean isArray;
    protected final InitVal initVal;

    public Def(String ident, ArrayList<ConstExp> arrayDim, InitVal initVal) {
        this.ident = ident;
        this.arrayDim = arrayDim;
        this.isArray = !arrayDim.isEmpty();
        this.initVal = initVal;
    }

}
