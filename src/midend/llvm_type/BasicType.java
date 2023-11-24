package midend.llvm_type;

public class BasicType extends LLvmType {
    private final String LLvmDataType;

    private final int size;

    public BasicType(String LLvmDataType, int size) {
        this.LLvmDataType = LLvmDataType;
        this.size = size;
    }

    @Override
    public String toString() {
        return LLvmDataType;
    }

    @Override
    public int size() {
        return size;
    }
}
