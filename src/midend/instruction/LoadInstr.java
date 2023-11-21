package midend.instruction;

import midend.BasicBlock;
import midend.llvm_type.PointerType;
import midend.value.Value;

public class LoadInstr extends Instr {

    private final Value pointer;

    public LoadInstr(Value pointer, BasicBlock parentBB) {
        super(((PointerType) pointer.lLvmType()).pointeeType(), InstrOp.LOAD, parentBB);
        addUse(this.pointer = pointer);
    }

    @Override
    public String toString() {
        return String.format(
                "%s = %s %s, %s %s",
                lLvmIdent,
                instrOp,
                lLvmType,
                pointer.lLvmType(),
                pointer.lLvmIdent()
        );
    }
}
