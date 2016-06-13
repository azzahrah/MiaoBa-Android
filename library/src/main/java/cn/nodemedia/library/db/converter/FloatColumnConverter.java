package cn.nodemedia.library.db.converter;

import android.database.Cursor;

public class FloatColumnConverter implements ColumnConverter<Float> {

    @Override
    public Float getFieldValue(final Cursor cursor, int index) {
        return cursor.isNull(index) ? null : cursor.getFloat(index);
    }

    @Override
    public Object fieldValue2DbValue(Float fieldValue) {
        return fieldValue;
    }

    @Override
    public cn.nodemedia.library.db.sqlite.ColumnDbType getColumnDbType() {
        return cn.nodemedia.library.db.sqlite.ColumnDbType.REAL;
    }
}
