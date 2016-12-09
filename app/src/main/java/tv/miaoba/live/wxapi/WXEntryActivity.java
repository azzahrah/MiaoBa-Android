package tv.miaoba.live.wxapi;

import android.content.Intent;
import android.os.Bundle;

import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;

import tv.miaoba.live.Application;
import tv.miaoba.live.view.contract.LauncherContract;
import xyz.tanwb.airship.rxjava.RxBus;
import xyz.tanwb.airship.utils.Log;
import xyz.tanwb.airship.utils.ToastUtils;
import xyz.tanwb.airship.view.BaseActivity;

/**
 * 微信登录结果返回
 * Created by Bining.
 */
public class WXEntryActivity extends BaseActivity implements IWXAPIEventHandler {

    @Override
    public int getLayoutId() {
        return 0;
    }

    @Override
    public void initView(Bundle bundle) {
        Log.e("微信登录结果返回");
        IWXAPI mWeixinAPI = ((Application) mApplication).getIWXAPI();
        if (mWeixinAPI != null) {
            mWeixinAPI.handleIntent(getIntent(), this);
        }
    }

    @Override
    public void initPresenter() {
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        initView(null);
    }

    @Override
    public void onReq(BaseReq baseReq) {
        //接收微信发送的请求
        Log.e("onReq 回执类型：" + baseReq.getType() + " 回执编码：" + baseReq.transaction);
    }

    @Override
    public void onResp(BaseResp baseResp) {
        //接收发送到微信请求的响应结果
        Log.e("onResp 回执类型：" + baseResp.getType() + " 回执编码：" + baseResp.errCode);
        if (baseResp.getType() == ConstantsAPI.COMMAND_SENDAUTH) {
            switch (baseResp.errCode) {
                case BaseResp.ErrCode.ERR_OK:
                    SendAuth.Resp resp = new SendAuth.Resp(getIntent().getExtras());
                    String code = resp.code;
                    Log.e("onResp:" + code);
                    RxBus.$().post(LauncherContract.class.getName(), code);
                    break;
                case BaseResp.ErrCode.ERR_USER_CANCEL:
                    ToastUtils.show(mActivity, "您已取消登陆.");
                    break;
                case BaseResp.ErrCode.ERR_AUTH_DENIED:
                    ToastUtils.show(mActivity, "您已拒绝授权.");
                    break;
                default:
                    ToastUtils.show(mActivity, "微信登陆异常.");
                    break;
            }
        }
        exit();
    }

    @Override
    public boolean hasSwipeFinish() {
        return false;
    }

}
