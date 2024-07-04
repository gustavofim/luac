package lua;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicLong;

public class LuaTable implements LuaObj {
    private static final AtomicLong nextId = new AtomicLong(0);
    private final long id = nextId.getAndIncrement();

    private HashMap<LuaObj, LuaObj> data = new HashMap<LuaObj, LuaObj>();

    private double idx = 0.0;

    public LuaObj get(LuaObj key) {
        return data.getOrDefault(key, LuaNil.getInstance());
    }

    public void put(LuaObj key, LuaObj value) {
        data.put(key, value);
    }

    public void put(LuaObj value) {
        data.put(new LuaDouble(++idx), value);
    }

    public double len() {
        return idx;
    }

    @Override
    public Double toDouble() {
        return null;
    }

    @Override
    public boolean toBoolean() {
        return true;
    }

    @Override
    public String toString() {
        this.data.forEach((key, value) -> System.out.println(key + ": " + value));
        return String.format("table@%d (len: %.0f)", this.id, this.idx);
    }

    @Override
    public int hashCode() {
        return String.format("table-%s", this.toString()).hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof LuaTable)) {
            return false;
        }
        LuaTable oTable = (LuaTable)o;
        return this.id == oTable.id;
    }
}
