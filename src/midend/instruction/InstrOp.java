package midend.instruction;

public enum InstrOp {
    // 加减
    ADD("add"), SUB("sub"),
    // 乘除
    MUL("mul"), DIV("sdiv"),
    ICMP("icmp"),
    REM("srem"),
    BR("br"),
    // 块转移
    CALL("call"), RET("ret"),
    // 内存
    ALLOCA("alloca"), STORE("store"), LOAD("load"),
    GETELEMPTR("getelementptr"), BITCAST("bitcast"),
    // 类型转换
    TRUNC("trunc"), ZEXT("zext");;
    private final String operation;

    InstrOp(String operation) {
        this.operation = operation;
    }

    @Override
    public String toString() {
        return operation;
    }
}
