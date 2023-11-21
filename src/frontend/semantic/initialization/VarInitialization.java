package frontend.semantic.initialization;

import midend.value.Value;

public class VarInitialization extends Initialization {

    private final Value initVal;

    public VarInitialization(Value initVal) {
        super(initVal.lLvmType());
        this.initVal = initVal;
    }

    public Value initVal() {
        return initVal;
    }

    @Override
    public String toString() {
        return lLvmType + " " + initVal;
    }
}
