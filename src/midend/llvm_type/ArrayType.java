package midend.llvm_type;

import java.util.ArrayList;

public class ArrayType extends LLvmType {

    private final int size;

    private final LLvmType elementType;

    private ArrayList<Integer> dims = new ArrayList<>();

    public ArrayType(LLvmType elementType, int size) {
        this.size = size;
        this.elementType = elementType;
        dims.add(size);
        if (elementType instanceof ArrayType) {
            dims.addAll(((ArrayType) elementType).dims);
        }
    }

    public LLvmType getElementType() {
        return elementType;
    }

    public int getSize() {
        return size;
    }

    @Override
    public String toString() {
        return String.format("[%d x %s]", size, elementType);
    }
}
