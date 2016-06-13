package cn.nodemedia.library.bean;

import java.util.List;

public class AbsL<T> extends Abs {

    public List<T> target;

    @Override
    public boolean isSuccess() {
        return super.isSuccess() && target != null && !target.isEmpty();
    }

}
