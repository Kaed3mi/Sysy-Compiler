package backend.operand;

import backend.mipsinstr.MipsInstr;
import backend.mipsinstr.MipsInstrType;
import midend.BasicBlock;

public class Label extends MipsInstr implements Operand {

    private final BasicBlock basicBlock;

    public enum LabelType implements MipsInstrType {
        label
    }

    public Label(BasicBlock basicBlock) {
        super(LabelType.label);
        this.basicBlock = basicBlock;
    }

    @Override
    public String toString() {
        return "label_" + basicBlock.getParentFunc().lLvmIdent().name() + "_b" + basicBlock.lLvmIdent().name();
    }

    public String labelString() {
        return toString() + ':';
    }

}
