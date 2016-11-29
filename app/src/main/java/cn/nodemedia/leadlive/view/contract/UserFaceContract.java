package cn.nodemedia.leadlive.view.contract;

import java.io.File;

import xyz.tanwb.airship.view.BaseView;
import xyz.tanwb.airship.view.contract.PhotoPresenter;

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
        public void onPhotoSuccess(int i, File file) {

        }
    }

}
