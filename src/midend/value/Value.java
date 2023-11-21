package midend.value;

import midend.LLvmIdent;
import midend.llvm_type.LLvmType;

import java.util.LinkedList;

public class Value {
    private final LinkedList<Use> useList = new LinkedList<>();

    protected LLvmType lLvmType;

    protected LLvmIdent lLvmIdent;


    public Value(LLvmType lLvmType, LLvmIdent llvmIdent) {
        this.lLvmType = lLvmType;
        this.lLvmIdent = llvmIdent;
    }

    public void addUse(Use use) {
        useList.addLast(use);
    }

    public LLvmType lLvmType() {
        return lLvmType;
    }

    public String lLvmIdent() {
        return lLvmIdent.toString();
    }

    /**
     * 用来给zext交换指令ident的函数。
     */
    public static void changeIdent(Value v1, Value v2) {
        LLvmIdent lLvmIdent1 = v1.lLvmIdent;
        LLvmIdent lLvmIdent2 = v2.lLvmIdent;
        v2.lLvmIdent = lLvmIdent1;
        v1.lLvmIdent = lLvmIdent2;
    }


}
