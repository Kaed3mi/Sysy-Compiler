package midend.function;

import midend.LLvmIdent;
import midend.llvm_type.LLvmType;
import midend.value.Value;

public class FunctionFParam extends Value {
    public FunctionFParam(LLvmType lLvmType) {
        super(lLvmType, LLvmIdent.FuncFParamIdent());
    }

    @Override
    public String toString() {
        return lLvmType + " " + lLvmIdent;
    }
}
