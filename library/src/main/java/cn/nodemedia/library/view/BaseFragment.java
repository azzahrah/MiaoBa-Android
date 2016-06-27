package cn.nodemedia.library.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;

public class BaseFragment extends Fragment {

    protected BaseActivity mActivity;

    /**
     * @param vh 传入VH的类名
     * @return
     */
    public static BaseFragment newInstance(Class<?> vh, String type) {
        Bundle arguments = new Bundle();
        arguments.putString("vh", vh.getCanonicalName());
        arguments.putString("type", type);
        BaseFragment fragment = new BaseFragment();
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = (BaseActivity) getActivity();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        String type = getArguments().getString("type");
        Class<?> cla = forName(getArguments().getString("vh"));
        return null;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (mActivity == null) {
            mActivity = (BaseActivity) getActivity();
        }
    }

    public void onResume() {
        super.onResume();
    }

    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

    public void onDestroy() {
        super.onDestroy();
    }

    public Class<?> forName(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
