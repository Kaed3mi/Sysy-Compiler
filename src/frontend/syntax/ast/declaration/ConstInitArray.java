package frontend.syntax.ast.declaration;

import java.util.ArrayList;

public class ConstInitArray implements InitVal, ConstInitVal {
    private ArrayList<ConstInitVal> initVals;

    public ConstInitArray(ArrayList<ConstInitVal> initVals) {
        this.initVals = initVals;
    }
}
