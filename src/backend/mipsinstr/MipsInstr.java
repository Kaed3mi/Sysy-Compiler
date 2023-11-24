package backend.mipsinstr;

import backend.operand.Operand;

import java.util.ArrayList;
import java.util.Arrays;

public class MipsInstr {

    private final MipsInstrType mipsInstrType;
    private final ArrayList<Operand> operands;

    protected MipsInstr(MipsInstrType mipsInstrType, Operand... operands) {
        this.mipsInstrType = mipsInstrType;
        this.operands = new ArrayList<>(Arrays.asList(operands));
    }

    @Override
    public String toString() {
        return String.format("%s %s", mipsInstrType,
                operands.stream().map(Operand::toString)
                        .reduce((s1, s2) -> s1 + ", " + s2).orElse("")
        );
    }
}
