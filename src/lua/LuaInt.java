package lua;

public class LuaInt implements LuaNumber {
    private int data;

    public LuaInt(int data) {
        this.data = data;
    }

    public Double toDouble() {
        return Double.valueOf(this.data);
    }

    // public Double add(LuaType t) {
    //     Double rhs = t.toDouble();

    //     if (rhs != null) return this.data + rhs;
    //     return null;
    // }

    @Override
    public String toString() {
        return Integer.toString(this.data);
    }
    
    @Override
    public boolean toBoolean() {
        return true;
    }

    @Override
    public int hashCode() {
        // System.out.println(String.format("num-%f", this.toDouble()));
        return String.format("num-%f", this.toDouble()).hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof LuaNumber)) {
            return false;
        }
        LuaObj oNumber = (LuaObj)o;
        // System.out.println(String.format("I: %f - %f", this.toDouble(), oNumber.toDouble()));
        return this.toDouble().doubleValue() == oNumber.toDouble().doubleValue();
    }
}
