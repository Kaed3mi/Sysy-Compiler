package frontend.syntax.ast.declaration;

import java.util.ArrayList;

public class ConstInitArray extends InitArray implements InitVal, ConstInitVal {
    private ArrayList<ConstInitVal> constInitVals;

    public ConstInitArray(ArrayList<ConstInitVal> constInitVals) {
        super(new ArrayList<>(constInitVals));
        this.constInitVals = constInitVals;
    }
    
}
