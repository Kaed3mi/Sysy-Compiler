package midend.llvm_type;

public abstract class LLvmType {
    public static final BasicType I32_TYPE;
    public static final BasicType I8_TYPE;
    public static final BasicType I1_TYPE;
    public static final BasicType F32_TYPE;
    public static final VoidType VOID_TYPE;
    public static final BBType BB_TYPE;

    static {
        I32_TYPE = new BasicType("i32");
        I8_TYPE = new BasicType("i8");
        I1_TYPE = new BasicType("i1");
        F32_TYPE = new BasicType("f32");
        VOID_TYPE = new VoidType();
        BB_TYPE = new BBType();
    }
}
