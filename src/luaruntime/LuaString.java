package luaruntime;

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

    @Override
    public int hashCode() {
        return String.format("str-%s", this.data).hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof LuaInt) {
            LuaInt oNumber = (LuaInt)o;
            return this.toDouble() == oNumber.toDouble();
        }
        if (!(o instanceof LuaString)) {
            return false;
        }
        LuaString oString = (LuaString)o;
        return this.data.equals(oString.data);
    }
}
