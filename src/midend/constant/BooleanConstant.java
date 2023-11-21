package midend.constant;

import midend.LLvmIdent;
import midend.llvm_type.LLvmType;
import midend.value.Value;

public class BooleanConstant extends Value implements Constant {
    private final boolean val;
    public static final BooleanConstant TRUE = new BooleanConstant(true);
    public static final BooleanConstant FALSE = new BooleanConstant(false);

    private BooleanConstant(boolean val) {
        super(LLvmType.I1_TYPE, LLvmIdent.UNNAMED);
        this.val = val;
    }

    @Override
    public String toString() {
        return String.valueOf(val);
    }

    @Override
    public String lLvmIdent() {
        return toString();
    }

}
