package LuaRuntime;

public class LuaString implements LuaType {
    private String data;

    public LuaString(String data) {
        this.data = data;
    }

    public Double getNumData() {
        return Double.parseDouble(this.data);
    }

    @Override
    public String toString() {
        return this.data;
    }
}
