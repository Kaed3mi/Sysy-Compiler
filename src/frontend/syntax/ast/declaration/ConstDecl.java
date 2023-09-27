package frontend.syntax.ast.declaration;

import frontend.syntax.ast.BType;

import java.util.ArrayList;

public class ConstDecl implements Decl {
    protected BType bType;
    protected ArrayList<ConstDef> constDefList;

    public ConstDecl(BType bType, ArrayList<ConstDef> constDefList) {
        this.bType = bType;
        this.constDefList = constDefList;
    }

}
