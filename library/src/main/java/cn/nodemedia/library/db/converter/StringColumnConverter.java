package cn.nodemedia.library.db.converter;

import android.database.Cursor;

public class StringColumnConverter implements ColumnConverter<String> {

    @Override
    public String getFieldValue(final Cursor cursor, int index) {
        return cursor.isNull(index) ? null : cursor.getString(index);
    }

    @Override
    public Object fieldValue2DbValue(String fieldValue) {
        return fieldValue;
    }

    @Override
    public cn.nodemedia.library.db.sqlite.ColumnDbType getColumnDbType() {
        return cn.nodemedia.library.db.sqlite.ColumnDbType.TEXT;
    }
}
