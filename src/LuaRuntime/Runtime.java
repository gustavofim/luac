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
        if (a instanceof LuaNil) {
            System.out.printf("RUNTIME ERROR: attempt to perform arithmetic on a nil value (var '%s')", a);
            System.exit(1);
        }

        if (b instanceof LuaNil) {
            System.out.printf("RUNTIME ERROR: attempt to perform arithmetic on a nil value (var '%s')", b);
            System.exit(1);
        }

        Double aNum = a.toDouble();
        Double bNum = b.toDouble();

        switch (op) {
            case 1:
                return new LuaNumber(aNum + bNum);
            case 2:
                return new LuaNumber(aNum - bNum);
            case 3:
                return new LuaNumber(aNum * bNum);
            case 4:
                return new LuaNumber(aNum / bNum);
            case 5:
                return new LuaNumber(aNum % bNum);
            default:
                // Should never be accessed
                return new LuaNil();
        }
    }

    public static LuaType relatOp(LuaType a, LuaType b, int op) {
        if (a.getClass() != b.getClass()) {
            return new LuaBoolean(false);
        }

        Double aNum = a.toDouble();
        Double bNum = b.toDouble();

        switch (op) {
            case 1:
                return new LuaBoolean(aBool > bBool);
            case 2:
                return new LuaBoolean(aBool < bBool);
            case 3:
                return new LuaBoolean(aBool >= bBool);
            case 4:
                return new LuaBoolean(aBool / bBool);
            case 5:
                return new LuaBoolean(aBool % bBool);
            default:
                // Should never be accessed
                return new LuaNil();
        }
    }

    public static void print(LuaType value) {
        System.out.println(value);
    }
}
