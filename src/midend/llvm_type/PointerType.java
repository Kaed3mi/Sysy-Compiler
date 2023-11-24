package midend.llvm_type;

public class PointerType extends LLvmType {
    private final LLvmType pointeeType;

    public PointerType(LLvmType pointeeType) {
        this.pointeeType = pointeeType;
    }

    public LLvmType pointeeType() {
        return pointeeType;
    }

    @Override
    public String toString() {
        return pointeeType + "*";
    }

    @Override
    public int size() {
        return 4;
    }
}
