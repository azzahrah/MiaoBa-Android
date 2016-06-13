package cn.nodemedia.library.db.converter;

import android.database.Cursor;

public class CharColumnConverter implements ColumnConverter<Character> {

    @Override
    public Character getFieldValue(final Cursor cursor, int index) {
        return cursor.isNull(index) ? null : (char) cursor.getInt(index);
    }

    @Override
    public Object fieldValue2DbValue(Character fieldValue) {
        if (fieldValue == null) return null;
        return (int) fieldValue;
    }

    @Override
    public cn.nodemedia.library.db.sqlite.ColumnDbType getColumnDbType() {
        return cn.nodemedia.library.db.sqlite.ColumnDbType.INTEGER;
    }
}
