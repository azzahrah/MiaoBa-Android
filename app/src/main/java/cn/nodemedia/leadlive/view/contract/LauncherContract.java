package cn.nodemedia.leadlive.view.contract;

import android.text.TextUtils;

import java.io.File;

import xyz.tanwb.airship.db.DBConfig;
import xyz.tanwb.airship.utils.FileUtils;
import xyz.tanwb.airship.view.BaseView;
import xyz.tanwb.airship.view.contract.PermissionsPresenter;

public interface LauncherContract {

    interface View extends BaseView {
        void startAnimation();
    }

    class Presenter extends PermissionsPresenter<View> {

        @Override
        public void onStart() {
            super.onStart();
            questManifestPermissions();
        }

        @Override
        public void onPermissionsSuccess(String[] strings) {
            initDB(FileUtils.getAppSdPath(FileUtils.PATH_DB));
        }

        @Override
        public void onPermissionsFailure(String strMsg) {
            super.onPermissionsFailure(strMsg);
            initDB(null);
        }

        private void initDB(String filePath) {
            if (!TextUtils.isEmpty(filePath)) {
                // 不设置dbDir时, 默认存储在app的私有目录.
                DBConfig.init().setDbDir(new File(filePath));
            }
            mView.startAnimation();
        }
    }

}
