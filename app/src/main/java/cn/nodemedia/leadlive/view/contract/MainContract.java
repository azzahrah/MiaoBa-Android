package cn.nodemedia.leadlive.view.contract;

import java.security.Permission;
import java.security.Permissions;
import java.util.jar.Manifest;

import xyz.tanwb.airship.utils.ToastUtils;
import xyz.tanwb.airship.view.BaseView;
import xyz.tanwb.airship.view.contract.PermissionsPresenter;

/**
 * No description
 * LeadLive the cn.nodemedia.leadlive.view.contract
 *
 * @author Bining
 * @since 16/7/26
 */
public interface MainContract {

    interface View extends BaseView {

        void startLivePublisher();
    }

    class Presenter extends PermissionsPresenter<View> {

        @Override
        public void onStart() {
        }

        public void questPermissions() {
            questPermissions(new String[]{android.Manifest.permission.CAMERA, android.Manifest.permission.RECORD_AUDIO, android.Manifest.permission.WRITE_EXTERNAL_STORAGE});
        }

        @Override
        public void onPermissionsSuccess(String[] strings) {
            mView.startLivePublisher();
        }

        @Override
        public void onPermissionsFailure(String strMsg) {
            super.onPermissionsFailure(strMsg);
            ToastUtils.show(mContext, "发布视频需要访问摄像头和麦克风的权限.");
        }
    }
}
