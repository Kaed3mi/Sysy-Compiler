package midend.instruction;

import backend.MipsBuilder;
import backend.mipsinstr.IInstr;
import backend.mipsinstr.MemInstr;
import backend.mipsinstr.RInstr;
import backend.operand.Addr;
import backend.operand.Immediate;
import backend.operand.Operand;
import backend.operand.Reg;
import midend.BasicBlock;
import midend.GlobalVar;
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

    @Override
    public void generateMips() {
        Operand operand = MipsBuilder.applyOperand(value, true);
        if (pointer instanceof GlobalVar) {
            Immediate offset = MipsBuilder.getGpOffset(pointer);
            if (operand instanceof Immediate imm) {
                MipsBuilder.addMipsInstr(new IInstr(IInstr.IType.li, Reg.v0, imm));
                MipsBuilder.addMipsInstr(new MemInstr(MemInstr.MemType.sw, Reg.v0, new Addr(offset, Reg.gp)));
            } else if (operand instanceof Reg rt) {
                MipsBuilder.addMipsInstr(new MemInstr(MemInstr.MemType.sw, rt, new Addr(offset, Reg.gp)));
            }
        } else if (pointer instanceof AllocaInstr) {
            if (MipsBuilder.regsUsingValue(pointer)) {
                Reg rt = (Reg) MipsBuilder.applyOperand(pointer, true);
                if (operand instanceof Immediate imm) {
                    MipsBuilder.addMipsInstr(new IInstr(IInstr.IType.li, rt, imm));
                } else if (operand instanceof Reg rs) {
                    MipsBuilder.addMipsInstr(new RInstr(RInstr.RType.or, rt, rs, Reg.zero));
                }
            } else {
                Immediate offset = MipsBuilder.getSpOffset(pointer);
                if (operand instanceof Immediate imm) {
                    MipsBuilder.addMipsInstr(new IInstr(IInstr.IType.li, Reg.v0, imm));
                    MipsBuilder.addMipsInstr(new MemInstr(MemInstr.MemType.sw, Reg.v0, new Addr(offset, Reg.sp)));
                } else if (operand instanceof Reg rt) {
                    MipsBuilder.addMipsInstr(new MemInstr(MemInstr.MemType.sw, rt, new Addr(offset, Reg.sp)));
                }
            }
        } else if (pointer instanceof GetElemPtrInstr) {
            Operand arrayOffset = MipsBuilder.applyOperand(pointer, true);
            if (operand instanceof Immediate imm) {
                MipsBuilder.addMipsInstr(new IInstr(IInstr.IType.li, Reg.v0, imm));
                MipsBuilder.addMipsInstr(new MemInstr(MemInstr.MemType.sw, Reg.v0, new Addr(Immediate.ZERO, (Reg) arrayOffset)));
            } else if (operand instanceof Reg rt) {
                MipsBuilder.addMipsInstr(new MemInstr(MemInstr.MemType.sw, rt, new Addr(Immediate.ZERO, (Reg) arrayOffset)));
            }
        } else {
            throw new RuntimeException();
        }
    }
}
