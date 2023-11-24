package backend.operand;

import backend.data.Data;

public class Addr implements Operand {
    private final Data data;
    private final Operand offset;
    private final Reg base;

    public Addr(Operand offset, Reg base) {
        this.data = null;
        this.offset = offset;
        this.base = base;
    }

    public Addr(Data data) {
        this.data = data;
        this.offset = null;
        this.base = null;
    }

    @Override
    public String toString() {
        // 在mips中，如果有.data a，那么 a与a(reg)到而已直接作为地址
        if (data == null) {
            return String.format("%s(%s)", offset, base);
        } else {
            return data.getIdent();

        }
    }
}
