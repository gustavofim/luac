package luaruntime;

import java.util.concurrent.atomic.AtomicLong;

public class LuaFunction implements LuaType {
    private static final AtomicLong nextId = new AtomicLong(0);
    private final long id = nextId.getAndIncrement();

    private int nArgs;
    // private String data;

    public LuaFunction(int nArgs) {
        // this.data = name;
        this.nArgs = nArgs;
    }

    @Override
    public boolean toBoolean() {
        return true;
    }

    @Override
    public Double toDouble() {
        return null;
    }


    @Override
    public String toString() {
        return String.format("function@%d (n args: %d)", this.id, this.nArgs);
    }

    @Override
    public int hashCode() {
        return String.format("function-%s", this.toString()).hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof LuaFunction)) {
            return false;
        }
        LuaFunction oFunction = (LuaFunction)o;
        return this.id == oFunction.id;
    }
}
