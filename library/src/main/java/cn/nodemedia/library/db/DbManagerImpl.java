package cn.nodemedia.library.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;

import cn.nodemedia.library.App;
import cn.nodemedia.library.db.table.ColumnEntity;
import cn.nodemedia.library.db.table.DbBase;
import cn.nodemedia.library.rxjava.RxBus;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public final class DbManagerImpl extends DbBase {

    // ********************************************************
    // create instance
    // ********************************************************

    /**
     * key: dbName
     */
    private final static HashMap<DaoConfig, DbManagerImpl> DAO_MAP = new HashMap<DaoConfig, DbManagerImpl>();

    private SQLiteDatabase database;
    private DaoConfig daoConfig;
    private boolean allowTransaction;

    private DbManagerImpl(DaoConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("daoConfig may not be null");
        }
        this.daoConfig = config;
        this.allowTransaction = config.isAllowTransaction();
        this.database = openOrCreateDatabase(config);
        DbOpenListener dbOpenListener = config.getDbOpenListener();
        if (dbOpenListener != null) {
            dbOpenListener.onDbOpened(this);
        }
    }

    public synchronized static DbManager getInstance(DaoConfig daoConfig) {

        if (daoConfig == null) {//使用默认配置
            daoConfig = new DaoConfig();
        }

        DbManagerImpl dao = DAO_MAP.get(daoConfig);
        if (dao == null) {
            dao = new DbManagerImpl(daoConfig);
            DAO_MAP.put(daoConfig, dao);
        } else {
            dao.daoConfig = daoConfig;
        }

        // update the database if needed
        SQLiteDatabase database = dao.database;
        int oldVersion = database.getVersion();
        int newVersion = daoConfig.getDbVersion();
        if (oldVersion != newVersion) {
            if (oldVersion != 0) {
                DbUpgradeListener upgradeListener = daoConfig.getDbUpgradeListener();
                if (upgradeListener != null) {
                    upgradeListener.onUpgrade(dao, oldVersion, newVersion);
                } else {
                    try {
                        dao.dropDb();
                    } catch (DbException e) {

                    }
                }
            }
            database.setVersion(newVersion);
        }

        return dao;
    }

    @Override
    public SQLiteDatabase getDatabase() {
        return database;
    }

    @Override
    public DaoConfig getDaoConfig() {
        return daoConfig;
    }

    // ********************************************************
    // operations
    // ********************************************************

    @Override
    public void saveOrUpdate(Object entity) throws DbException {
        try {
            beginTransaction();

            if (entity instanceof List) {
                List<?> entities = (List<?>) entity;
                if (entities.isEmpty())
                    return;
                cn.nodemedia.library.db.table.TableEntity<?> table = this.getTable(entities.get(0).getClass());
                createTableIfNotExist(table);
                for (Object item : entities) {
                    saveOrUpdateWithoutTransaction(table, item);
                }
            } else {
                cn.nodemedia.library.db.table.TableEntity<?> table = this.getTable(entity.getClass());
                createTableIfNotExist(table);
                saveOrUpdateWithoutTransaction(table, entity);
            }

            setTransactionSuccessful();
            // EventBus.getDefault().post(new EventBusInfo(entity.getClass().getName()));
            RxBus.$().post(entity.getClass().getName());
        } finally {
            endTransaction();
        }
    }

    @Override
    public void replace(Object entity) throws DbException {
        try {
            beginTransaction();

            if (entity instanceof List) {
                List<?> entities = (List<?>) entity;
                if (entities.isEmpty()) return;
                cn.nodemedia.library.db.table.TableEntity<?> table = this.getTable(entities.get(0).getClass());
                createTableIfNotExist(table);
                for (Object item : entities) {
                    execNonQuery(cn.nodemedia.library.db.sqlite.SqlInfoBuilder.buildReplaceSqlInfo(table, item));
                }
            } else {
                cn.nodemedia.library.db.table.TableEntity<?> table = this.getTable(entity.getClass());
                createTableIfNotExist(table);
                execNonQuery(cn.nodemedia.library.db.sqlite.SqlInfoBuilder.buildReplaceSqlInfo(table, entity));
            }

            setTransactionSuccessful();
            // EventBus.getDefault().post(new EventBusInfo(entity.getClass().getName()));
            RxBus.$().post(entity.getClass().getName());
        } finally {
            endTransaction();
        }
    }

    @Override
    public void save(Object entity) throws DbException {
        try {
            beginTransaction();

            if (entity instanceof List) {
                List<?> entities = (List<?>) entity;
                if (entities.isEmpty()) return;
                cn.nodemedia.library.db.table.TableEntity<?> table = this.getTable(entities.get(0).getClass());
                createTableIfNotExist(table);
                for (Object item : entities) {
                    execNonQuery(cn.nodemedia.library.db.sqlite.SqlInfoBuilder.buildInsertSqlInfo(table, item));
                }
            } else {
                cn.nodemedia.library.db.table.TableEntity<?> table = this.getTable(entity.getClass());
                createTableIfNotExist(table);
                execNonQuery(cn.nodemedia.library.db.sqlite.SqlInfoBuilder.buildInsertSqlInfo(table, entity));
            }

            setTransactionSuccessful();
            // EventBus.getDefault().post(new EventBusInfo(entity.getClass().getName()));
            RxBus.$().post(entity.getClass().getName());
        } finally {
            endTransaction();
        }
    }

    @Override
    public boolean saveBindingId(Object entity) throws DbException {
        boolean result = false;
        try {
            beginTransaction();

            if (entity instanceof List) {
                List<?> entities = (List<?>) entity;
                if (entities.isEmpty()) return false;
                cn.nodemedia.library.db.table.TableEntity<?> table = this.getTable(entities.get(0).getClass());
                createTableIfNotExist(table);
                for (Object item : entities) {
                    if (!saveBindingIdWithoutTransaction(table, item)) {
                        throw new DbException("saveBindingId error, transaction will not commit!");
                    }
                }
            } else {
                cn.nodemedia.library.db.table.TableEntity<?> table = this.getTable(entity.getClass());
                createTableIfNotExist(table);
                result = saveBindingIdWithoutTransaction(table, entity);
            }

            setTransactionSuccessful();
            // EventBus.getDefault().post(new EventBusInfo(entity.getClass().getName()));
            RxBus.$().post(entity.getClass().getName());
        } finally {
            endTransaction();
        }
        return result;
    }

    @Override
    public void deleteById(Class<?> entityType, Object idValue) throws DbException {
        cn.nodemedia.library.db.table.TableEntity<?> table = this.getTable(entityType);
        if (!table.tableIsExist()) return;
        try {
            beginTransaction();

            execNonQuery(cn.nodemedia.library.db.sqlite.SqlInfoBuilder.buildDeleteSqlInfoById(table, idValue));

            setTransactionSuccessful();
            // EventBus.getDefault().post(new EventBusInfo(entityType.getName()));
            RxBus.$().post(entityType.getName());
        } finally {
            endTransaction();
        }
    }

    @Override
    public void delete(Object entity) throws DbException {
        try {
            beginTransaction();

            if (entity instanceof List) {
                List<?> entities = (List<?>) entity;
                if (entities.isEmpty()) return;
                cn.nodemedia.library.db.table.TableEntity<?> table = this.getTable(entities.get(0).getClass());
                if (!table.tableIsExist()) return;
                for (Object item : entities) {
                    execNonQuery(cn.nodemedia.library.db.sqlite.SqlInfoBuilder.buildDeleteSqlInfo(table, item));
                }
            } else {
                cn.nodemedia.library.db.table.TableEntity<?> table = this.getTable(entity.getClass());
                if (!table.tableIsExist()) return;
                execNonQuery(cn.nodemedia.library.db.sqlite.SqlInfoBuilder.buildDeleteSqlInfo(table, entity));
            }

            setTransactionSuccessful();
            // EventBus.getDefault().post(new EventBusInfo(entity.getClass().getName()));
            RxBus.$().post(entity.getClass().getName());
        } finally {
            endTransaction();
        }
    }

    @Override
    public void delete(Class<?> entityType) throws DbException {
        delete(entityType, null);
    }

    @Override
    public int delete(Class<?> entityType, cn.nodemedia.library.db.sqlite.WhereBuilder whereBuilder) throws DbException {
        cn.nodemedia.library.db.table.TableEntity<?> table = this.getTable(entityType);
        if (!table.tableIsExist()) return 0;
        int result = 0;
        try {
            beginTransaction();

            result = executeUpdateDelete(cn.nodemedia.library.db.sqlite.SqlInfoBuilder.buildDeleteSqlInfo(table, whereBuilder));

            setTransactionSuccessful();
            // EventBus.getDefault().post(new EventBusInfo(entityType.getName()));
            RxBus.$().post(entityType.getName());
        } finally {
            endTransaction();
        }
        return result;
    }

    @Override
    public void update(Object entity, String... updateColumnNames) throws DbException {
        try {
            beginTransaction();

            if (entity instanceof List) {
                List<?> entities = (List<?>) entity;
                if (entities.isEmpty()) return;
                cn.nodemedia.library.db.table.TableEntity<?> table = this.getTable(entities.get(0).getClass());
                if (!table.tableIsExist()) return;
                for (Object item : entities) {
                    execNonQuery(cn.nodemedia.library.db.sqlite.SqlInfoBuilder.buildUpdateSqlInfo(table, item, updateColumnNames));
                }
            } else {
                cn.nodemedia.library.db.table.TableEntity<?> table = this.getTable(entity.getClass());
                if (!table.tableIsExist()) return;
                execNonQuery(cn.nodemedia.library.db.sqlite.SqlInfoBuilder.buildUpdateSqlInfo(table, entity, updateColumnNames));
            }

            setTransactionSuccessful();
            // EventBus.getDefault().post(new EventBusInfo(entity.getClass().getName()));
            RxBus.$().post(entity.getClass().getName());
        } finally {
            endTransaction();
        }
    }

    @Override
    public int update(Class<?> entityType, cn.nodemedia.library.db.sqlite.WhereBuilder whereBuilder, KeyValue... nameValuePairs) throws DbException {
        cn.nodemedia.library.db.table.TableEntity<?> table = this.getTable(entityType);
        if (!table.tableIsExist()) return 0;

        int result = 0;
        try {
            beginTransaction();

            result = executeUpdateDelete(cn.nodemedia.library.db.sqlite.SqlInfoBuilder.buildUpdateSqlInfo(table, whereBuilder, nameValuePairs));

            setTransactionSuccessful();
            // EventBus.getDefault().post(new EventBusInfo(entityType.getName()));
            RxBus.$().post(entityType.getName());
        } finally {
            endTransaction();
        }

        return result;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T findById(Class<T> entityType, Object idValue) throws DbException {
        cn.nodemedia.library.db.table.TableEntity<T> table = this.getTable(entityType);
        if (!table.tableIsExist()) return null;

        Selector selector = Selector.from(table).where(table.getId().getName(), "=", idValue);

        String sql = selector.limit(1).toString();
        Cursor cursor = execQuery(sql);
        if (cursor != null) {
            try {
                if (cursor.moveToNext()) {
                    return CursorUtils.getEntity(table, cursor);
                }
            } catch (Throwable e) {
                throw new DbException(e);
            } finally {
                cursor.close();
            }
        }
        return null;
    }

    @Override
    public <T> T findFirst(Class<T> entityType) throws DbException {
        return this.selector(entityType).findFirst();
    }

    @Override
    public <T> List<T> findAll(Class<T> entityType) throws DbException {
        return this.selector(entityType).findAll();
    }

    @Override
    public <T> Selector<T> selector(Class<T> entityType) throws DbException {
        return Selector.from(this.getTable(entityType));
    }

    @Override
    public cn.nodemedia.library.db.table.DbModel findDbModelFirst(cn.nodemedia.library.db.sqlite.SqlInfo sqlInfo) throws DbException {
        Cursor cursor = execQuery(sqlInfo);
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

    @Override
    public List<cn.nodemedia.library.db.table.DbModel> findDbModelAll(cn.nodemedia.library.db.sqlite.SqlInfo sqlInfo) throws DbException {
        List<cn.nodemedia.library.db.table.DbModel> dbModelList = new ArrayList<cn.nodemedia.library.db.table.DbModel>();

        Cursor cursor = execQuery(sqlInfo);
        if (cursor != null) {
            try {
                while (cursor.moveToNext()) {
                    dbModelList.add(CursorUtils.getDbModel(cursor));
                }
            } catch (Throwable e) {
                throw new DbException(e);
            } finally {
                cursor.close();
            }
        }
        return dbModelList;
    }

    // ********************************************************
    // config
    // ********************************************************

    private SQLiteDatabase openOrCreateDatabase(DaoConfig config) {
        SQLiteDatabase result;

        File dbDir = config.getDbDir();
        if (dbDir != null && (dbDir.exists() || dbDir.mkdirs())) {
            File dbFile = new File(dbDir, config.getDbName());
            result = SQLiteDatabase.openOrCreateDatabase(dbFile, null);
        } else {
            result = App.app().openOrCreateDatabase(config.getDbName(), 0, null);
        }
        return result;
    }

    // ********************************************************
    // private operations with out transaction
    // ********************************************************

    private void saveOrUpdateWithoutTransaction(cn.nodemedia.library.db.table.TableEntity<?> table, Object entity) throws DbException {
        ColumnEntity id = table.getId();
        if (id.isAutoId()) {
            if (id.getColumnValue(entity) != null) {
                execNonQuery(cn.nodemedia.library.db.sqlite.SqlInfoBuilder.buildUpdateSqlInfo(table, entity));
            } else {
                saveBindingIdWithoutTransaction(table, entity);
            }
        } else {
            execNonQuery(cn.nodemedia.library.db.sqlite.SqlInfoBuilder.buildReplaceSqlInfo(table, entity));
        }
    }

    private boolean saveBindingIdWithoutTransaction(cn.nodemedia.library.db.table.TableEntity<?> table, Object entity) throws DbException {
        ColumnEntity id = table.getId();
        if (id.isAutoId()) {
            execNonQuery(cn.nodemedia.library.db.sqlite.SqlInfoBuilder.buildInsertSqlInfo(table, entity));
            long idValue = getLastAutoIncrementId(table.getName());
            if (idValue == -1) {
                return false;
            }
            id.setAutoIdValue(entity, idValue);
            return true;
        } else {
            execNonQuery(cn.nodemedia.library.db.sqlite.SqlInfoBuilder.buildInsertSqlInfo(table, entity));
            return true;
        }
    }

    // ********************************************************
    // tools
    // ********************************************************

    private long getLastAutoIncrementId(String tableName) throws DbException {
        long id = -1;
        Cursor cursor = execQuery("SELECT seq FROM sqlite_sequence WHERE name='" + tableName + "' LIMIT 1");
        if (cursor != null) {
            try {
                if (cursor.moveToNext()) {
                    id = cursor.getLong(0);
                }
            } catch (Throwable e) {
                throw new DbException(e);
            } finally {
                cursor.close();
            }
        }
        return id;
    }

    @Override
    public void close() throws IOException {
        if (DAO_MAP.containsKey(daoConfig)) {
            DAO_MAP.remove(daoConfig);
            this.database.close();
        }
    }

    // ********************************************************
    // exec sql
    // ********************************************************

    private void beginTransaction() {
        if (allowTransaction) {
            database.beginTransaction();
        }
    }

    private void setTransactionSuccessful() {
        if (allowTransaction) {
            database.setTransactionSuccessful();
        }
    }

    private void endTransaction() {
        if (allowTransaction) {
            database.endTransaction();
        }
    }

    @Override
    public int executeUpdateDelete(cn.nodemedia.library.db.sqlite.SqlInfo sqlInfo) throws DbException {
        SQLiteStatement statement = null;
        try {
            statement = sqlInfo.buildStatement(database);
            return statement.executeUpdateDelete();
        } catch (Throwable e) {
            throw new DbException(e);
        } finally {
            if (statement != null) {
                statement.releaseReference();
            }
        }
    }

    @Override
    public int executeUpdateDelete(String sql) throws DbException {
        SQLiteStatement statement = null;
        try {
            statement = database.compileStatement(sql);
            return statement.executeUpdateDelete();
        } catch (Throwable e) {
            throw new DbException(e);
        } finally {
            if (statement != null) {
                statement.releaseReference();
            }
        }
    }

    @Override
    public void execNonQuery(cn.nodemedia.library.db.sqlite.SqlInfo sqlInfo) throws DbException {
        SQLiteStatement statement = null;
        try {
            statement = sqlInfo.buildStatement(database);
            statement.execute();
        } catch (Throwable e) {
            throw new DbException(e);
        } finally {
            if (statement != null) {
                statement.releaseReference();
            }
        }
    }

    @Override
    public void execNonQuery(String sql) throws DbException {
        try {
            database.execSQL(sql);
        } catch (Throwable e) {
            throw new DbException(e);
        }
    }

    @Override
    public Cursor execQuery(cn.nodemedia.library.db.sqlite.SqlInfo sqlInfo) throws DbException {
        try {
            return database.rawQuery(sqlInfo.getSql(), sqlInfo.getBindArgsAsStrArray());
        } catch (Throwable e) {
            throw new DbException(e);
        }
    }

    @Override
    public Cursor execQuery(String sql) throws DbException {
        try {
            return database.rawQuery(sql, null);
        } catch (Throwable e) {
            throw new DbException(e);
        }
    }

}
