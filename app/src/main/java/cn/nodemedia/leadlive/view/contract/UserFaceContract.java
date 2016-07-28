package cn.nodemedia.leadlive.view.contract;

import android.net.Uri;

import xyz.tanwb.treasurechest.view.BaseView;
import xyz.tanwb.treasurechest.view.contract.PhotoPresenter;

/**
 * No description
 * LeadLive the cn.nodemedia.leadlive.view.contract
 *
 * @author Bining
 * @since 16/7/26
 */
public interface UserFaceContract {

    interface View extends BaseView {
    }

    class Presenter extends PhotoPresenter<View> {

        @Override
        public void onStart() {

        }

        @Override
        public void onPhotoSuccess(int i, Uri uri) {

        }

    }

}
