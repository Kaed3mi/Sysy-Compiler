package backend.mipsinstr;

import backend.operand.Operand;

public class RInstr extends MipsInstr {

    public enum RType implements MipsInstrType {
        addu, sub,
        or
    }

    public RInstr(RType rType, Operand... operands) {
        super(rType, operands);
    }

}
