package cn.nodemedia.library.model;

/**
 * 基于Rxjava的事件总线
 * Created by Bining.
 * <p/>
 * 在Android studio中使用
 * 导入：rxjava和rxandroid
 */
public class RxBus {

//    private static RxBus instance;
//
//    public static synchronized RxBus get() {
//        if (instance == null) {
//            synchronized (RxBus.class) {
//                if (instance == null) {
//                    instance = new RxBus();
//                }
//            }
//        }
//        return instance;
//    }
//
//    private RxBus() {
//    }
//
//    private ConcurrentHashMap<Object, List<Subject>> subjectMapper = new ConcurrentHashMap<Object, List<Subject>>();
//
//    @SuppressWarnings("unchecked")
//    public <T> Observable<T> register(@NonNull Object tag, @NonNull Class<T> clazz) {
//        List<Subject> subjectList = subjectMapper.get(tag);
//        if (null == subjectList) {
//            subjectList = new ArrayList<Subject>();
//            subjectMapper.put(tag, subjectList);
//        }
//
//        Subject<T, T> subject;
//        subjectList.add(subject = PublishSubject.create());
//        //subject.observeOn(AndroidSchedulers.mainThread());
//        Log.d("[register]subjectMapper: " + subjectMapper);
//        return subject;
//    }
//
//    public void unregister(@NonNull Object tag, @NonNull Observable observable) {
//        List<Subject> subjects = subjectMapper.get(tag);
//        if (null != subjects) {
//            subjects.remove(observable);
//            if (subjects.size() == 0) {
//                subjectMapper.remove(tag);
//            }
//        }
//        Log.d("[unregister]subjectMapper: " + subjectMapper);
//    }
//
//    public void post(@NonNull Object content) {
//        post(content.getClass().getName(), content);
//    }
//
//    @SuppressWarnings("unchecked")
//    public void post(@NonNull Object tag, @NonNull Object content) {
//        List<Subject> subjectList = subjectMapper.get(tag);
//        if (subjectList != null && subjectList.size() > 0) {
//            for (Subject subject : subjectList) {
//                subject.onNext(content);
//            }
//        }
//        Log.d("[send]subjectMapper: " + subjectMapper);
//    }


//    private static RxBus instance;
//
//    public static synchronized RxBus $() {
//        if (null == instance) {
//            instance = new RxBus();
//        }
//        return instance;
//    }
//
//    private RxBus() {
//    }
//
//    @SuppressWarnings("rawtypes")
//    private ConcurrentHashMap<Object, List<Subject>> subjectMapper = new ConcurrentHashMap<Object, List<Subject>>();
//
//    /**
//     * 订阅事件源
//     *
//     * @param mObservable
//     * @param mAction1
//     * @return
//     */
//    public RxBus OnEvent(Observable<?> mObservable, Action1<Object> mAction1) {
//        mObservable.observeOn(AndroidSchedulers.mainThread()).subscribe(mAction1, (e) -> e.printStackTrace());
//        return $();
//    }
//
//    /**
//     * 注册事件源
//     *
//     * @param tag
//     * @return
//     */
//    @SuppressWarnings({"rawtypes"})
//    public <T> Observable<T> register(@NonNull Object tag) {
//        List<Subject> subjectList = subjectMapper.get(tag);
//        if (null == subjectList) {
//            subjectList = new ArrayList<Subject>();
//            subjectMapper.put(tag, subjectList);
//        }
//        Subject<T, T> subject;
//        subjectList.add(subject = PublishSubject.create());
//        LogUtil.d("register", tag + "  size:" + subjectList.size());
//        return subject;
//    }
//
//    @SuppressWarnings("rawtypes")
//    public void unregister(@NonNull Object tag) {
//        List<Subject> subjects = subjectMapper.get(tag);
//        if (null != subjects) {
//            subjectMapper.remove(tag);
//        }
//    }
//
//    /**
//     * 取消监听
//     *
//     * @param tag
//     * @param observable
//     * @return
//     */
//    @SuppressWarnings("rawtypes")
//    public RxBus unregister(@NonNull Object tag,
//                            @NonNull Observable<?> observable) {
//        if (null == observable)
//            return $();
//        List<Subject> subjects = subjectMapper.get(tag);
//        if (null != subjects) {
//            subjects.remove((Subject<?, ?>) observable);
//            if (isEmpty(subjects)) {
//                subjectMapper.remove(tag);
//                LogUtil.d("unregister", tag + "  size:" + subjects.size());
//            }
//        }
//        return $();
//    }
//
//    public void post(@NonNull Object content) {
//        post(content.getClass().getName(), content);
//    }
//
//    /**
//     * 触发事件
//     *
//     * @param content
//     */
//    @SuppressWarnings({"unchecked", "rawtypes"})
//    public void post(@NonNull Object tag, @NonNull Object content) {
//        LogUtil.d("post", "eventName: " + tag);
//        List<Subject> subjectList = subjectMapper.get(tag);
//        if (!isEmpty(subjectList)) {
//            for (Subject subject : subjectList) {
//                subject.onNext(content);
//                LogUtil.d("onEvent", "eventName: " + tag);
//            }
//        }
//    }
//
//    @SuppressWarnings("rawtypes")
//    public static boolean isEmpty(Collection<Subject> collection) {
//        return null == collection || collection.isEmpty();
//    }

}
