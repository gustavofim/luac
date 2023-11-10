package luaruntime;

public class LuaNumber implements LuaType {
    private double data;

    public LuaNumber(double data) {
        this.data = data;
    }

    public Double toDouble() {
        return this.data;
    }

    public Double add(LuaType t) {
        Double rhs = t.toDouble();

        if (rhs != null) return this.data + rhs;
        return null;
    }

    @Override
    public String toString() {
        return Double.toString(this.data);
    }
    
    @Override
    public boolean toBoolean() {
        return true;
    }
}
