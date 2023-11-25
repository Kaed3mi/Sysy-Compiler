package midend;

import frontend.lexical.Ident;

public class LLvmIdent {
    
    private static int LLvmRegCnt = 0;
    private static int BasicBlockCnt = 0;
    private static int FuncFParamCnt = 0;
    private final String prefix;
    private final String name;

    private LLvmIdent(String prefix, String name) {
        this.prefix = prefix;
        this.name = name;
    }

    public static LLvmIdent ConstantIdent(int val) {
        return new LLvmIdent("", String.valueOf(val));
    }

    public static LLvmIdent GlobalVarIdent(Ident ident) {
        return new LLvmIdent("@", ident.toString());
    }

    public static LLvmIdent FuncIdent(Ident ident) {
        return new LLvmIdent("@", ident.toString());
    }

    public static LLvmIdent FuncIdent(String name) {
        return new LLvmIdent("@", name);
    }

    public static LLvmIdent BBIdent() {
        return new LLvmIdent("%b", String.valueOf(BasicBlockCnt++));
    }

    public static LLvmIdent RegIdent() {
        return new LLvmIdent("%v", String.valueOf(LLvmRegCnt++));
    }

    public static LLvmIdent NoneIdent() {
        return new LLvmIdent("%v", String.valueOf(-1));
    }

    public static LLvmIdent FuncFParamIdent() {
        return new LLvmIdent("%f", String.valueOf(FuncFParamCnt++));
    }

    @Override
    public String toString() {
        return prefix + name;
    }

    public String name() {
        return name;
    }
}
