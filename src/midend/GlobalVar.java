package midend;

import frontend.semantic.initialization.Initialization;
import midend.llvm_type.LLvmType;
import midend.llvm_type.PointerType;
import midend.value.Value;

public class GlobalVar extends Value {

    private final Initialization initialization;

    public GlobalVar(LLvmType lLvmType, LLvmIdent llvmIdent, Initialization initialization) {
        super(new PointerType(lLvmType), llvmIdent);
        this.initialization = initialization;
    }


    @Override
    public String toString() {
        return String.format("%s = dso_local global %s", lLvmIdent, initialization);
    }

    public Initialization initialization() {
        return initialization;
    }
}
