package cn.nodemedia.leadlive.view.contract;

import xyz.tanwb.treasurechest.utils.ToastUtils;
import xyz.tanwb.treasurechest.view.BaseView;
import xyz.tanwb.treasurechest.view.contract.PermissionsPresenter;

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
            questPermissions(new String[]{"android.permission.CAMERA", "android.permission.RECORD_AUDIO", "android.permission.WRITE_EXTERNAL_STORAGE"});
        }

        @Override
        public void onPermissionsSuccess() {
            mView.startLivePublisher();
        }

        @Override
        public void onPermissionsFailure(String strMsg) {
            super.onPermissionsFailure(strMsg);
            ToastUtils.show(context, "发布视频需要访问摄像头,访问麦克风等权限.");
        }
    }
}
