package midend.llvm_type;

import java.util.ArrayList;

public class ArrayType extends LLvmType {

    private final int length;

    private final LLvmType elementType;

    private ArrayList<Integer> dims = new ArrayList<>();

    public ArrayType(LLvmType elementType, int length) {
        this.length = length;
        this.elementType = elementType;
        dims.add(length);
        if (elementType instanceof ArrayType) {
            dims.addAll(((ArrayType) elementType).dims);
        }
    }

    public LLvmType getElementType() {
        return elementType;
    }

    public int length() {
        return length;
    }

    @Override
    public int size() {
        return length * elementType.size();
    }

    @Override
    public String toString() {
        return String.format("[%d x %s]", length, elementType);
    }
}
