package cn.nodemedia.leadlive.view.contract;

import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.File;

import cn.nodemedia.leadlive.R;
import xyz.tanwb.airship.view.BaseView;
import xyz.tanwb.airship.view.contract.PhotoPresenter;

public interface UserFaceContract {

    interface View extends BaseView {

        ImageView getFaceView();
    }

    class Presenter extends PhotoPresenter<View> {

        private ImageView faceView;

        @Override
        public void onStart() {
            faceView = mView.getFaceView();
        }

        @Override
        public void onPhotoSuccess(int i, File file) {
            Glide.with(mActivity).load(file).asBitmap().error(R.drawable.mr_720).into(faceView);
        }
    }

}
