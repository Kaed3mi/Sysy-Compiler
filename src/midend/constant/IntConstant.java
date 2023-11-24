package midend.constant;

import frontend.syntax.ast.expression.Number;
import midend.LLvmIdent;
import midend.llvm_type.LLvmType;
import midend.value.Value;

public class IntConstant extends Value implements Constant {
    private final int val;
    public static final IntConstant ZERO = new IntConstant(0);
    public static final IntConstant ONE = new IntConstant(1);

    public IntConstant(Number number) {
        super(LLvmType.I32_TYPE, LLvmIdent.ConstantIdent(Integer.parseInt(number.getVal())));
        this.val = Integer.parseInt(number.getVal());
    }

    public IntConstant(int val) {
        super(LLvmType.I32_TYPE, LLvmIdent.ConstantIdent(val));
        this.val = val;
    }

    private IntConstant(String val) {
        super(LLvmType.I32_TYPE, LLvmIdent.ConstantIdent(Integer.parseInt(val)));
        this.val = Integer.parseInt(val);
    }

    public int getVal() {
        return val;
    }

    public static IntConstant FromInt(int val) {
        return new IntConstant(val);
    }

    @Override
    public String toString() {
        return String.valueOf(val);
    }


}
