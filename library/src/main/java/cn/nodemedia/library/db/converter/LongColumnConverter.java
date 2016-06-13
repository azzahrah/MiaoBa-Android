package cn.nodemedia.library.db.converter;

import android.database.Cursor;

public class LongColumnConverter implements ColumnConverter<Long> {

    @Override
    public Long getFieldValue(final Cursor cursor, int index) {
        return cursor.isNull(index) ? null : cursor.getLong(index);
    }

    @Override
    public Object fieldValue2DbValue(Long fieldValue) {
        return fieldValue;
    }

    @Override
    public cn.nodemedia.library.db.sqlite.ColumnDbType getColumnDbType() {
        return cn.nodemedia.library.db.sqlite.ColumnDbType.INTEGER;
    }
}
