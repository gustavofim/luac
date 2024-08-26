package lua;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashMap;
import java.util.Scanner;

public class Runtime {
    // private static HashMap<String, LuaType> v = new HashMap<String, LuaType>();
    private static Deque<HashMap<String, LuaObj>> vars = new ArrayDeque<HashMap<String, LuaObj>>();

    private final static LuaNil luaNil = LuaNil.getInstance();

    public static LuaObj wrapConst(int number) {
        LuaInt newNum = new LuaInt(number);
        return newNum;
    }

    public static LuaObj wrapConst(double number) {
        LuaDouble newNum = new LuaDouble(number);
        return newNum;
    }

    public static LuaObj wrapConst(String str) {
        LuaString newStr = new LuaString(str);
        return newStr;
    }

    public static LuaObj wrapConst(int id, LuaFunctionLiteral func) {
        LuaFunction newFunc = new LuaFunction(id, func);
        return newFunc;
    }

    public static LuaObj nilConst() {
        return luaNil;
    }

    public static LuaObj trueConst() {
        return new LuaBoolean(true);
    }

    public static LuaObj falseConst() {
        return new LuaBoolean(false);
    }

    public static LuaObj tableConst() {
        return new LuaTable();
    }

    public static LuaObj constructTable(LuaObj table, LuaObj key, LuaObj value) {
        if (!(table instanceof LuaTable)) {
            System.out.printf("RUNTIME ERROR: indexing non table\n");
            System.exit(1);
        }
        ((LuaTable)table).put(key, value);
        return table;
    }

    public static LuaObj constructTable(LuaObj table, LuaObj value) {
        if (!(table instanceof LuaTable)) {
            System.out.printf("RUNTIME ERROR: indexing non table\n");
            System.exit(1);
        }
        ((LuaTable)table).put(value);
        return table;
    }

    public static LuaObj getFromTable(LuaObj table, LuaObj key) {
        if (!(table instanceof LuaTable)) {
            System.out.printf("RUNTIME ERROR: indexing non table\n");
            System.exit(1);
        }
        return ((LuaTable)table).get(key);
    }

    public static void setGlobalVar(String id, LuaObj value) {
        // System.out.println("==========================================");
        // System.out.println(id);
        // System.out.println(value.toString());
        // System.out.println("==========================================");
        vars.peekLast().put(id, value);
    }

    public static void setLocalVar(String id, LuaObj value) {
        vars.peek().put(id, value);
    }

    public static LuaObj getVar(String id) {
        LuaObj ret = null;

        for (HashMap<String, LuaObj> v : vars)  {
            ret = v.getOrDefault(id, null);
            if (ret != null) return ret;
        }

        return vars.peek().getOrDefault(id, luaNil);
    }

    // public static LuaObj getLocalVar(String id) {
    //     return vars.peek().getOrDefault(id, luaNil);
    // }

    public static LuaObj aritOp(LuaObj a, LuaObj b, int op) {
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

        boolean aIsInt = a instanceof LuaInt;
        boolean bIsInt = b instanceof LuaInt;

        if (a instanceof LuaString) {
            if (aNum == null) {
                System.out.printf("RUNTIME ERROR: attempt to perform arithmetic on a string.\n");
                System.exit(1);
            }
            try {
                Integer.parseInt(a.toString());
                aIsInt = true;                
            } catch (Exception e) {
            }
        }

        if (b instanceof LuaString) {
            if (bNum == null) {
                System.out.printf("RUNTIME ERROR: attempt to perform arithmetic on a string.\n");
                System.exit(1);
            }
            try {
                Integer.parseInt(b.toString());
                bIsInt = true;                
            } catch (Exception e) {
            }
        }

        boolean ints = aIsInt && bIsInt;

        switch (op) {
            case 1:
                if (ints)
                    return new LuaInt(aNum.intValue() + bNum.intValue());
                return new LuaDouble(aNum + bNum);
            case 2:
                if (ints)
                    return new LuaInt(aNum.intValue() - bNum.intValue());
                return new LuaDouble(aNum - bNum);
            case 3:
                if (ints)
                    return new LuaInt(aNum.intValue() * bNum.intValue());
                return new LuaDouble(aNum * bNum);
            case 4:
                return new LuaDouble(aNum / bNum);
            case 5:
                if (ints)
                    return new LuaInt(aNum.intValue() % bNum.intValue());
                return new LuaDouble(aNum % bNum);
            default:
                // Should never be accessed
                return luaNil;
        }
    }

    public static LuaObj relatOp(LuaObj a, LuaObj b, int op) {
        if (a.getClass() != b.getClass()) {
            if (!(a instanceof LuaNumber && b instanceof LuaNumber)) {
                return new LuaBoolean(false);
            }
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

    public static LuaObj unaryOp(LuaObj value, int op) {
        switch (op) {
            case 1:
                return new LuaDouble(-1 * value.toDouble());
            case 2:
                return new LuaBoolean(!value.toBoolean());
            case 3:
                if (!(value instanceof LuaTable)) {
                    System.out.printf("RUNTIME ERROR: attempt to get length of non table\n");
                    System.exit(1);
                }
                return new LuaDouble(((LuaTable)value).len());
            default:
                // Should never be accessed
                return luaNil;
        }
    }

    public static LuaObj boolOp(LuaObj a, LuaObj b, int op) {
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

    public static LuaObj setParam(LuaObj func, String param) {
        ((LuaFunction)func).setParam(param);
        return func;
    }

    public static LuaObj setArg(LuaObj func, LuaObj arg, int id) {
        ((LuaFunction)func).setArg(id, arg);
        return func;
    }

    public static LuaObj call(LuaObj func) {
        return ((LuaFunction)func).call();
    }

    public static void print(LuaObj value) {
        System.out.println(value);
    }

    public static LuaObj read() {
        Scanner scan = new Scanner(System.in);
        String s = scan.nextLine();
        scan.close();
        return wrapConst(s);
    }

    public static void startScope() {
        vars.push(new HashMap<String, LuaObj>());
    }

    public static void endScope() {
        vars.pop();
    }
}
