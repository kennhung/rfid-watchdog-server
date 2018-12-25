package indi.kennhuang.rfidwatchdog.server.util.database;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class TableGenerator {

    enum SQLiteDataTypes {
        INTEGER,
        TEXT,
        REAL,
        BLOB
    }
    // enums

    public class FieldConfig {
        public SQLiteDataTypes type;
        public boolean isNotNULL;
        public boolean isUnique;

        public FieldConfig(SQLiteDataTypes type, boolean isNotNULL, boolean isUnique) {
            this.type = type;
            this.isNotNULL = isNotNULL;
            this.isUnique = isUnique;
        }

        public FieldConfig(SQLiteDataTypes type) {
            this.type = type;
            isNotNULL = false;
            isUnique = false;
        }
    }

    public class FieldConfigException extends Exception {
        public FieldConfigException(String errorMessage) {
            super(errorMessage);
        }
    }
    // sub classes

    private String name;
    private Map<String, FieldConfig> fields;
    private String pkName;
    private boolean isPkAutoIncrement;

    public TableGenerator(String tableName) {
        name = tableName;
        fields = new HashMap<>();
        pkName = null;
        isPkAutoIncrement = false;
    }

    public void setPk(String name, boolean isPkAutoIncrement) throws FieldConfigException {
        if (!fields.containsKey(name)) {
            throw new FieldConfigException("Can't find field: " + name);
        }

        pkName = name;
        this.isPkAutoIncrement = isPkAutoIncrement;
    }

    public void addField(String name, FieldConfig fc) throws FieldConfigException {
        if (fields.containsKey(name)) {
            throw new FieldConfigException(name + " already exist in the table");
        }

        fields.put(name, fc);
    }

    public String getGenerateString() {
        String out = "CREATE TABLE IF NOT EXISTS `" + name + "` (";

        Set<String> keySet = fields.keySet();
        Iterator<String> it = keySet.iterator();
        String endAppend = "";

        while (it.hasNext()) {
            String key = it.next();
            FieldConfig fc = fields.get(key);

            out += " `" + key + "` " + fc.type.toString();

            if (key.equals(pkName)) {
                if (isPkAutoIncrement) {
                    out += " PRIMARY KEY AUTOINCREMENT";
                } else {
                    endAppend += ", PRIMARY KEY(`" + key + "`)";
                }
            }
            if (fc.isNotNULL) {
                out += " NOT NULL";
            }
            if (fc.isUnique) {
                out += " UNIQUE";
            }

            if (it.hasNext()) {
                out += ",";
            }
        }

        out += endAppend;
        out += ")";
        return out;
    }

    public void addInt(String name, boolean isNotNULL, boolean isUnique) throws FieldConfigException {
        FieldConfig fc = new FieldConfig(SQLiteDataTypes.INTEGER, isNotNULL, isUnique);
        addField(name, fc);
    }

    public void addInt(String name) throws FieldConfigException {
        addInt(name, false, false);
    }

    public void addText(String name, boolean isNotNULL, boolean isUnique) throws FieldConfigException {
        FieldConfig fc = new FieldConfig(SQLiteDataTypes.TEXT, isNotNULL, isUnique);
        addField(name, fc);
    }

    public void addText(String name) throws FieldConfigException {
        addText(name, false, false);
    }

    public void addReal(String name, boolean isNotNULL, boolean isUnique) throws FieldConfigException {
        FieldConfig fc = new FieldConfig(SQLiteDataTypes.REAL, isNotNULL, isUnique);
        addField(name, fc);
    }

    public void addReal(String name) throws FieldConfigException {
        addReal(name, false, false);
    }

    public void addBlob(String name, boolean isNotNULL, boolean isUnique) throws FieldConfigException {
        FieldConfig fc = new FieldConfig(SQLiteDataTypes.BLOB, isNotNULL, isUnique);
        addField(name, fc);
    }

    public void addBlob(String name) throws FieldConfigException {
        addBlob(name, false, false);
    }


}
