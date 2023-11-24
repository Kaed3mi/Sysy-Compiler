package backend.mipsinstr;

import backend.operand.Immediate;

public class Syscall extends MipsInstr {
    // when v0 equals val, then syscall run:
    public static final Immediate PUT_INT = new Immediate(1); // a0 to print
    public static final Immediate GET_INT = new Immediate(5); // read to v0
    public static final Immediate EXIT = new Immediate(10);
    public static final Immediate PUT_CH = new Immediate(11); // a0 to print


    public enum SyscallType implements MipsInstrType {
        syscall
    }

    public Syscall() {
        super(SyscallType.syscall);
    }

    @Override
    public String toString() {
        return "syscall";
    }

}
