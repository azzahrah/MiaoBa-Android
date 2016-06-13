package cn.nodemedia.library.db.sqlite;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import cn.nodemedia.library.db.converter.ColumnConverterFactory;

import java.util.ArrayList;
import java.util.List;

public final class SqlInfo {

    private String sql;
    private List<cn.nodemedia.library.db.KeyValue> bindArgs;

    public SqlInfo() {
    }

    public SqlInfo(String sql) {
        this.sql = sql;
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public void addBindArg(cn.nodemedia.library.db.KeyValue kv) {
        if (bindArgs == null) {
            bindArgs = new ArrayList<cn.nodemedia.library.db.KeyValue>();
        }
        bindArgs.add(kv);
    }

    public void addBindArgs(List<cn.nodemedia.library.db.KeyValue> bindArgs) {
        if (this.bindArgs == null) {
            this.bindArgs = bindArgs;
        } else {
            this.bindArgs.addAll(bindArgs);
        }
    }

    public SQLiteStatement buildStatement(SQLiteDatabase database) {
        SQLiteStatement result = database.compileStatement(sql);
        if (bindArgs != null) {
            for (int i = 1; i < bindArgs.size() + 1; i++) {
                cn.nodemedia.library.db.KeyValue kv = bindArgs.get(i - 1);
                Object value = cn.nodemedia.library.db.table.ColumnUtils.convert2DbValueIfNeeded(kv.value);
                if (value == null) {
                    result.bindNull(i);
                } else {
                    cn.nodemedia.library.db.converter.ColumnConverter converter = ColumnConverterFactory.getColumnConverter(value.getClass());
                    ColumnDbType type = converter.getColumnDbType();
                    switch (type) {
                        case INTEGER:
                            result.bindLong(i, ((Number) value).longValue());
                            break;
                        case REAL:
                            result.bindDouble(i, ((Number) value).doubleValue());
                            break;
                        case TEXT:
                            result.bindString(i, value.toString());
                            break;
                        case BLOB:
                            result.bindBlob(i, (byte[]) value);
                            break;
                        default:
                            result.bindNull(i);
                            break;
                    } // end switch
                }
            }
        }
        return result;
    }

    public Object[] getBindArgs() {
        Object[] result = null;
        if (bindArgs != null) {
            result = new Object[bindArgs.size()];
            for (int i = 0; i < bindArgs.size(); i++) {
                result[i] = cn.nodemedia.library.db.table.ColumnUtils.convert2DbValueIfNeeded(bindArgs.get(i).value);
            }
        }
        return result;
    }

    public String[] getBindArgsAsStrArray() {
        String[] result = null;
        if (bindArgs != null) {
            result = new String[bindArgs.size()];
            for (int i = 0; i < bindArgs.size(); i++) {
                Object value = cn.nodemedia.library.db.table.ColumnUtils.convert2DbValueIfNeeded(bindArgs.get(i).value);
                result[i] = value == null ? null : value.toString();
            }
        }
        return result;
    }
}
