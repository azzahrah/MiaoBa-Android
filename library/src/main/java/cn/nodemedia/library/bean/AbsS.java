package cn.nodemedia.library.bean;

public class AbsS extends Abs {

    public AbsS() {
    }

    public AbsS(String target) {
        this("1", null, target);
    }

    public AbsS(String errCode, String message) {
        this(errCode, message, null);
    }

    public AbsS(String errCode, String message, String target) {
        this.errCode = errCode;
        this.message = message;
        this.target = target;
    }

    public String target;

    @Override
    public boolean isSuccess() {
        return super.isSuccess();
    }

}
