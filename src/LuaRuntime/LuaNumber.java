package LuaRuntime;

public class LuaNumber implements LuaType {
    private double data;

    public LuaNumber(double data) {
        this.data = data;
    }

    public void add(LuaType t) {
        if (t instanceof LuaNumber) {
            System.out.println("Numbero");
        } else if (t instanceof LuaString) {
            System.out.println("String");
        }
    }

    @Override
    public String toString() {
        return Double.toString(this.data);
    }
}
