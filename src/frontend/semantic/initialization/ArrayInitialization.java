package frontend.semantic.initialization;

import midend.constant.IntConstant;
import midend.llvm_type.ArrayType;
import midend.llvm_type.LLvmType;

import java.util.ArrayList;

public class ArrayInitialization extends Initialization {
    private final ArrayList<Initialization> initializations;

    public ArrayInitialization(LLvmType lLvmType, ArrayList<Initialization> initializations, boolean isGlobal) {
        super(lLvmType);
        // 补全零初始化
        LLvmType elementType = ((ArrayType) lLvmType).getElementType();
        if (isGlobal) {
            while (initializations.size() < ((ArrayType) lLvmType).length()) {
                initializations.add(elementType instanceof ArrayType ? new ZeroInitialization(elementType) : new VarInitialization(IntConstant.ZERO));
            }
        }
        this.initializations = initializations;
    }

    public ArrayList<Initialization> getInitializations() {
        return new ArrayList<>(initializations);
    }

    public int size() {
        return initializations.size();
    }


    @Override
    public String toString() {
        return String.format("%s [%s]",
                lLvmType,
                initializations.stream().map(Initialization::toString)
                        .reduce((s1, s2) -> s1 + ", " + s2).orElse(""));
    }

    public Initialization get(int index) {
        return initializations.get(index);
    }
}
