package table;

import java.util.HashMap;
import java.util.Formatter;

public final class IdentifierTable {
    private HashMap<String, Entry> table = new HashMap<String, Entry>();

    public void add(String name, int line) {
        this.table.put(name, new Entry(name, line));
    }

    public boolean lookup(String name) {
        if (this.table.get(name) != null) {
            return true;
        }
        return false;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        Formatter f = new Formatter(sb);
        f.format("; Identifier table:\n");
        this.table.forEach((key, value) ->
            f.format("; \t%s", value.toString())
        );
        f.close();
        return sb.toString();
    }

    private static final class Entry {
        private String name;
        private int line;
        // private HashMap<String, Entry> children;

        Entry(String name, int line) {
            this.name = name;
            this.line = line;
        }

        // public String getName() {
        //     return this.name;
        // }

        // public int getLine() {
        //     return this.line;
        // }

        public String toString() {
            StringBuilder sb = new StringBuilder();
            Formatter f = new Formatter(sb);
            f.format("Line: %d\t\tName: %s\n", this.line, this.name);
            f.close();
            return sb.toString();
        }
    }
}