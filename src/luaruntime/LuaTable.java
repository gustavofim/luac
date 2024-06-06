package luaruntime;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicLong;

public class LuaTable implements LuaType {
    private static final AtomicLong nextId = new AtomicLong(0);
    private final long id = nextId.getAndIncrement();

    private HashMap<LuaType, LuaType> data = new HashMap<LuaType, LuaType>();

    private double idx = 0.0;

    public LuaType get(LuaType key) {
        return data.getOrDefault(key, LuaNil.getInstance());
    }

    public void put(LuaType key, LuaType value) {
        data.put(key, value);
    }

    public void put(LuaType value) {
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
