package backend.operand;

import midend.constant.Constant;

import java.util.HexFormat;

public class Immediate implements Operand {

    public static final Immediate ZERO = new Immediate(0);
    public static final int GP = HexFormat.fromHexDigits("10008000");
    private final int val;

    public Immediate(Constant constant) {
        val = constant.getVal();
    }

    public Immediate(int val) {
        this.val = val;
    }

    public Immediate neg() {
        return new Immediate(-val);
    }

    public int getVal() {
        return val;
    }

    @Override
    public String toString() {
        return String.valueOf(val);
    }

}
