package frontend.syntax.ast.statement;

import java.util.ArrayList;

public class Block implements Stmt {
    private ArrayList<BlockItem> blockItems;

    public Block(ArrayList<BlockItem> blockItem) {
        this.blockItems = blockItem;
    }
}
