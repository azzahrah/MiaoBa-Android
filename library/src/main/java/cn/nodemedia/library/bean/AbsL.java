package cn.nodemedia.library.bean;

import java.util.List;

public class AbsL<T> extends Abs {

    // public List<T> target;
    public List<T> result;

    @Override
    public boolean isSuccess() {
        return super.isSuccess() && result != null && !result.isEmpty();
    }

}
