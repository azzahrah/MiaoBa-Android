package cn.nodemedia.library.db.sqlite;

import cn.nodemedia.library.db.DbException;
import cn.nodemedia.library.db.table.ColumnEntity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Build "insert", "replace",，"update", "delete" and "create" sql.
 */
public final class SqlInfoBuilder {

    private static final ConcurrentHashMap<cn.nodemedia.library.db.table.TableEntity<?>, String> INSERT_SQL_CACHE = new ConcurrentHashMap<cn.nodemedia.library.db.table.TableEntity<?>, String>();
    private static final ConcurrentHashMap<cn.nodemedia.library.db.table.TableEntity<?>, String> REPLACE_SQL_CACHE = new ConcurrentHashMap<cn.nodemedia.library.db.table.TableEntity<?>, String>();

    //*********************************************** insert sql ***********************************************

    public static SqlInfo buildInsertSqlInfo(cn.nodemedia.library.db.table.TableEntity<?> table, Object entity) throws DbException {

        List<cn.nodemedia.library.db.KeyValue> keyValueList = entity2KeyValueList(table, entity);
        if (keyValueList.size() == 0) return null;

        SqlInfo result = new SqlInfo();
        String sql = INSERT_SQL_CACHE.get(table);
        if (sql == null) {
            StringBuilder builder = new StringBuilder();
            builder.append("INSERT INTO ");
            builder.append("\"").append(table.getName()).append("\"");
            builder.append(" (");
            for (cn.nodemedia.library.db.KeyValue kv : keyValueList) {
                builder.append("\"").append(kv.key).append("\"").append(',');
            }
            builder.deleteCharAt(builder.length() - 1);
            builder.append(") VALUES (");

            int length = keyValueList.size();
            for (int i = 0; i < length; i++) {
                builder.append("?,");
            }
            builder.deleteCharAt(builder.length() - 1);
            builder.append(")");

            sql = builder.toString();
            result.setSql(sql);
            result.addBindArgs(keyValueList);
            INSERT_SQL_CACHE.put(table, sql);
        } else {
            result.setSql(sql);
            result.addBindArgs(keyValueList);
        }

        return result;
    }

    //*********************************************** replace sql ***********************************************

    public static SqlInfo buildReplaceSqlInfo(cn.nodemedia.library.db.table.TableEntity<?> table, Object entity) throws DbException {

        List<cn.nodemedia.library.db.KeyValue> keyValueList = entity2KeyValueList(table, entity);
        if (keyValueList.size() == 0) return null;

        SqlInfo result = new SqlInfo();
        String sql = REPLACE_SQL_CACHE.get(table);
        if (sql == null) {
            StringBuilder builder = new StringBuilder();
            builder.append("REPLACE INTO ");
            builder.append("\"").append(table.getName()).append("\"");
            builder.append(" (");
            for (cn.nodemedia.library.db.KeyValue kv : keyValueList) {
                builder.append("\"").append(kv.key).append("\"").append(',');
            }
            builder.deleteCharAt(builder.length() - 1);
            builder.append(") VALUES (");

            int length = keyValueList.size();
            for (int i = 0; i < length; i++) {
                builder.append("?,");
            }
            builder.deleteCharAt(builder.length() - 1);
            builder.append(")");

            sql = builder.toString();
            result.setSql(sql);
            result.addBindArgs(keyValueList);
            REPLACE_SQL_CACHE.put(table, sql);
        } else {
            result.setSql(sql);
            result.addBindArgs(keyValueList);
        }

        return result;
    }

    //*********************************************** delete sql ***********************************************

    public static SqlInfo buildDeleteSqlInfo(cn.nodemedia.library.db.table.TableEntity<?> table, Object entity) throws DbException {
        SqlInfo result = new SqlInfo();

        ColumnEntity id = table.getId();
        Object idValue = id.getColumnValue(entity);

        if (idValue == null) {
            throw new DbException("this entity[" + table.getEntityType() + "]'s id value is null");
        }
        StringBuilder builder = new StringBuilder("DELETE FROM ");
        builder.append("\"").append(table.getName()).append("\"");
        builder.append(" WHERE ").append(WhereBuilder.b(id.getName(), "=", idValue));

        result.setSql(builder.toString());

        return result;
    }

    public static SqlInfo buildDeleteSqlInfoById(cn.nodemedia.library.db.table.TableEntity<?> table, Object idValue) throws DbException {
        SqlInfo result = new SqlInfo();

        ColumnEntity id = table.getId();

        if (idValue == null) {
            throw new DbException("this entity[" + table.getEntityType() + "]'s id value is null");
        }
        StringBuilder builder = new StringBuilder("DELETE FROM ");
        builder.append("\"").append(table.getName()).append("\"");
        builder.append(" WHERE ").append(WhereBuilder.b(id.getName(), "=", idValue));

        result.setSql(builder.toString());

        return result;
    }

