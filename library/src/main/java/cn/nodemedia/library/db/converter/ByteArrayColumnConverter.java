package cn.nodemedia.library.db.converter;

import android.database.Cursor;

public class ByteArrayColumnConverter implements ColumnConverter<byte[]> {

    @Override
    public byte[] getFieldValue(final Cursor cursor, int index) {
        return cursor.isNull(index) ? null : cursor.getBlob(index);
    }

    @Override
    public Object fieldValue2DbValue(byte[] fieldValue) {
        return fieldValue;
    }

    @Override
    public cn.nodemedia.library.db.sqlite.ColumnDbType getColumnDbType() {
        return cn.nodemedia.library.db.sqlite.ColumnDbType.BLOB;
    }
}
