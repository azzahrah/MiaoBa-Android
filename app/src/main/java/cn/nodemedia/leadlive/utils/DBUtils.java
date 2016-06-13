package cn.nodemedia.leadlive.utils;

import cn.nodemedia.library.db.DbManager;
import cn.nodemedia.library.db.DbManagerImpl;

/**
 * 数据库辅助
 * Created by Bining.
 */
public enum DBUtils {

    CACHE(new DbManager.DaoConfig()
            .setDbName("warehouse9.db")
            // 不设置dbDir时, 默认存储在app的私有目录.
            //.setDbDir(new File("/sdcard")) // "sdcard"的写法并非最佳实践, 这里为了简单, 先这样写了.
            .setDbVersion(1)
            .setDbOpenListener(new DbManager.DbOpenListener() {
                @Override
                public void onDbOpened(DbManager db) {
                    // 开启WAL, 对写入加速提升巨大
                    db.getDatabase().enableWriteAheadLogging();
                }
            })
            .setDbUpgradeListener(new DbManager.DbUpgradeListener() {
                @Override
                public void onUpgrade(DbManager db, int oldVersion, int newVersion) {
                    // TODO: ...
                    // db.addColumn();
                    // db.dropTable(...);
                    // ...
                    // or
                    // db.dropDb();// 默认删除所有表
                }
            })
    );

    private DbManager.DaoConfig config;

    DBUtils(DbManager.DaoConfig config) {
        this.config = config;
    }

    public DbManager.DaoConfig getConfig() {
        return config;
    }

    public static DbManager getInstance() {
        return DbManagerImpl.getInstance(DBUtils.CACHE.getConfig());
    }

}
