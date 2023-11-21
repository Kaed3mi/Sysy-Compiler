package midend.instruction;

import midend.BasicBlock;
import midend.llvm_type.LLvmType;
import midend.llvm_type.PointerType;

public class AllocaInstr extends Instr {
    private final LLvmType pointeeType;

    public AllocaInstr(LLvmType pointeeType, BasicBlock parentBB) {
        super(new PointerType(pointeeType), InstrOp.ALLOCA, parentBB);
        this.pointeeType = pointeeType;
    }

    public LLvmType pointeeType() {
        return pointeeType;
    }

    @Override
    public String toString() {
        return String.format("%s = %s %s", lLvmIdent, instrOp, pointeeType);
    }
}
