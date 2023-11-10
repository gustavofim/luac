package luaruntime;

public class LuaNil implements LuaType {
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
