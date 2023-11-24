package midend.instruction;

import backend.MipsBuilder;
import backend.mipsinstr.IInstr;
import backend.mipsinstr.MDInstr;
import backend.mipsinstr.RInstr;
import backend.operand.Immediate;
import backend.operand.Operand;
import backend.operand.Reg;
import midend.BasicBlock;
import midend.GlobalVar;
import midend.llvm_type.ArrayType;
import midend.llvm_type.LLvmType;
import midend.llvm_type.PointerType;
import midend.value.Value;

import java.util.ArrayList;
import java.util.StringJoiner;

public class GetElemPtrInstr extends Instr {
    private final Value ptr;
    private final ArrayList<Value> indexes;

    public GetElemPtrInstr(LLvmType pointeeType, Value ptr, ArrayList<Value> indexes, BasicBlock parentBB) {
        super(new PointerType(pointeeType), InstrOp.GETELEMPTR, parentBB);
        addUse(this.ptr = ptr);
        (this.indexes = indexes).forEach(this::addUse);
    }

    private String getIndexesString() {
        StringJoiner stringJoiner = new StringJoiner(", ");
        indexes.forEach(fParam ->
                stringJoiner.add(String.format(
                        "%s %s", fParam.lLvmType(), fParam.lLvmIdent())));
        return stringJoiner.toString();
    }

    public Value getPtr() {
        return ptr;
    }

    @Override
    public String toString() {
        return String.format(
                "%s = %s %s, %s %s, %s",
                lLvmIdent, instrOp, ((PointerType) ptr.lLvmType()).pointeeType(),
                ptr.lLvmType(),
                ptr.lLvmIdent(),
                getIndexesString());
    }

    @Override
    public void generateMips() {
        // 需要生成偏移地址
        LLvmType pointeeType = ptr.lLvmType();
        ArrayList<Integer> sizes = new ArrayList<>();
        for (Value ignored : indexes) {
            if (pointeeType instanceof PointerType) {
                // 函数参数
                pointeeType = ((PointerType) pointeeType).pointeeType();
            } else if (pointeeType instanceof ArrayType) {
                // 数组变量
                pointeeType = ((ArrayType) pointeeType).getElementType();
            }
            sizes.add(pointeeType.size());
        }
        // 下面计算地址：数组起始地址方法相同，唯一不同的是需要根据ptr类型计算数组基地址
        // 使用寄存器v0暂存计算地址
        MipsBuilder.addMipsInstr(new IInstr(IInstr.IType.li, Reg.v0, Immediate.ZERO));
        int intConstantOffset = 0; // 若数组下标是const，可以直接得到的偏移，减少指令。
        for (int i = 0; i < indexes.size(); i++) {
            Operand offset_i = MipsBuilder.applyOperand(indexes.get(i), true);
            if (offset_i instanceof Immediate imm) {
                // 直接加上立即数
                intConstantOffset += sizes.get(i) * imm.getVal();
            } else if (offset_i instanceof Reg rd) {
                // 利用乘法指令计算偏移
                MipsBuilder.addMipsInstr(new IInstr(IInstr.IType.li, Reg.v1, new Immediate(sizes.get(i))));
                MipsBuilder.addMipsInstr(new MDInstr(MDInstr.MDType.mult, Reg.v1, rd));
                MipsBuilder.addMipsInstr(new MDInstr(MDInstr.MDType.mflo, Reg.v1));
                MipsBuilder.addMipsInstr(new RInstr(RInstr.RType.addu, Reg.v0, Reg.v0, Reg.v1));
            }
        }
        if (ptr instanceof GlobalVar) {
            Immediate imm = new Immediate(MipsBuilder.getGpOffset(ptr).getVal() + intConstantOffset);
            Operand rt = MipsBuilder.applyOperand(this, false);
            MipsBuilder.addMipsInstr(new IInstr(IInstr.IType.addiu, Reg.v0, Reg.v0, imm));
            MipsBuilder.addMipsInstr(new RInstr(RInstr.RType.addu, rt, Reg.v0, Reg.gp));
        } else if (ptr instanceof AllocaInstr) {
            Immediate imm = new Immediate(MipsBuilder.getSpOffset(ptr).getVal() + intConstantOffset);
            Operand rt = MipsBuilder.applyOperand(this, false);
            MipsBuilder.addMipsInstr(new IInstr(IInstr.IType.addiu, Reg.v0, Reg.v0, imm));
            MipsBuilder.addMipsInstr(new RInstr(RInstr.RType.addu, rt, Reg.v0, Reg.sp));
        } else if (ptr.lLvmType() instanceof PointerType) {
            Operand ptrBaseAddr = MipsBuilder.applyOperand(ptr, true);
            Operand rt = MipsBuilder.applyOperand(this, false);
            Immediate imm = new Immediate(intConstantOffset);
            MipsBuilder.addMipsInstr(new IInstr(IInstr.IType.addiu, Reg.v0, Reg.v0, imm));
            MipsBuilder.addMipsInstr(new RInstr(RInstr.RType.addu, rt, Reg.v0, ptrBaseAddr));
        } else {
            throw new RuntimeException();
        }
    }

}
