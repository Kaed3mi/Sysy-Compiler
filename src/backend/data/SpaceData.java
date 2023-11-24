package backend.data;

import midend.GlobalVar;

public class SpaceData extends Data {
    private final int size;

    public SpaceData(String ident, GlobalVar globalVar, int size) {
        super(ident, globalVar, size);
        this.size = size;
    }

    @Override
    public String toString() {
        return String.format("%s: .space %s", ident, size);
    }

}
