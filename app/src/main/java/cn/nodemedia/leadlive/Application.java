package cn.nodemedia.leadlive;

import cn.nodemedia.library.BaseApplication;
import cn.nodemedia.library.glide.GlideUtils;

/**
 * Application
 * Created by Bining.
 */
public class Application extends BaseApplication {

    @Override
    public void onCreate() {
        super.onCreate();
        GlideUtils.initGlide(this, Constants.DNS_ADDRESS);
    }
}
