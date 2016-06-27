package cn.nodemedia.leadlive.view;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import cn.nodemedia.leadlive.R;
import cn.nodemedia.library.utils.PhotoUtils;
import cn.nodemedia.library.utils.ScreenUtils;

/**
 * 设置头像
 * Created by Bining.
 */
public class UserFaceActivity extends ActionbarActivity {

    @InjectView(R.id.face_preview)
    ImageView facePreview;

    private String faces;
    private Uri photoUri = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            faces = savedInstanceState.getString("p0", "");
        } else {
            faces = getIntent().getStringExtra("p0");
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("p0", faces);
        super.onSaveInstanceState(outState);
    }

    @Override
    public int getContentView() {
        return R.layout.activity_userface;
    }

    @Override
    public void initView() {
        super.initView();
        ButterKnife.inject(this);
        setTitle("设置头像");
        int width = ScreenUtils.getScreenWidth();
        facePreview.setLayoutParams(new LinearLayout.LayoutParams(width, width));
        Glide.with(mActivity).load(faces).asBitmap().error(R.drawable.mr_720).into(facePreview);
    }

    @Override
    public void initPresenter() {
    }

    @OnClick({R.id.face_source_album, R.id.face_source_photo})
    public void onClick(View v) {
        if (!isCanClick(v)) return;
        switch (v.getId()) {
            case R.id.face_source_album:
                PhotoUtils.openImageChoice(mActivity);
                break;
            case R.id.face_source_photo:
                photoUri = PhotoUtils.getSysDCIM();
                PhotoUtils.openSystemCamera(mActivity, photoUri);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case PhotoUtils.REQUEST_CODE_IMAGE:
                    photoUri = PhotoUtils.openPhotoCut(mActivity, data.getData());
                    break;
                case PhotoUtils.REQUEST_CODE_CAMERA:
                    if (data != null && data.getData() != null) {
                        photoUri = data.getData();
                    }
                    photoUri = PhotoUtils.openPhotoCut(mActivity, photoUri);
                    break;
                case PhotoUtils.REQUEST_CODE_PHOTOCUT:
                    try {
                        //showProgress();
                        //String filePath = ImageUtils.getPath(mActivity, photoUri);
//                        APIManager.upload(context, "apps", filePath, new OKHttpCallBack<FileInfo>() {
//                            @Override
//                            public void onSuccess(FileInfo fileInfo, String userAttrId) {
//                                APIManager.doSetMemImage(context, fileInfo.fileId, new OKHttpCallBack<Abs>() {
//                                    @Override
//                                    public void onSuccess(Abs abs, String userAttrId) {
//                                        if (abs.isSuccess()) {
//                                            onSucc();
//                                            getMemData();
//                                        } else {
//                                            onFail("设置头像失败,请重试.");
//                                        }
//                                    }
//
//                                    @Override
//                                    public void onFailure(String strMsg) {
//                                        super.onFailure(strMsg);
//                                        onFail(strMsg);
//                                    }
//                                });
//                            }
//
//                            @Override
//                            public void onFailure(String strMsg) {
//                                super.onFailure(strMsg);
//                                onFail("上传头像失败:" + strMsg);
//                            }
//                        });
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
