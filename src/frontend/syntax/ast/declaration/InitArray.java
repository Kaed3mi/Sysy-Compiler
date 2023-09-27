package frontend.syntax.ast.declaration;

import java.util.ArrayList;

public class InitArray implements InitVal, ConstInitVal {

    private final ArrayList<InitVal> initVals;

    public InitArray(ArrayList<InitVal> initVals) {
        this.initVals = initVals;
    }
}
