package midend.instruction;

import backend.GenerateMips;
import midend.BasicBlock;
import midend.LLvmIdent;
import midend.llvm_type.LLvmType;
import midend.value.Use;
import midend.value.User;
import midend.value.Value;

import java.util.ArrayList;

public abstract class Instr extends Value implements User, GenerateMips {
    protected InstrOp instrOp;
    protected ArrayList<Use> operandList;

    protected BasicBlock parentBB;

    public Instr(LLvmType llvmType, InstrOp instrOp, BasicBlock parentBB) {
        super(llvmType, llvmType.equals(LLvmType.VOID_TYPE) ? //避免不需要返回值的指令占用LLvmReg
                LLvmIdent.NoneIdent() : LLvmIdent.RegIdent());
        this.instrOp = instrOp;
        this.parentBB = parentBB;
        this.operandList = new ArrayList<>();
    }

    protected void addUse(Value value) {
        Use use = new Use(this, value, operandList.size());
        value.addUse(use);
        this.operandList.add(use);
    }

    public InstrOp getInstrOp() {
        return instrOp;
    }

    @Override
    public void generateMips() {
        throw new RuntimeException(getClass() + "没有实现toMips");
    }
}
