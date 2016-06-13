package cn.nodemedia.library.db.converter;

import android.database.Cursor;

public interface ColumnConverter<T> {

    T getFieldValue(final Cursor cursor, int index);

    Object fieldValue2DbValue(T fieldValue);

    cn.nodemedia.library.db.sqlite.ColumnDbType getColumnDbType();
}
