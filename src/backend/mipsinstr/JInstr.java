package backend.mipsinstr;

import backend.operand.Operand;

public class JInstr extends MipsInstr {

    public enum JType implements MipsInstrType {
        j, jal, jr
    }
    
    public JInstr(JType jType, Operand... operands) {
        super(jType, operands);
    }

}
