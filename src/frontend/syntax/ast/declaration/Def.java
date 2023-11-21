package frontend.syntax.ast.declaration;

import frontend.lexical.Ident;
import frontend.syntax.ast.expression.ConstExp;

import java.util.ArrayList;

/**
 * 常数定义 ConstDef ::= Ident { '[' ConstExp ']' } '=' ConstInitVal
 * <p>
 * 变量定义 VarDef ::= Ident { '[' ConstExp ']' }     没有初值
 * | Ident { '[' ConstExp ']' } '=' InitVal         有初值
 */
public class Def {

    protected final Ident ident;
    protected final ArrayList<ConstExp> arrayDim;
    protected final boolean isArray;
    protected final InitVal initVal;

    public Def(Ident ident, ArrayList<ConstExp> arrayDim, InitVal initVal) {
        this.ident = ident;
        this.arrayDim = arrayDim;
        this.isArray = !arrayDim.isEmpty();
        this.initVal = initVal;
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

    public InitVal getInitVal() {
        return initVal;
    }

    public boolean hasInitVal() {
        return initVal != null;
    }


}
