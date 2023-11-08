package LuaRuntime;

public class LuaString implements LuaType {
    private String data;

    public LuaString(String data) {
        this.data = data;
    }

    public Double toDouble() {
        return Double.parseDouble(this.data);
    }

    @Override
    public String toString() {
        return this.data;
    }
    
    @Override
    public boolean toBoolean() {
        return true;
    }
}
