package lua;

public class LuaDouble implements LuaNumber {
    private double data;

    public LuaDouble(double data) {
        this.data = data;
    }

    public Double toDouble() {
        return this.data;
    }

    // public Double add(LuaType t) {
    //     Double rhs = t.toDouble();

    //     if (rhs != null) return this.data + rhs;
    //     return null;
    // }

    @Override
    public String toString() {
        return Double.toString(this.data);
    }
    
    @Override
    public boolean toBoolean() {
        return true;
    }

    @Override
    public int hashCode() {
        // System.out.println(String.format("D: num-%f", this.toDouble()));
        return String.format("num-%f", this.data).hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof LuaNumber)) {
            return false;
        }
        LuaObj oNumber = (LuaObj)o;
        // System.out.println(String.format("I: %.10f - %.10f", this.toDouble(), oNumber.toDouble()));
        return this.toDouble().doubleValue() == oNumber.toDouble().doubleValue();
    }
}
