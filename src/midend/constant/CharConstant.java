package midend.constant;

import frontend.syntax.ast.expression.Number;
import midend.LLvmIdent;
import midend.llvm_type.LLvmType;
import midend.value.Value;

public class CharConstant extends Value implements Constant {
    private final int val;
    public static final CharConstant ZERO = new CharConstant(0);
    public static final CharConstant ONE = new CharConstant(1);

    public CharConstant(Number number) {
        super(LLvmType.I8_TYPE, LLvmIdent.UNNAMED);
        this.val = Integer.parseInt(number.getVal());
    }

    public CharConstant(int val) {
        super(LLvmType.I8_TYPE, LLvmIdent.UNNAMED);
        this.val = val;
    }

    private CharConstant(String val) {
        super(LLvmType.I8_TYPE, LLvmIdent.UNNAMED);
        this.val = Integer.parseInt(val);
    }

    public static CharConstant FromInt(int val) {
        return new CharConstant(val);
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
