package frontend.syntax.ast.statement;

import java.util.ArrayList;

public class Block implements Stmt {
    private ArrayList<BlockItem> blockItems;
    private int rbraceLineNum;

    public Block(ArrayList<BlockItem> blockItem, int rbraceLineNum) {
        this.blockItems = blockItem;
        this.rbraceLineNum = rbraceLineNum;
    }

    public ArrayList<BlockItem> getBlockItems() {
        return blockItems;
    }

    public int getRbraceLineNum() {
        return rbraceLineNum;
    }
}
