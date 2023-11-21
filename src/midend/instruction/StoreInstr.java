package midend.instruction;

import midend.BasicBlock;
import midend.llvm_type.LLvmType;
import midend.value.Value;

public class StoreInstr extends Instr {
    private final Value value;
    private final Value pointer;

    // 把value保存到pointer指向的内存
    public StoreInstr(Value value, Value pointer, BasicBlock parentBB) {
        super(LLvmType.VOID_TYPE, InstrOp.STORE, parentBB);
        addUse(this.value = value);
        addUse(this.pointer = pointer);
    }

    @Override
    public String toString() {
        return String.format(
                "%s %s %s, %s %s",
                instrOp,
                value.lLvmType(),
                value.lLvmIdent(),
                pointer.lLvmType(),
                pointer.lLvmIdent()
        );
    }
}
