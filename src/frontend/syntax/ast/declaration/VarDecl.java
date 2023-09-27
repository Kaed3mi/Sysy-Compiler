package frontend.syntax.ast.declaration;

import frontend.syntax.ast.BType;

import java.util.ArrayList;

public class VarDecl implements Decl {

    protected BType bType;
    protected ArrayList<VarDef> varDefList;

    public VarDecl(BType bType, ArrayList<VarDef> varDefList) {
        this.bType = bType;
        this.varDefList = varDefList;
    }
}
