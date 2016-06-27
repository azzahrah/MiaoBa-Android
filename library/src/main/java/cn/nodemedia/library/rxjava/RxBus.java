package cn.nodemedia.library.rxjava;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import cn.nodemedia.library.utils.Log;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.subjects.PublishSubject;
import rx.subjects.Subject;

/**
 * 基于Rxjava的事件总线
 * Created by Bining.
 * <p/>
 * 在Android studio中使用
 * 导入：rxjava和rxandroid
 */
public class RxBus {

    private static RxBus instance;

    public static synchronized RxBus $() {
        if (null == instance) {
            instance = new RxBus();
        }
        return instance;
    }

    private RxBus() {
    }

    private ConcurrentHashMap<Object, List<Subject>> subjectMapper = new ConcurrentHashMap<>();

    /**
     * 订阅事件源
     *
     * @param mObservable
     * @param mAction1
     * @return
     */
    public RxBus OnEvent(Observable<?> mObservable, Action1<Object> mAction1) {
        mObservable.observeOn(AndroidSchedulers.mainThread()).subscribe(mAction1, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                throwable.printStackTrace();
            }
        });
        return $();
    }

    /**
     * 注册事件源
     *
     * @param tag
     * @return
     */
    @SuppressWarnings({"rawtypes"})
    public <T> Observable<T> register(@NonNull Object tag) {
        List<Subject> subjectList = subjectMapper.get(tag);
        if (null == subjectList) {
            subjectList = new ArrayList<Subject>();
            subjectMapper.put(tag, subjectList);
        }
        Subject<T, T> subject;
        subjectList.add(subject = PublishSubject.create());
        Log.d("register:" + tag + "  size:" + subjectList.size());
        return subject;
    }

    public void unregister(@NonNull Object tag) {
        List<Subject> subjects = subjectMapper.get(tag);
        if (null != subjects) {
            subjectMapper.remove(tag);
        }
    }

    /**
     * 取消监听
     *
     * @param tag
     * @param observable
     * @return
     */
    public RxBus unregister(@NonNull Object tag, @NonNull Observable<?> observable) {
        List<Subject> subjects = subjectMapper.get(tag);
        if (null != subjects) {
            subjects.remove(observable);
            if (isEmpty(subjects)) {
                subjectMapper.remove(tag);
                Log.d("unregister:" + tag + "  size:" + subjects.size());
            }
        }
        return $();
    }

    public void post(@NonNull Object content) {
        post(content, content);
    }

    /**
     * 触发事件
     *
     * @param tag
     * @param content
     */
    public void post(@NonNull Object tag, @NonNull Object content) {
        Log.d("eventName: " + tag);
        List<Subject> subjectList = subjectMapper.get(tag);
        if (!isEmpty(subjectList)) {
            for (Subject subject : subjectList) {
                subject.onNext(content);
                Log.d("eventName: " + tag);
            }
        }
    }

    public static boolean isEmpty(Collection<Subject> collection) {
        return null == collection || collection.isEmpty();
    }
}
