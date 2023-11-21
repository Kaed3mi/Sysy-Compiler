package midend.instruction;

import midend.BasicBlock;
import midend.llvm_type.LLvmType;
import midend.value.Value;

public class AluInstr extends Instr {
    private final Value operand0;
    private final Value operand1;

    public AluInstr(LLvmType llvmType, InstrOp instrOp, Value operand0, Value operand1, BasicBlock parentBB) {
        super(llvmType, instrOp, parentBB);
        addUse(this.operand0 = operand0);
        addUse(this.operand1 = operand1);
    }

    @Override
    public String toString() {
        return String.format("%s = %s %s %s, %s",
                lLvmIdent, instrOp, lLvmType,
                operand0.lLvmIdent(),
                operand1.lLvmIdent()
        );
    }
}
