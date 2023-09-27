package frontend.syntax.ast.function;

import frontend.syntax.ast.CompUnit;
import frontend.syntax.ast.statement.Block;

/**
 * FuncDef ::= FuncType Ident '(' [FuncFParams] ')' Block
 */
public class FuncDef implements CompUnit {
    private FuncType funcType;
    private String ident;
    private FuncFParams funcFParams;
    private Block block;

    public FuncDef(FuncType funcType, String ident, FuncFParams funcFParams, Block block) {
        this.funcType = funcType;
        this.ident = ident;
        this.funcFParams = funcFParams;
        this.block = block;
    }
}
