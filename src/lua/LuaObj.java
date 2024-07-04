package lua;

public interface LuaObj {
    boolean toBoolean();
    Double toDouble(); 
    String toString();
    int hashCode();
    boolean equals(Object o);
}