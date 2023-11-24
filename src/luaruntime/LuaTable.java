package luaruntime;

import java.util.HashMap;

public class LuaTable implements LuaType {
    private HashMap<LuaType, LuaType> data;

    public LuaType get(LuaType key) {
        return data.getOrDefault(key, LuaNil.getInstance());
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
        return "table";
    }
}