    public static SqlInfo buildDeleteSqlInfo(cn.nodemedia.library.db.table.TableEntity<?> table, WhereBuilder whereBuilder) throws DbException {
        StringBuilder builder = new StringBuilder("DELETE FROM ");
        builder.append("\"").append(table.getName()).append("\"");

        if (whereBuilder != null && whereBuilder.getWhereItemSize() > 0) {
            builder.append(" WHERE ").append(whereBuilder.toString());
        }

        return new SqlInfo(builder.toString());
    }

    //*********************************************** update sql ***********************************************

    public static SqlInfo buildUpdateSqlInfo(cn.nodemedia.library.db.table.TableEntity<?> table, Object entity, String... updateColumnNames) throws DbException {

        List<cn.nodemedia.library.db.KeyValue> keyValueList = entity2KeyValueList(table, entity);
        if (keyValueList.size() == 0) return null;

        HashSet<String> updateColumnNameSet = null;
        if (updateColumnNames != null && updateColumnNames.length > 0) {
            updateColumnNameSet = new HashSet<String>(updateColumnNames.length);
            Collections.addAll(updateColumnNameSet, updateColumnNames);
        }

        ColumnEntity id = table.getId();
        Object idValue = null;
        try {
            idValue = id.getColumnValue(entity);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

        if (idValue == null) {
            throw new DbException("this entity[" + table.getEntityType() + "]'s id value is null");
        }

        SqlInfo result = new SqlInfo();
        StringBuilder builder = new StringBuilder("UPDATE ");
        builder.append("\"").append(table.getName()).append("\"");
        builder.append(" SET ");
        for (cn.nodemedia.library.db.KeyValue kv : keyValueList) {
            if (updateColumnNameSet == null || updateColumnNameSet.contains(kv.key)) {
                builder.append("\"").append(kv.key).append("\"").append("=?,");
                result.addBindArg(kv);
            }
        }
        builder.deleteCharAt(builder.length() - 1);
        builder.append(" WHERE ").append(WhereBuilder.b(id.getName(), "=", idValue));

        result.setSql(builder.toString());
        return result;
    }

    public static SqlInfo buildUpdateSqlInfo(cn.nodemedia.library.db.table.TableEntity<?> table, WhereBuilder whereBuilder, cn.nodemedia.library.db.KeyValue... nameValuePairs) throws DbException {

        if (nameValuePairs == null || nameValuePairs.length == 0) return null;

        SqlInfo result = new SqlInfo();
        StringBuilder builder = new StringBuilder("UPDATE ");
        builder.append("\"").append(table.getName()).append("\"");
        builder.append(" SET ");
        for (cn.nodemedia.library.db.KeyValue kv : nameValuePairs) {
            builder.append("\"").append(kv.key).append("\"").append("=?,");
            result.addBindArg(kv);
        }
        builder.deleteCharAt(builder.length() - 1);
        if (whereBuilder != null && whereBuilder.getWhereItemSize() > 0) {
            builder.append(" WHERE ").append(whereBuilder.toString());
        }

        result.setSql(builder.toString());
        return result;
    }

    //*********************************************** others ***********************************************

    public static SqlInfo buildCreateTableSqlInfo(cn.nodemedia.library.db.table.TableEntity<?> table) throws DbException {
        ColumnEntity id = table.getId();

        StringBuilder builder = new StringBuilder();
        builder.append("CREATE TABLE IF NOT EXISTS ");
        builder.append("\"").append(table.getName()).append("\"");
        builder.append(" ( ");

        if (id.isAutoId()) {
            builder.append("\"").append(id.getName()).append("\"").append(" INTEGER PRIMARY KEY AUTOINCREMENT, ");
        } else {
            builder.append("\"").append(id.getName()).append("\"").append(id.getColumnDbType()).append(" PRIMARY KEY, ");
        }

        Collection<ColumnEntity> columns = table.getColumnMap().values();
        for (ColumnEntity column : columns) {
            if (column.isId()) continue;
            builder.append("\"").append(column.getName()).append("\"");
            builder.append(' ').append(column.getColumnDbType());
            builder.append(' ').append(column.getProperty());
            builder.append(',');
        }

        builder.deleteCharAt(builder.length() - 1);
        builder.append(" )");
        return new SqlInfo(builder.toString());
    }

    public static List<cn.nodemedia.library.db.KeyValue> entity2KeyValueList(cn.nodemedia.library.db.table.TableEntity<?> table, Object entity) throws DbException {

        Collection<ColumnEntity> columns = table.getColumnMap().values();
        List<cn.nodemedia.library.db.KeyValue> keyValueList = new ArrayList<cn.nodemedia.library.db.KeyValue>(columns.size());
        for (ColumnEntity column : columns) {
            cn.nodemedia.library.db.KeyValue kv = column2KeyValue(entity, column);
            if (kv != null) {
                keyValueList.add(kv);
            }
        }

        return keyValueList;
    }

    private static cn.nodemedia.library.db.KeyValue column2KeyValue(Object entity, ColumnEntity column) throws DbException {
        if (column.isAutoId()) {
            return null;
        }

        String key = column.getName();
        Object value = column.getFieldValue(entity);
        return new cn.nodemedia.library.db.KeyValue(key, value);
    }
}
