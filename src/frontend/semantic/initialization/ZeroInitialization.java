package frontend.semantic.initialization;

import midend.llvm_type.LLvmType;

public class ZeroInitialization extends Initialization {
    public ZeroInitialization(LLvmType lLvmType) {
        super(lLvmType);
    }

    @Override

    public String toString() {
        return String.format("%s %s",
                lLvmType, "zeroinitializer");
    }
}
