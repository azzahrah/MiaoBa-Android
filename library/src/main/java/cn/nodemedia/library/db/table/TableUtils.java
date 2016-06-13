package cn.nodemedia.library.db.table;

import cn.nodemedia.library.db.converter.ColumnConverterFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.LinkedHashMap;

/* package */
final class TableUtils {

    private TableUtils() {
    }

    /* package */
    static synchronized LinkedHashMap<String, ColumnEntity> findColumnMap(Class<?> entityType) throws Throwable {
        LinkedHashMap<String, ColumnEntity> columnMap = new LinkedHashMap<String, ColumnEntity>();
        addColumns2Map(entityType, columnMap);
        return columnMap;
    }

    private static void addColumns2Map(Class<?> entityType, HashMap<String, ColumnEntity> columnMap) throws Throwable {
        if (Object.class.equals(entityType)) return;

        Field[] fields = entityType.getDeclaredFields();
        for (Field field : fields) {
            int modify = field.getModifiers();
            if (Modifier.isStatic(modify) || Modifier.isTransient(modify)) {
                continue;
            }
            cn.nodemedia.library.db.annotation.Column columnAnn = field.getAnnotation(cn.nodemedia.library.db.annotation.Column.class);
            if (columnAnn != null) {
                if (ColumnConverterFactory.isSupportColumnConverter(field.getType())) {
                    ColumnEntity column = new ColumnEntity(entityType, field, columnAnn);
                    if (!columnMap.containsKey(column.getName())) {
                        columnMap.put(column.getName(), column);
                    }
                }
            }
        }

        addColumns2Map(entityType.getSuperclass(), columnMap);
    }
}
