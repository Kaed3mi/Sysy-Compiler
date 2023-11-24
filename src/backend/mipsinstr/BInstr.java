package backend.mipsinstr;

import backend.operand.Operand;

public class BInstr extends MipsInstr {

    public enum BType implements MipsInstrType {
        beq, bne,
        bgez, bgtz, blez, bltz
    }

    public BInstr(BType bType, Operand... operands) {
        super(bType, operands);
    }
}
