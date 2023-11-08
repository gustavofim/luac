package LuaRuntime;

import java.util.HashMap;

public class Runtime {
    private static HashMap<String, LuaType> vars = new HashMap<String, LuaType>();

    public static LuaType wrapConst(double number) {
        LuaNumber newNum = new LuaNumber(number);
        return newNum;
    }

    public static LuaType wrapConst(String str) {
        LuaString newStr = new LuaString(str);
        return newStr;
    }

    public static void setVar(String id, LuaType value) {
        vars.put(id, value);
    }

    public static LuaType getVar(String id) {
        return vars.getOrDefault(id, new LuaNil());
    }

    public static LuaType aritOp(LuaType a, LuaType b, int op) {
        Double aNum = a.toDouble();
        if (aNum == null) {
            System.out.printf("RUNTIME ERROR: attempt to perform arithmetic on a nil value (var '%s')", a);
            System.exit(1);
        }
        Double bNum = b.toDouble();
        if (bNum == null) {
            System.out.printf("RUNTIME ERROR: attempt to perform arithmetic on a nil value (var '%s')", b);
            System.exit(1);
        }

        switch (op) {
            case 1:
                return new LuaNumber(aNum + bNum);
            case 2:
                return new LuaNumber(aNum - bNum);
            case 3:
                return new LuaNumber(aNum * bNum);
            case 4:
                return new LuaNumber(aNum / bNum);
            default:
                // Should never be accessed
                return new LuaNil();
        }
    }

    public static void print(LuaType value) {
        System.out.println(value);
    }
}
