package midend;

import config.Config;
import frontend.lexical.Ident;

public class LLvmIdent {

    public static LLvmIdent UNNAMED = new LLvmIdent("这个llvmIdent是匿名。");
    private static int LLvmRegCnt = 0;
    private static int BasicBlockCnt = 0;
    private static int FuncFParamCnt = 0;
    private final String name;
    private String comment;

    private LLvmIdent(String name) {
        this.name = name;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public static LLvmIdent GlobalVarIdent(Ident ident) {
        return new LLvmIdent("@" + ident.toString());
    }

    public static LLvmIdent FuncIdent(Ident ident) {
        return new LLvmIdent("@" + ident);
    }

    public static LLvmIdent FuncIdent(String name) {
        return new LLvmIdent("@" + name);
    }

    public static LLvmIdent BBIdent() {
        return new LLvmIdent("%b" + BasicBlockCnt++);
    }

    public static LLvmIdent RegIdent() {
        return new LLvmIdent("%v" + LLvmRegCnt++);
    }

    public static LLvmIdent NoneIdent() {
        return new LLvmIdent("%v" + -1);
    }

    public static LLvmIdent FuncFParamIdent() {
        return new LLvmIdent("%f" + FuncFParamCnt++);
    }

    @Override
    public String toString() {
        if (Config.LLVM_COMMENT && comment != null) {
            return name + "(@" + comment + ")";
        } else {
            return name;
        }
    }
}
