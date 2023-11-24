package backend.data;

import midend.GlobalVar;

public class Data {
    protected static int currentAddress = 0;

    protected final String ident;
    protected final GlobalVar globalVar;
    protected final int address;


    protected Data(String ident, GlobalVar globalVar, int size) {
        this.ident = ident;
        this.address = currentAddress;
        this.globalVar = globalVar;
        currentAddress = currentAddress + size;
    }

    public GlobalVar getGlobalVar() {
        return globalVar;
    }

    public int getAddress() {
        return address;
    }

    public String getIdent() {
        return ident;
    }
}
