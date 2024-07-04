package lua;

public class LuaNil implements LuaObj {
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

    @Override
    public int hashCode() {
        return "nil".hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof LuaNil)) {
            return false;
        }
        return true;
    }
}
