package backend.mipsinstr;

import backend.operand.Operand;

/**
 * multiply and divide Instr
 */
public class MDInstr extends MipsInstr {

    public enum MDType implements MipsInstrType {
        multu, div, mfhi, mflo
    }

    public MDInstr(MDType mdType, Operand... operands) {
        super(mdType, operands);
    }


}
