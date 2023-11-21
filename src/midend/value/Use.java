package midend.value;

public class Use {

    private final User user;
    private final Value usedValue;
    private final int index;

    public Use(User user, Value usedValue, int index) {
        this.user = user;
        this.usedValue = usedValue;
        this.index = index;
    }

    public Value usedValue() {
        return usedValue;
    }

}
