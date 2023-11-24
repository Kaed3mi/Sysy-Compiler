package midend;

import backend.GenerateMips;
import backend.MipsBuilder;
import backend.data.SpaceData;
import backend.data.WordData;
import frontend.semantic.initialization.ArrayInitialization;
import frontend.semantic.initialization.Initialization;
import frontend.semantic.initialization.VarInitialization;
import frontend.semantic.initialization.ZeroInitialization;
import midend.constant.IntConstant;
import midend.llvm_type.LLvmType;
import midend.llvm_type.PointerType;
import midend.value.Value;

import java.util.ArrayList;

public class GlobalVar extends Value implements GenerateMips {

    private final Initialization initialization;

    public GlobalVar(LLvmType lLvmType, LLvmIdent llvmIdent, Initialization initialization) {
        super(new PointerType(lLvmType), llvmIdent);
        this.initialization = initialization;
    }


    @Override
    public String toString() {
        return String.format("%s = dso_local global %s", lLvmIdent, initialization);
    }

    public Initialization initialization() {
        return initialization;
    }

    // backend

    private ArrayList<Integer> func(ArrayInitialization arrayInitialization) {
        ArrayList<Integer> words = new ArrayList<>();
        ArrayList<Initialization> inits = arrayInitialization.getInitializations();
        for (Initialization init : inits) {
            if (init instanceof VarInitialization) {
                int word = ((IntConstant) (((VarInitialization) init).initVal())).getVal();
                words.add(word);
            } else if (init instanceof ArrayInitialization) {
                words.addAll(func((ArrayInitialization) init));
            } else if (init instanceof ZeroInitialization) {
                int zeroCnt = ((ZeroInitialization) init).size();
                for (int i = 0; i < zeroCnt; i++) {
                    words.add(0);
                }
            }
        }
        return words;
    }

    @Override
    public void generateMips() {
        if (initialization instanceof VarInitialization) {
            int word = ((IntConstant) (((VarInitialization) initialization).initVal())).getVal();
            MipsBuilder.addData(new WordData(lLvmIdent.name(), this, word));
        } else if (initialization instanceof ArrayInitialization) {
            ArrayList<Integer> words = func((ArrayInitialization) initialization);
            MipsBuilder.addData(new WordData(lLvmIdent.name(), this, words));
        } else if (initialization instanceof ZeroInitialization) {
            int zeroCnt = ((ZeroInitialization) initialization).size();
            MipsBuilder.addData(new SpaceData(lLvmIdent.name(), this, zeroCnt * 4));
        }
    }
}
