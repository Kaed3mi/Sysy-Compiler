package midend.llvm_type;

public class BasicType extends LLvmType {
    private final String LLvmDataType;

    BasicType(String LLvmDataType) {
        this.LLvmDataType = LLvmDataType;
    }

    @Override
    public String toString() {
        return LLvmDataType;
    }
}
