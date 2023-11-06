package LuaRuntime;

public class LuaNil implements LuaType {
    public Double getNumData() {
        return null;
    }

    @Override
    public String toString() {
        return "Nil";
    }
}
