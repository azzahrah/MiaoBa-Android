package cn.nodemedia.leadlive.view;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;

import butterknife.BindView;
import butterknife.OnClick;
import cn.nodemedia.leadlive.R;
import cn.nodemedia.leadlive.view.contract.UserFaceContract;
import xyz.tanwb.airship.utils.ScreenUtils;

/**
 * 设置头像
 * Created by Bining.
 */
public class UserFaceActivity extends ActionbarActivity<UserFaceContract.Presenter> implements UserFaceContract.View {

    @BindView(R.id.face_preview)
    ImageView facePreview;

    private String faces;

    @Override
    public int getLayoutId() {
        return R.layout.activity_userface;
    }

    @Override
    public void initView(Bundle bundle) {
        super.initView(bundle);
        if (bundle != null) {
            faces = bundle.getString("p0", "");
        } else {
            faces = getIntent().getStringExtra("p0");
        }
        setTitle("设置头像");
        int width = ScreenUtils.getScreenWidth();
        facePreview.setLayoutParams(new LinearLayout.LayoutParams(width, width));
        Glide.with(mActivity).load(faces).asBitmap().error(R.drawable.mr_720).into(facePreview);
    }

    @Override
    public void initPresenter() {
        mPresenter.initPresenter(this);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        outState.putString("p0", faces);
    }

    @OnClick({R.id.face_source_album, R.id.face_source_photo})
    public void onClick(View v) {
        if (!isCanClick(v)) return;
        switch (v.getId()) {
            case R.id.face_source_album:
                mPresenter.startAction(1);
                break;
            case R.id.face_source_photo:
                mPresenter.startAction(2);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mPresenter.handleResult(requestCode, resultCode, data);
    }

    @Override
    public ImageView getFaceView() {
        return facePreview;
    }
}
