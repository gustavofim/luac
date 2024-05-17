package luaruntime;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Scanner;

public class Runtime {
    // private static HashMap<String, LuaType> v = new HashMap<String, LuaType>();
    private static Deque<HashMap<String, LuaType>> vars = new ArrayDeque<HashMap<String, LuaType>>();

    private final static LuaNil luaNil = LuaNil.getInstance();

    public static LuaType wrapConst(double number) {
        LuaNumber newNum = new LuaNumber(number);
        return newNum;
    }

    public static LuaType wrapConst(String str) {
        LuaString newStr = new LuaString(str);
        return newStr;
    }

    public static LuaType wrapConst(int id, LuaFunctionLiteral func) {
        LuaFunction newFunc = new LuaFunction(id, func);
        return newFunc;
    }

    public static LuaType nilConst() {
        return luaNil;
    }

    public static LuaType trueConst() {
        return new LuaBoolean(true);
    }

    public static LuaType falseConst() {
        return new LuaBoolean(false);
    }

    public static LuaType tableConst() {
        return new LuaTable();
    }

    public static LuaType constructTable(LuaType table, LuaType key, LuaType value) {
        if (!(table instanceof LuaTable)) {
            System.out.printf("RUNTIME ERROR: indexing non table\n");
            System.exit(1);
        }
        ((LuaTable)table).put(key, value);
        return table;
    }

    public static LuaType constructTable(LuaType table, LuaType value) {
        if (!(table instanceof LuaTable)) {
            System.out.printf("RUNTIME ERROR: indexing non table\n");
            System.exit(1);
        }
        ((LuaTable)table).put(value);
        return table;
    }

    public static LuaType getFromTable(LuaType table, LuaType key) {
        if (!(table instanceof LuaTable)) {
            System.out.printf("RUNTIME ERROR: indexing non table\n");
            System.exit(1);
        }
        return ((LuaTable)table).get(key);
    }

    public static void setGlobalVar(String id, LuaType value) {
        // System.out.println("==========================================");
        // System.out.println(id);
        // System.out.println(value.toString());
        // System.out.println("==========================================");
        vars.peekLast().put(id, value);
    }

    public static void setLocalVar(String id, LuaType value) {
        vars.peek().put(id, value);
    }

    public static LuaType getVar(String id) {
        LuaType ret = null;

        for (HashMap<String, LuaType> v : vars)  {
            ret = v.getOrDefault(id, null);
            if (ret != null) return ret;
        }

        return vars.peek().getOrDefault(id, luaNil);
    }

    public static LuaType getLocalVar(String id) {
        return vars.peek().getOrDefault(id, luaNil);
    }

    public static LuaType aritOp(LuaType a, LuaType b, int op) {
        if (a instanceof LuaNil) {
            System.out.printf("RUNTIME ERROR: attempt to perform arithmetic on a nil value (var '%s')\n", a);
            System.exit(1);
        }

        if (b instanceof LuaNil) {
            System.out.printf("RUNTIME ERROR: attempt to perform arithmetic on a nil value (var '%s')\n", b);
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
                return luaNil;
        }
    }

    public static LuaType relatOp(LuaType a, LuaType b, int op) {
        if (a.getClass() != b.getClass()) {
            return new LuaBoolean(false);
        }

        int cmp;
        if (a instanceof LuaNumber) {
            cmp = a.toDouble().compareTo(b.toDouble());
        } else {
            cmp = a.toString().compareTo(b.toString());
        }

        switch (op) {
            case 1:
                return new LuaBoolean(cmp == 0);
            case 2:
                return new LuaBoolean(cmp != 0);
            case 3:
                return new LuaBoolean(cmp > 0);
            case 4:
                return new LuaBoolean(cmp < 0);
            case 5:
                return new LuaBoolean(cmp >= 0);
            case 6:
                return new LuaBoolean(cmp <= 0);
            default:
                // Should never be accessed
                return luaNil;
        }
    }

    public static LuaType unaryOp(LuaType value, int op) {
        switch (op) {
            case 1:
                return new LuaNumber(-1 * value.toDouble());
            case 2:
                return new LuaBoolean(!value.toBoolean());
            case 3:
                if (!(value instanceof LuaTable)) {
                    System.out.printf("RUNTIME ERROR: attempt to get length of non table\n");
                    System.exit(1);
                }
                return new LuaNumber(((LuaTable)value).len());
            default:
                // Should never be accessed
                return luaNil;
        }
    }

    public static LuaType boolOp(LuaType a, LuaType b, int op) {
        boolean aBool = a.toBoolean();

        switch (op) {
            case 1:
                if (!aBool) {
                    return a;
                } else {
                    return b;
                }
            case 2:
                if (aBool) {
                    return a;
                } else {
                    return b;
                }
            default:
                // Should never be accessed
                return luaNil;
        }
    }

    public static LuaType setParam(LuaType func, String param) {
        ((LuaFunction)func).setParam(param);
        return func;
    }

    public static LuaType setArg(LuaType func, LuaType arg, int id) {
        ((LuaFunction)func).setArg(id, arg);
        return func;
    }

    public static LuaType call(LuaType func) {
        return ((LuaFunction)func).call();
    }

    public static void print(LuaType value) {
        System.out.println(value);
    }

    public static LuaType read() {
        Scanner scan = new Scanner(System.in);
        String s = scan.nextLine();
        scan.close();
        return wrapConst(s);
    }

    public static void startScope() {
        vars.push(new HashMap<String, LuaType>());
    }

    public static void endScope() {
        vars.pop();
    }
}
