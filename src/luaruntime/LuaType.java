package luaruntime;

public interface LuaType {
    boolean toBoolean();
    Double toDouble(); 
    String toString();
    int hashCode();
    boolean equals(Object o);
}