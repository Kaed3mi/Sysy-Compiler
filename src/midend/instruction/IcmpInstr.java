package midend.instruction;

import backend.MipsBuilder;
import backend.mipsinstr.IInstr;
import backend.mipsinstr.SInstr;
import backend.operand.Immediate;
import backend.operand.Operand;
import backend.operand.Reg;
import midend.BasicBlock;
import midend.constant.Constant;
import midend.constant.IntConstant;
import midend.llvm_type.LLvmType;
import midend.value.Value;

public class IcmpInstr extends Instr {

    public enum IcmpOp {
        EQ("eq"),// equal
        NE("ne"),// not equal
        SGT("sgt"),// signed greater than
        SGE("sge"),// signed greater or equal
        SLT("slt"),// signed less than
        SLE("sle");// signed less or equal

        private final String name;

        public IcmpOp reverse() {
            return switch (this) {
                case EQ -> EQ;
                case NE -> NE;
                case SGT -> SLT;
                case SGE -> SLE;
                case SLT -> SGT;
                case SLE -> SGE;
            };
        }

        IcmpOp(String name) {
            this.name = name;
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private final IcmpOp icmpOp;
    private final Value operand0;
    private final Value operand1;

    public IcmpInstr(IcmpOp icmpOp, Value operand0, Value operand1, BasicBlock parentBasicBlock) {
        super(LLvmType.I1_TYPE, InstrOp.ICMP, parentBasicBlock);
        this.icmpOp = icmpOp;
        if (operand0.lLvmType() != operand1.lLvmType()) {
            // 当类型不同时，进行类型转换
            ZextInstr bitCastInstr;
            if (operand0.lLvmType() == LLvmType.I1_TYPE) {
                bitCastInstr = new ZextInstr(operand0, LLvmType.I32_TYPE, parentBasicBlock);
                parentBasicBlock.addInstr(bitCastInstr);
                addUse(this.operand0 = bitCastInstr);
                addUse(this.operand1 = operand1);
            } else if (operand1.lLvmType() == LLvmType.I1_TYPE) {
                bitCastInstr = new ZextInstr(operand1, LLvmType.I32_TYPE, parentBasicBlock);
                parentBasicBlock.addInstr(bitCastInstr);
                addUse(this.operand0 = operand0);
                addUse(this.operand1 = bitCastInstr);
            } else {
                throw new RuntimeException("");
            }
            Value.changeIdent(bitCastInstr, this);
        } else {
            addUse(this.operand0 = operand0);
            addUse(this.operand1 = operand1);
        }
    }

    @Override
    public String toString() {
        return String.format(
                "%s = %s %s %s %s, %s",
                lLvmIdent, instrOp, icmpOp, operand0.lLvmType(),
                operand0.lLvmIdent(),
                operand1.lLvmIdent());
    }

    // EQ("eq"),// equal
    //        NE("ne"),// not equal
    //        SGT("sgt"),// signed greater than
    //        SGE("sge"),// signed greater or equal
    //        SLT("slt"),// signed less than
    //        SLE("sle");// signed less or equal
    @Override
    public void generateMips() {
        Operand rt = MipsBuilder.applyOperand(this, false);
        if (operand0 instanceof Constant && operand1 instanceof Constant) {
            int op0 = ((Constant) operand0).getVal();
            int op1 = ((Constant) operand1).getVal();
            int ret = switch (icmpOp) {
                case EQ -> op0 == op1 ? 1 : 0;
                case NE -> op0 != op1 ? 1 : 0;
                case SGT -> op0 > op1 ? 1 : 0;
                case SGE -> op0 >= op1 ? 1 : 0;
                case SLT -> op0 < op1 ? 1 : 0;
                case SLE -> op0 <= op1 ? 1 : 0;
            };
            MipsBuilder.addMipsInstr(new IInstr(IInstr.IType.li, rt, new Immediate(ret)));
        } else if (operand0 instanceof Constant) {
            Operand rs = MipsBuilder.applyOperand(operand1, true);
            Immediate imm = new Immediate((IntConstant) operand0);
            switch (icmpOp) {
                case EQ -> MipsBuilder.addMipsInstr(new SInstr(SInstr.SType.seq, rt, rs, imm));
                case NE -> MipsBuilder.addMipsInstr(new SInstr(SInstr.SType.sne, rt, rs, imm));
                case SGT -> MipsBuilder.addMipsInstr(new SInstr(SInstr.SType.sle, rt, rs, imm));
                case SGE -> {
                    MipsBuilder.addMipsInstr(new IInstr(IInstr.IType.li, Reg.v0, imm));
                    MipsBuilder.addMipsInstr(new SInstr(SInstr.SType.slt, rt, rs, Reg.v0));
                }
                case SLT -> MipsBuilder.addMipsInstr(new SInstr(SInstr.SType.sge, rt, rs, imm));
                case SLE -> MipsBuilder.addMipsInstr(new SInstr(SInstr.SType.sgt, rt, rs, imm));
            }
        } else if (operand1 instanceof Constant) {
            Operand rs = MipsBuilder.applyOperand(operand0, true);
            Immediate imm = new Immediate((IntConstant) operand1);
            switch (icmpOp) {
                case EQ -> MipsBuilder.addMipsInstr(new SInstr(SInstr.SType.seq, rt, rs, imm));
                case NE -> MipsBuilder.addMipsInstr(new SInstr(SInstr.SType.sne, rt, rs, imm));
                case SGT -> MipsBuilder.addMipsInstr(new SInstr(SInstr.SType.sgt, rt, rs, imm));
                case SGE -> MipsBuilder.addMipsInstr(new SInstr(SInstr.SType.sge, rt, rs, imm));
                case SLT -> {
                    MipsBuilder.addMipsInstr(new IInstr(IInstr.IType.li, Reg.v0, imm));
                    MipsBuilder.addMipsInstr(new SInstr(SInstr.SType.slt, rt, rs, Reg.v0));
                }
                case SLE -> MipsBuilder.addMipsInstr(new SInstr(SInstr.SType.sle, rt, rs, imm));
            }
        } else {
            Operand rs = MipsBuilder.applyOperand(operand0, true);
            Operand rd = MipsBuilder.applyOperand(operand1, true);
            switch (icmpOp) {
                case EQ -> MipsBuilder.addMipsInstr(new SInstr(SInstr.SType.seq, rt, rs, rd));
                case NE -> MipsBuilder.addMipsInstr(new SInstr(SInstr.SType.sne, rt, rs, rd));
                case SGT -> MipsBuilder.addMipsInstr(new SInstr(SInstr.SType.sgt, rt, rs, rd));
                case SGE -> MipsBuilder.addMipsInstr(new SInstr(SInstr.SType.sge, rt, rs, rd));
                case SLT -> MipsBuilder.addMipsInstr(new SInstr(SInstr.SType.slt, rt, rs, rd));
                case SLE -> MipsBuilder.addMipsInstr(new SInstr(SInstr.SType.sle, rt, rs, rd));
            }
        }
    }
}
