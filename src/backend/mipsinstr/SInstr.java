package backend.mipsinstr;

import backend.operand.Operand;

public class SInstr extends MipsInstr {

    public enum SType implements MipsInstrType {
        seq, sne, sgt, sge, slt, sle
    }

    public SInstr(SType sType, Operand... operands) {
        super(sType, operands);
    }

}
