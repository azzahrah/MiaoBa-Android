package cn.nodemedia.library.db;

import android.database.Cursor;
import android.text.TextUtils;

import cn.nodemedia.library.db.sqlite.WhereBuilder;

import java.util.ArrayList;
import java.util.List;

public final class DbModelSelector {

    private String[] columnExpressions;
    private String groupByColumnName;
    private WhereBuilder having;

    private Selector<?> selector;

    private DbModelSelector(cn.nodemedia.library.db.table.TableEntity<?> table) {
        selector = Selector.from(table);
    }

    protected DbModelSelector(Selector<?> selector, String groupByColumnName) {
        this.selector = selector;
        this.groupByColumnName = groupByColumnName;
    }

    protected DbModelSelector(Selector<?> selector, String[] columnExpressions) {
        this.selector = selector;
        this.columnExpressions = columnExpressions;
    }

    /*package*/
    static DbModelSelector from(cn.nodemedia.library.db.table.TableEntity<?> table) {
        return new DbModelSelector(table);
    }

    public DbModelSelector where(WhereBuilder whereBuilder) {
        selector.where(whereBuilder);
        return this;
    }

    public DbModelSelector where(String columnName, String op, Object value) {
        selector.where(columnName, op, value);
        return this;
    }

    public DbModelSelector and(String columnName, String op, Object value) {
        selector.and(columnName, op, value);
        return this;
    }

    public DbModelSelector and(WhereBuilder where) {
        selector.and(where);
        return this;
    }

    public DbModelSelector or(String columnName, String op, Object value) {
        selector.or(columnName, op, value);
        return this;
    }

    public DbModelSelector or(WhereBuilder where) {
        selector.or(where);
        return this;
    }

    public DbModelSelector expr(String expr) {
        selector.expr(expr);
        return this;
    }

    public DbModelSelector groupBy(String columnName) {
        this.groupByColumnName = columnName;
        return this;
    }

    public DbModelSelector having(WhereBuilder whereBuilder) {
        this.having = whereBuilder;
        return this;
    }

    public DbModelSelector select(String... columnExpressions) {
        this.columnExpressions = columnExpressions;
        return this;
    }

    public DbModelSelector orderBy(String columnName) {
        selector.orderBy(columnName);
        return this;
    }

    public DbModelSelector orderBy(String columnName, boolean desc) {
        selector.orderBy(columnName, desc);
        return this;
    }

    public DbModelSelector limit(int limit) {
        selector.limit(limit);
        return this;
    }

    public DbModelSelector offset(int offset) {
        selector.offset(offset);
        return this;
    }

    public cn.nodemedia.library.db.table.TableEntity<?> getTable() {
        return selector.getTable();
    }

    public cn.nodemedia.library.db.table.DbModel findFirst() throws DbException {
        cn.nodemedia.library.db.table.TableEntity<?> table = selector.getTable();
        if (!table.tableIsExist()) return null;

        this.limit(1);
        Cursor cursor = table.getDb().execQuery(this.toString());
        if (cursor != null) {
            try {
                if (cursor.moveToNext()) {
                    return CursorUtils.getDbModel(cursor);
                }
            } catch (Throwable e) {
                throw new DbException(e);
            } finally {
                cursor.close();
            }
        }
        return null;
    }

    public List<cn.nodemedia.library.db.table.DbModel> findAll() throws DbException {
        cn.nodemedia.library.db.table.TableEntity<?> table = selector.getTable();
        if (!table.tableIsExist()) return null;

        List<cn.nodemedia.library.db.table.DbModel> result = null;

        Cursor cursor = table.getDb().execQuery(this.toString());
        if (cursor != null) {
            try {
                result = new ArrayList<cn.nodemedia.library.db.table.DbModel>();
                while (cursor.moveToNext()) {
                    cn.nodemedia.library.db.table.DbModel entity = CursorUtils.getDbModel(cursor);
                    result.add(entity);
                }
            } catch (Throwable e) {
                throw new DbException(e);
            } finally {
                cursor.close();
            }
        }
        return result;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("SELECT ");
        if (columnExpressions != null && columnExpressions.length > 0) {
            for (String columnExpression : columnExpressions) {
                result.append(columnExpression);
                result.append(",");
            }
            result.deleteCharAt(result.length() - 1);
        } else {
            if (!TextUtils.isEmpty(groupByColumnName)) {
                result.append(groupByColumnName);
            } else {
                result.append("*");
            }
        }
        result.append(" FROM ").append("\"").append(selector.getTable().getName()).append("\"");
        WhereBuilder whereBuilder = selector.getWhereBuilder();
        if (whereBuilder != null && whereBuilder.getWhereItemSize() > 0) {
            result.append(" WHERE ").append(whereBuilder.toString());
        }
        if (!TextUtils.isEmpty(groupByColumnName)) {
            result.append(" GROUP BY ").append("\"").append(groupByColumnName).append("\"");
            if (having != null && having.getWhereItemSize() > 0) {
                result.append(" HAVING ").append(having.toString());
            }
        }
        List<Selector.OrderBy> orderByList = selector.getOrderByList();
        if (orderByList != null && orderByList.size() > 0) {
            for (int i = 0; i < orderByList.size(); i++) {
                result.append(" ORDER BY ").append(orderByList.get(i).toString()).append(',');
            }
            result.deleteCharAt(result.length() - 1);
        }
        if (selector.getLimit() > 0) {
            result.append(" LIMIT ").append(selector.getLimit());
            result.append(" OFFSET ").append(selector.getOffset());
        }
        return result.toString();
    }
}
