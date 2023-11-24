package backend.mipsinstr;

import backend.operand.Addr;
import backend.operand.Operand;

public class MemInstr extends MipsInstr {

    public enum MemType implements MipsInstrType {
        lw, sw
    }

    public MemInstr(MemType memType, Operand dst, Addr addr) {
        super(memType, dst, addr);
    }
}
