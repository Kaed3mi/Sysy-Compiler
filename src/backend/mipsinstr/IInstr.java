package backend.mipsinstr;

import backend.operand.Operand;

public class IInstr extends MipsInstr {
    public enum IType implements MipsInstrType {
        li,
        addiu
    }

    public IInstr(IType iType, Operand... operands) {
        super(iType, operands);
    }

}
