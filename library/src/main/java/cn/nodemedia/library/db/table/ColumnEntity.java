package cn.nodemedia.library.db.table;

import android.database.Cursor;

import cn.nodemedia.library.db.DbException;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public final class ColumnEntity {

    protected final String name;
    private final String property;
    private final boolean isId;
    private final boolean isAutoId;

    protected final Method getMethod;
    protected final Method setMethod;

    protected final Field columnField;
    protected final cn.nodemedia.library.db.converter.ColumnConverter columnConverter;

    /* package */ ColumnEntity(Class<?> entityType, Field field, cn.nodemedia.library.db.annotation.Column column) {
        field.setAccessible(true);

        this.columnField = field;
        this.name = column.name();
        this.property = column.property();
        this.isId = column.isId();

        Class<?> fieldType = field.getType();
        this.isAutoId = this.isId && column.autoGen() && ColumnUtils.isAutoIdType(fieldType);
        this.columnConverter = cn.nodemedia.library.db.converter.ColumnConverterFactory.getColumnConverter(fieldType);


        this.getMethod = ColumnUtils.findGetMethod(entityType, field);
        if (this.getMethod != null && !this.getMethod.isAccessible()) {
            this.getMethod.setAccessible(true);
        }
        this.setMethod = ColumnUtils.findSetMethod(entityType, field);
        if (this.setMethod != null && !this.setMethod.isAccessible()) {
            this.setMethod.setAccessible(true);
        }
    }

    public void setValueFromCursor(Object entity, Cursor cursor, int index) throws DbException {
        Object value = columnConverter.getFieldValue(cursor, index);
        if (value == null) return;
        try {
            if (setMethod != null) {
                setMethod.invoke(entity, value);
            } else {
                this.columnField.set(entity, value);
            }
        } catch (Exception e) {
            throw new DbException(e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public Object getColumnValue(Object entity) throws DbException {
        Object fieldValue = getFieldValue(entity);
        if (this.isAutoId && (fieldValue.equals(0L) || fieldValue.equals(0))) {
            return null;
        }
        return columnConverter.fieldValue2DbValue(fieldValue);
    }

    public void setAutoIdValue(Object entity, long value) throws DbException {
        Object idValue = value;
        if (ColumnUtils.isInteger(columnField.getType())) {
            idValue = (int) value;
        }
        try {
            if (setMethod != null) {
                setMethod.invoke(entity, idValue);
            } else {
                this.columnField.set(entity, idValue);
            }
        } catch (Exception e) {
            throw new DbException(e.getMessage());
        }
    }

    public Object getFieldValue(Object entity) throws DbException {
        Object fieldValue = null;
        try {
            if (entity != null) {
                if (getMethod != null) {
                    fieldValue = getMethod.invoke(entity);
                } else {
                    fieldValue = this.columnField.get(entity);
                }
            }
        } catch (Exception e) {
            throw new DbException(e.getMessage());
        }
        return fieldValue;
    }

    public String getName() {
        return name;
    }

    public String getProperty() {
        return property;
    }

    public boolean isId() {
        return isId;
    }

    public boolean isAutoId() {
        return isAutoId;
    }

    public Field getColumnField() {
        return columnField;
    }

    public cn.nodemedia.library.db.converter.ColumnConverter getColumnConverter() {
        return columnConverter;
    }

    public cn.nodemedia.library.db.sqlite.ColumnDbType getColumnDbType() {
        return columnConverter.getColumnDbType();
    }

    @Override
    public String toString() {
        return name;
    }
}
