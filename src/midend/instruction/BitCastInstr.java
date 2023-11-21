package midend.instruction;

import midend.BasicBlock;
import midend.llvm_type.LLvmType;
import midend.value.Value;

public class BitCastInstr extends Instr {
    private final Value src;

    public BitCastInstr(LLvmType destType, Value src, BasicBlock parentBB) {
        super(destType, InstrOp.BITCAST, parentBB);
        addUse(this.src = src);
    }

    @Override
    public String toString() {
        return String.format(
                "%s = %s %s %s to %s",
                lLvmIdent, instrOp, src.lLvmType(),
                src.lLvmIdent(),
                lLvmType);
    }
}
