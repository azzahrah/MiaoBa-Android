package cn.nodemedia.leadlive.wxapi;

import android.content.Intent;

import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;

import butterknife.ButterKnife;
import cn.nodemedia.leadlive.Constants;
import cn.nodemedia.library.rxjava.RxBus;
import cn.nodemedia.library.utils.Log;
import cn.nodemedia.library.view.BaseActivity;

/**
 * 微信支付结果返回
 * Created by Bining.
 */
public class WXPayEntryActivity extends BaseActivity implements IWXAPIEventHandler {

    @Override
    public int getLayoutId() {
        return 0;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        initView();
    }

    @Override
    public void initView() {
        ButterKnife.inject(this);
        Log.e("微信支付结果返回");

        IWXAPI mWeixinAPI = mApplication.getIWXAPI();
        if (mWeixinAPI != null) {
            mWeixinAPI.handleIntent(getIntent(), this);
        }
    }

    @Override
    public void initPresenter() {
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
        if (baseResp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            RxBus.$().post(Constants.PAYMENTWXPAY, baseResp);
            Back();
        }
    }

    @Override
    public boolean hasBindServer() {
        return false;
    }

    @Override
    public boolean hasSwipeFinish() {
        return false;
    }

}
