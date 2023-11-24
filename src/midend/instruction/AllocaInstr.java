package midend.instruction;

import backend.MipsBuilder;
import midend.BasicBlock;
import midend.llvm_type.ArrayType;
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

    @Override
    public void generateMips() {
        if (pointeeType == LLvmType.I32_TYPE) {
            // int a
            MipsBuilder.stackFrameAlloc(this);
            MipsBuilder.applyOperand(this, false);
        } else if (pointeeType instanceof ArrayType) {
            // int a[10]
            MipsBuilder.stackFrameAlloc(this);
        } else if (pointeeType instanceof PointerType) {
            // int f(int a[])
            MipsBuilder.stackFrameAlloc(this);
            MipsBuilder.applyOperand(this, false);
        }
    }
}
