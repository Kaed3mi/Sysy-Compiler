package frontend.semantic.initialization;

import midend.llvm_type.ArrayType;
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

    public int size() {
        LLvmType lLvmType = this.lLvmType;
        int size = 1;
        while (lLvmType instanceof ArrayType) {
            size = size * ((ArrayType) lLvmType).length();
            lLvmType = ((ArrayType) lLvmType).getElementType();
        }
        return size;
    }

}
