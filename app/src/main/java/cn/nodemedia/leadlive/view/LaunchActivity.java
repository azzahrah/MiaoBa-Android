package cn.nodemedia.leadlive.view;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;

import cn.nodemedia.leadlive.R;

/**
 * 启动页
 * Created by Bining.
 */
public class LaunchActivity extends AbsActionbarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        hasActionBar(View.GONE);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                StartActivity(LoginWayActivity.class);
                finish();
            }
        }, 1500);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
