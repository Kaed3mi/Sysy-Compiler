package midend.instruction;

import backend.MipsBuilder;
import backend.mipsinstr.MemInstr;
import backend.mipsinstr.RInstr;
import backend.operand.Addr;
import backend.operand.Immediate;
import backend.operand.Operand;
import backend.operand.Reg;
import midend.BasicBlock;
import midend.GlobalVar;
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

    @Override
    public void generateMips() {
        // 三种指针类型llvm指令: globalvar、alloca、getelementptr
        Operand rt = MipsBuilder.applyOperand(this, false);
        if (pointer instanceof GlobalVar) {
            Immediate offset = MipsBuilder.getGpOffset(pointer);
            MipsBuilder.addMipsInstr(new MemInstr(MemInstr.MemType.lw, rt, new Addr(offset, Reg.gp)));
        } else if (pointer instanceof AllocaInstr) {
            if (MipsBuilder.regsUsingValue(pointer)) {
                // pointer在寄存器中直接从寄存器中取
                Operand rs = MipsBuilder.applyOperand(pointer, true);
                MipsBuilder.addMipsInstr(new RInstr(RInstr.RType.or, rt, rs, Reg.zero));
            } else {
                // pointer在内存中需要load到寄存器中
                Immediate imm = MipsBuilder.getSpOffset(pointer);
                MipsBuilder.addMipsInstr(new MemInstr(MemInstr.MemType.lw, rt, new Addr(imm, Reg.sp)));
            }
        } else if (pointer instanceof GetElemPtrInstr) {
            Reg elementAddr = (Reg) MipsBuilder.applyOperand(pointer, true);
            MipsBuilder.addMipsInstr(new MemInstr(MemInstr.MemType.lw, rt, new Addr(Immediate.ZERO, elementAddr))); //直接lw $t0 0($v0)可以简化指令
        } else {
            throw new RuntimeException();
        }
    }
}
