package frontend.semantic.initialization;

import midend.llvm_type.LLvmType;

public abstract class Initialization {
    protected final LLvmType lLvmType;

    public Initialization(LLvmType lLvmType) {
        this.lLvmType = lLvmType;
    }
}
