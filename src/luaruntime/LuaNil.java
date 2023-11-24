package luaruntime;

public class LuaNil implements LuaType {
    private static LuaNil instance = null;

    private LuaNil() {}

    public static LuaNil getInstance() {
        if (instance == null) {
            instance = new LuaNil();
        }
        return instance;
    }

    public Double toDouble() {
        return null;
    }

    @Override
    public String toString() {
        return "nil";
    }
    
    @Override
    public boolean toBoolean() {
        return false;
    }
}
