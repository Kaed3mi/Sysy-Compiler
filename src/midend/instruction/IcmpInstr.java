package midend.instruction;

import midend.BasicBlock;
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
            ZextInstr zextInstr;
            if (operand0.lLvmType() == LLvmType.I1_TYPE) {
                zextInstr = new ZextInstr(operand0, LLvmType.I32_TYPE, parentBasicBlock);
                parentBasicBlock.addInstr(zextInstr);
                addUse(this.operand0 = zextInstr);
                addUse(this.operand1 = operand1);
            } else if (operand1.lLvmType() == LLvmType.I1_TYPE) {
                zextInstr = new ZextInstr(operand1, LLvmType.I32_TYPE, parentBasicBlock);
                parentBasicBlock.addInstr(zextInstr);
                addUse(this.operand0 = operand0);
                addUse(this.operand1 = zextInstr);
            } else {
                throw new RuntimeException("");
            }
            Value.changeIdent(zextInstr, this);
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
}
