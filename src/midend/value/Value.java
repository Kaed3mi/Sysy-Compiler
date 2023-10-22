package midend.value;

import frontend.semantic.SymType;

public class Value {

    private SymType symType;

    public Value(SymType symType) {
        this.symType = symType;
    }

    public SymType getSymType() {
        return symType;
    }
}
