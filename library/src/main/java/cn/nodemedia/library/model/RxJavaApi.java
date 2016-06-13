package cn.nodemedia.library.model;

/**
 * RxJava 使用示例 Created by Bining.
 */
public class RxJavaApi {

	// 创建操作
	// 用于创建Observable的操作符
	// Create — 通过调用观察者的方法从头创建一个Observable
	// Defer — 在观察者订阅之前不创建这个Observable，为每一个观察者创建一个新的Observable
	// Empty/Never/Throw — 创建行为受限的特殊Observable
	// From — 将其它的对象或数据结构转换为Observable
	// Interval — 创建一个定时发射整数序列的Observable
	// Just — 将对象或者对象集合转换为一个会发射这些对象的Observable
	// Range — 创建发射指定范围的整数序列的Observable
	// Repeat — 创建重复发射特定的数据或数据序列的Observable
	// Start — 创建发射一个函数的返回值的Observable
	// Timer — 创建在一个指定的延迟之后发射单个数据的Observable

	// 变换操作
	// 这些操作符可用于对Observable发射的数据进行变换，详细解释可以看每个操作符的文档
	// Buffer — 缓存，可以简单的理解为缓存，它定期从Observable收集数据到一个集合，然后把这些数据集合打包发射，而不是一次发射一个
	// FlatMap —
	// 扁平映射，将Observable发射的数据变换为Observables集合，然后将这些Observable发射的数据平坦化的放进一个单独的Observable，可以认为是一个将嵌套的数据结构展开的过程。
	// GroupBy —
	// 分组，将原来的Observable分拆为Observable集合，将原始Observable发射的数据按Key分组，每一个Observable发射一组不同的数据
	// Map — 映射，通过对序列的每一项都应用一个函数变换Observable发射的数据，实质是对序列中的每一项执行一个函数，函数的参数就是这个数据项
	// Scan — 扫描，对Observable发射的每一项数据应用一个函数，然后按顺序依次发射这些值
	// Window —
	// 窗口，定期将来自Observable的数据分拆成一些Observable窗口，然后发射这些窗口，而不是每次发射一项。类似于Buffer，但Buffer发射的是数据，Window发射的是Observable，每一个Observable发射原始Observable的数据的一个子集

	// 过滤操作
	// 这些操作符用于从Observable发射的数据中进行选择
	// Debounce — 只有在空闲了一段时间后才发射数据，通俗的说，就是如果一段时间没有操作，就执行一次操作
	// Distinct — 去重，过滤掉重复数据项
	// ElementAt — 取值，取特定位置的数据项
	// Filter — 过滤，过滤掉没有通过谓词测试的数据项，只发射通过测试的
	// First — 首项，只发射满足条件的第一条数据
	// IgnoreElements — 忽略所有的数据，只保留终止通知(onError或onCompleted)
	// Last — 末项，只发射最后一条数据
	// Sample — 取样，定期发射最新的数据，等于是数据抽样，有的实现里叫ThrottleFirst
	// Skip — 跳过前面的若干项数据
	// SkipLast — 跳过后面的若干项数据
	// Take — 只保留前面的若干项数据
	// TakeLast — 只保留后面的若干项数据

	// 组合操作
	// 组合操作符用于将多个Observable组合成一个单一的Observable
	// And/Then/When — 通过模式(And条件)和计划(Then次序)组合两个或多个Observable发射的数据集
	// CombineLatest —
	// 当两个Observables中的任何一个发射了一个数据时，通过一个指定的函数组合每个Observable发射的最新数据（一共两个数据），然后发射这个函数的结果
	// Join —
	// 无论何时，如果一个Observable发射了一个数据项，只要在另一个Observable发射的数据项定义的时间窗口内，就将两个Observable发射的数据合并发射
	// Merge — 将两个Observable发射的数据组合并成一个
	// StartWith — 在发射原来的Observable的数据序列之前，先发射一个指定的数据序列或数据项
	// Switch —
	// 将一个发射Observable序列的Observable转换为这样一个Observable：它逐个发射那些Observable最近发射的数据
	// Zip — 打包，使用一个指定的函数将多个Observable发射的数据组合在一起，然后将这个函数的结果作为单项数据发射

	// 错误处理
	// 这些操作符用于从错误通知中恢复
	// Catch — 捕获，继续序列操作，将错误替换为正常的数据，从onError通知中恢复
	// Retry — 重试，如果Observable发射了一个错误通知，重新订阅它，期待它正常终止

	// 辅助操作
	// 一组用于处理Observable的操作符
	// Delay — 延迟一段时间发射结果数据
	// Do — 注册一个动作占用一些Observable的生命周期事件，相当于Mock某个操作
	// Materialize/Dematerialize — 将发射的数据和通知都当做数据发射，或者反过来
	// ObserveOn — 指定观察者观察Observable的调度程序（工作线程）
	// Serialize — 强制Observable按次序发射数据并且功能是有效的
	// Subscribe — 收到Observable发射的数据和通知后执行的操作
	// SubscribeOn — 指定Observable应该在哪个调度程序上执行
	// TimeInterval — 将一个Observable转换为发射两个数据之间所耗费时间的Observable
	// Timeout — 添加超时机制，如果过了指定的一段时间没有发射数据，就发射一个错误通知
	// Timestamp — 给Observable发射的每个数据项添加一个时间戳
	// Using — 创建一个只在Observable的生命周期内存在的一次性资源

	// 条件和布尔操作
	// 这些操作符可用于单个或多个数据项，也可用于Observable
	// All — 判断Observable发射的所有的数据项是否都满足某个条件
	// Amb — 给定多个Observable，只让第一个发射数据的Observable发射全部数据
	// Contains — 判断Observable是否会发射一个指定的数据项
	// DefaultIfEmpty — 发射来自原始Observable的数据，如果原始Observable没有发射数据，就发射一个默认数据
	// SequenceEqual — 判断两个Observable是否按相同的数据序列
	// SkipUntil —
	// 丢弃原始Observable发射的数据，直到第二个Observable发射了一个数据，然后发射原始Observable的剩余数据
	// SkipWhile — 丢弃原始Observable发射的数据，直到一个特定的条件为假，然后发射原始Observable剩余的数据
	// TakeUntil — 发射来自原始Observable的数据，直到第二个Observable发射了一个数据或一个通知
	// TakeWhile — 发射原始Observable的数据，直到一个特定的条件为真，然后跳过剩余的数据

	// 算术和聚合操作
	// 这些操作符可用于整个数据序列
	// Average — 计算Observable发射的数据序列的平均值，然后发射这个结果
	// Concat — 不交错的连接多个Observable的数据
	// Count — 计算Observable发射的数据个数，然后发射这个结果
	// Max — 计算并发射数据序列的最大值
	// Min — 计算并发射数据序列的最小值
	// Reduce — 按顺序对数据序列的每一个应用某个函数，然后返回这个值
	// Sum — 计算并发射数据序列的和

	// 连接操作
	// 一些有精确可控的订阅行为的特殊Observable
	// Connect — 指示一个可连接的Observable开始发射数据给订阅者
	// Publish — 将一个普通的Observable转换为可连接的
	// RefCount — 使一个可连接的Observable表现得像一个普通的Observable
	// Replay — 确保所有的观察者收到同样的数据序列，即使他们在Observable开始发射数据之后才订阅

	// 转换操作
	// To — 将Observable转换为其它的对象或数据结构
	// Blocking 阻塞Observable的操作符

	// 操作符决策树
	// 几种主要的需求
	// 直接创建一个Observable（创建操作）
	// 组合多个Observable（组合操作）
	// 对Observable发射的数据执行变换操作（变换操作）
	// 从Observable发射的数据中取特定的值（过滤操作）
	// 转发Observable的部分值（条件/布尔/过滤操作）
	// 对Observable发射的数据序列求值（算术/聚合操作）
//
//	/**
//	 * create操作符 所有创建型操作符的“根”，也就是说其他创建型操作符最后都是通过create操作符来创建Observable的
//	 */
//	private void creatrUse() {
//		// 创建被观察者
//		Observable<String> observable = Observable.create(new Observable.OnSubscribe<String>() {
//			@Override
//			public void call(Subscriber<? super String> subscriber) {
//				// 根据观察者索引对象向观察者发送数据
//				subscriber.onNext("发送测试消息");
//				// 数据发送完成
//				subscriber.onCompleted();
//				// 解绑观察者
//				if (!subscriber.isUnsubscribed()) {
//					subscriber.unsubscribe();
//				}
//			}
//		});
//
//		// 绑定观察者(调用此方法时事件发送的逻辑开始运行)
//		observable.subscribe(new Subscriber<String>() {
//			@Override
//			public void onCompleted() {
//				Log.d("onCompleted");
//			}
//
//			@Override
//			public void onError(Throwable e) {
//				Log.d(e.getMessage());
//			}
//
//			@Override
//			public void onNext(String s) {
//				Log.d(s);
//			}
//		});
//	}
//
//	/**
//	 * from操作符 把其他类型的对象和数据类型转化成Observable
//	 */
//	private void fromUse() {
//		Integer[] items = { 0, 1, 2, 3, 4, 5 };
//		Observable<Integer> observable = Observable.from(items);
//		observable.subscribe(new Action1<Integer>() {
//			@Override
//			public void call(Integer item) {
//				System.out.println(item);
//			}
//		}, new Action1<Throwable>() {
//			@Override
//			public void call(Throwable error) {
//				System.out.println("Error encountered: " + error.getMessage());
//			}
//		}, new Action0() {
//			@Override
//			public void call() {
//				System.out.println("Sequence complete");
//			}
//		});
//	}
//
//	/**
//	 * just操作符 把其他类型的对象和数据类型转化成Observable，它和from操作符很像，只是方法的参数有所差别
//	 */
//	private void justUse() {
//		Observable.just(1, 2, 3).subscribe(new Subscriber<Integer>() {
//			@Override
//			public void onNext(Integer item) {
//				System.out.println("Next: " + item);
//			}
//
//			@Override
//			public void onError(Throwable error) {
//				System.err.println("Error: " + error.getMessage());
//			}
//
//			@Override
//			public void onCompleted() {
//				System.out.println("Sequence complete.");
//			}
//		});
//	}
//
//	/**
//	 * defer操作符
//	 * 直到有订阅者订阅时，才通过Observable的工厂方法创建Observable并执行，defer操作符能够保证Observable的状态是最新的
//	 * 输出结果为 10 15
//	 */
//	private void deferUse() {
//		int i = 10;
//		Observable justObservable = Observable.just(i);
//		i = 12;
//
//		Observable deferObservable = Observable.defer(new Func0<Observable<Integer>>() {
//			@Override
//			public Observable<Integer> call() {
//				int i = 12;
//				return Observable.just(i);
//			}
//		});
//
//		i = 15;
//
//		justObservable.subscribe(new Subscriber() {
//			@Override
//			public void onCompleted() {
//
//			}
//
//			@Override
//			public void onError(Throwable e) {
//
//			}
//
//			@Override
//			public void onNext(Object o) {
//				System.out.println("just result:" + o.toString());
//			}
//		});
//
//		deferObservable.subscribe(new Subscriber() {
//			@Override
//			public void onCompleted() {
//
//			}
//
//			@Override
//			public void onError(Throwable e) {
//
//			}
//
//			@Override
//			public void onNext(Object o) {
//				System.out.println("defer result:" + o.toString());
//			}
//		});
//	}
//
//	/**
//	 * timer操作符 隔一段时间产生一个数字，然后就结束，可以理解为延迟产生数字
//	 */
//	private void timerUse() {
//		// 两秒后产生一个数字
//		Observable.timer(2, TimeUnit.SECONDS).subscribe(new Subscriber<Long>() {
//			@Override
//			public void onCompleted() {
//				System.out.println("Sequence complete.");
//			}
//
//			@Override
//			public void onError(Throwable e) {
//				System.out.println("error:" + e.getMessage());
//			}
//
//			@Override
//			public void onNext(Long aLong) {
//				System.out.println("Next:" + aLong.toString());
//			}
//		});
//	}
//
//	/**
//	 * interval操作符 每隔一段时间就产生一个数字，这些数字从0开始(?)，一次递增1直至无穷大
//	 */
//	private void intervalUse() {
//		Observable.interval(2, TimeUnit.SECONDS).subscribe(new Action1<Long>() {
//			@Override
//			public void call(Long aLong) {
//				System.out.println("Next:" + aLong.toString());
//			}
//		});
//	}

	// //range操作符是创建一组在从n开始，个数为m的连续数字，比如range(3,10)，就是创建3、4、5…12的一组数字
	// private void rangeUse() {
	// Observable.range(3, 10).subscribe(new Subscriber<Integer>() {
	// @Override
	// public void onCompleted() {
	// System.out.println("Sequence complete.");
	// }
	//
	// @Override
	// public void onError(Throwable e) {
	// System.out.println("error:" + e.getMessage());
	// }
	//
	// @Override
	// public void onNext(Integer i) {
	// System.out.println("Next:" + i.toString());
	// }
	// });
	// }
	//
	// //repeat操作符是对某一个Observable，重复产生多次结果
	// private void repeatUse() {
	// ///连续产生两组(3,4,5)的数字
	// Observable.range(3, 3).repeat(2).subscribe(new Subscriber<Integer>() {
	// @Override
	// public void onCompleted() {
	// System.out.println("Sequence complete.");
	// }
	//
	// @Override
	// public void onError(Throwable e) {
	// System.out.println("error:" + e.getMessage());
	// }
	//
	// @Override
	// public void onNext(Integer i) {
	// System.out.println("Next:" + i.toString());
	// }
	// });
	// }
	//
	// //repeatWhen操作符是对某一个Observable，有条件地重新订阅从而产生多次结果
	// private void repeatWhenUse() {
	// Observable.range(3, 3).repeatWhen(new Func1<Observable<Integer>,
	// Observable<Integer>>() {
	//
	// @Override
	// public Observable<Integer> call(Observable<Integer> integer) {
	// return Observable.just(integer);
	// }
	// }).subscribe(new Action1<Integer>() {
	// @Override
	// public void call(Integer integer) {
	// System.out.println("Next:" + integer.toString());
	// }
	// });
	// }
	//
	// //buffer操作符周期性地收集源Observable产生的结果到列表中，并把这个列表提交给订阅者，订阅者处理后，清空buffer列表，同时接收下一次收集的结果并提交给订阅者，周而复始。
	// private void bufferUse() {
	// //定义邮件内容
	// final String[] mails = new String[]{"Here is an email!", "Another
	// email!", "Yet another email!"};
	// //每隔1秒就随机发布一封邮件
	// Observable<String> endlessMail = Observable.create(new
	// Observable.OnSubscribe<String>() {
	// @Override
	// public void call(Subscriber<? super String> subscriber) {
	// try {
	// if (subscriber.isUnsubscribed()) return;
	// Random random = new Random();
	// while (true) {
	// String mail = mails[random.nextInt(mails.length)];
	// subscriber.onNext(mail);
	// Thread.sleep(1000);
	// }
	// } catch (Exception ex) {
	// subscriber.onError(ex);
	// }
	// }
	// }).subscribeOn(Schedulers.io());
	// //把上面产生的邮件内容缓存到列表中，并每隔3秒通知订阅者
	// endlessMail.buffer(3, TimeUnit.SECONDS).subscribe(new
	// Action1<List<String>>() {
	// @Override
	// public void call(List<String> list) {
	// System.out.println(String.format("You've got %d new messages! Here they
	// are!", list.size()));
	// for (int i = 0; i < list.size(); i++)
	// System.out.println("**" + list.get(i).toString());
	// }
	// });
	// }
	//
	// //flatMap操作符是把Observable产生的结果转换成多个Observable，然后把这多个Observable“扁平化”成一个Observable，并依次提交产生的结果给订阅者。
	// private void flatMap(Context context) {
	// Observable.just(context.getExternalCacheDir())
	// .flatMap(new Func1<File, Observable<File>>() {
	// @Override
	// public Observable<File> call(File file) {
	// //参数file是just操作符产生的结果，这里判断file是不是目录文件，如果是目录文件，则递归查找其子文件flatMap操作符神奇的地方在于，返回的结果还是一个Observable，而这个Observable其实是包含多个文件的Observable的，输出应该是ExternalCacheDir下的所有文件
	// if (file.isDirectory()) {
	// return Observable.from(file.listFiles()).flatMap(new Func1<File,
	// Observable<File>>() {
	// @Override
	// public Observable<File> call(File file) {
	// return Observable.just(file);
	// }
	// });
	// } else {
	// return Observable.just(file);
	// }
	// }
	// })
	// .subscribe(new Action1<File>() {
	// @Override
	// public void call(File file) {
	// System.out.println(file.getAbsolutePath());
	// }
	// });
	// }
	//
	// //concatMap都是把Observable产生的结果转换成多个Observable，然后把这多个Observable“扁平化”成一个Observable，并依次提交产生的结果给订阅者。
	// private void cnncatMap(Context context) {
	// Observable.just(context.getExternalCacheDir())
	// .concatMap(new Func1<File, Observable<File>>() {
	// @Override
	// public Observable<File> call(File file) {
	// //参数file是just操作符产生的结果，这里判断file是不是目录文件，如果是目录文件，则递归查找其子文件flatMap操作符神奇的地方在于，返回的结果还是一个Observable，而这个Observable其实是包含多个文件的Observable的，输出应该是ExternalCacheDir下的所有文件
	// if (file.isDirectory()) {
	// return Observable.from(file.listFiles()).flatMap(new Func1<File,
	// Observable<File>>() {
	// @Override
	// public Observable<File> call(File file) {
	// return Observable.just(file);
	// }
	// });
	// } else {
	// return Observable.just(file);
	// }
	// }
	// })
	// .subscribe(new Action1<File>() {
	// @Override
	// public void call(File file) {
	// System.out.println(file.getAbsolutePath());
	// }
	// });
	// }
	//
	// //switchMap操作符与flatMap操作符类似，都是把Observable产生的结果转换成多个Observable，然后把这多个Observable“扁平化”成一个Observable，并依次提交产生的结果给订阅者。
	// //与flatMap操作符不同的是，switchMap操作符会保存最新的Observable产生的结果而舍弃旧的结果，举个例子来说，比如源Observable产生A、B、C三个结果，通过switchMap的自定义映射规则，映射后应该会产生A1、A2、B1、B2、C1、C2，但是在产生B2的同时，C1已经产生了，这样最后的结果就变成A1、A2、B1、C1、C2，B2被舍弃掉了！
	// private void switchMapUse() {
	// //flatMap操作符的运行结果
	// Observable.just(10, 20, 30).flatMap(new Func1<Integer,
	// Observable<Integer>>() {
	// @Override
	// public Observable<Integer> call(Integer integer) {
	// //10的延迟执行时间为200毫秒、20和30的延迟执行时间为180毫秒
	// int delay = 200;
	// if (integer > 10)
	// delay = 180;
	//
	// return Observable.from(new Integer[]{integer, integer / 2}).delay(delay,
	// TimeUnit.MILLISECONDS);
	// }
	// }).observeOn(AndroidSchedulers.mainThread()).subscribe(new
	// Action1<Integer>() {
	// @Override
	// public void call(Integer integer) {
	// System.out.println("flatMap Next:" + integer);
	// }
	// });
	//
	// //concatMap操作符的运行结果
	// Observable.just(10, 20, 30).concatMap(new Func1<Integer,
	// Observable<Integer>>() {
	// @Override
	// public Observable<Integer> call(Integer integer) {
	// //10的延迟执行时间为200毫秒、20和30的延迟执行时间为180毫秒
	// int delay = 200;
	// if (integer > 10)
	// delay = 180;
	//
	// return Observable.from(new Integer[]{integer, integer / 2}).delay(delay,
	// TimeUnit.MILLISECONDS);
	// }
	// }).observeOn(AndroidSchedulers.mainThread()).subscribe(new
	// Action1<Integer>() {
	// @Override
	// public void call(Integer integer) {
	// System.out.println("concatMap Next:" + integer);
	// }
	// });
	//
	// //switchMap操作符的运行结果
	// Observable.just(10, 20, 30).switchMap(new Func1<Integer,
	// Observable<Integer>>() {
	// @Override
	// public Observable<Integer> call(Integer integer) {
	// //10的延迟执行时间为200毫秒、20和30的延迟执行时间为180毫秒
	// int delay = 200;
	// if (integer > 10)
	// delay = 180;
	//
	// return Observable.from(new Integer[]{integer, integer / 2}).delay(delay,
	// TimeUnit.MILLISECONDS);
	// }
	// }).observeOn(AndroidSchedulers.mainThread()).subscribe(new
	// Action1<Integer>() {
	// @Override
	// public void call(Integer integer) {
	// System.out.println("switchMap Next:" + integer);
	// }
	// });
	//
	//// 运行结果如下：
	//// flatMap Next:20
	//// flatMap Next:10
	//// flatMap Next:30
	//// flatMap Next:15
	//// flatMap Next:10
	//// flatMap Next:5
	//// switchMap Next:30
	//// switchMap Next:15
	//// concatMap Next:10
	//// concatMap Next:5
	//// concatMap Next:20
	//// concatMap Next:10
	//// concatMap Next:30
	//// concatMap Next:15
	// }
	//
	// //groupBy操作符是对源Observable产生的结果进行分组，形成一个类型为GroupedObservable的结果集，GroupedObservable中存在一个方法为getKey()，可以通过该方法获取结果集的Key值（类似于HashMap的key)。
	// private void groupByUse() {
	// Observable.interval(1, TimeUnit.SECONDS).take(10).groupBy(new Func1<Long,
	// Long>() {
	// @Override
	// public Long call(Long value) {
	// //按照key为0,1,2分为3组
	// return value % 3;
	// }
	// }).subscribe(new Action1<GroupedObservable<Long, Long>>() {
	// @Override
	// public void call(final GroupedObservable<Long, Long> result) {
	// result.subscribe(new Action1<Long>() {
	// @Override
	// public void call(Long value) {
	// System.out.println("key:" + result.getKey() + ", value:" + value);
	// }
	// });
	// }
	// });
	//
	//// 运行结果如下：
	//// key:0, value:0
	//// key:1, value:1
	//// key:2, value:2
	//// key:0, value:3
	//// key:1, value:4
	//// key:2, value:5
	//// key:0, value:6
	//// key:1, value:7
	//// key:2, value:8
	//// key:0, value:9
	// }
//
//	/**
//	 * map操作符 把源Observable产生的结果，通过映射规则转换成另一个结果集，并提交给订阅者进行处理。
//	 */
//	private void mapUse() {
//		Observable.just(1, 2, 3, 4, 5, 6).map(new Func1<Integer, Integer>() {
//			@Override
//			public Integer call(Integer integer) {
//				// 对源Observable产生的结果，都统一乘以3处理
//				return integer * 3;
//			}
//		}).subscribe(new Action1<Integer>() {
//			@Override
//			public void call(Integer integer) {
//				System.out.println("next:" + integer);
//			}
//		});
//	}
//
//	/**
//	 * cast操作符
//	 * 类似于map操作符，不同的地方在于map操作符可以通过自定义规则，把一个值A1变成另一个值A2，A1和A2的类型可以一样也可以不一样；
//	 * 而cast操作符主要是做类型转换的，传入参数为类型class，如果源Observable产生的结果不能转成指定的class，
//	 * 则会抛出ClassCastException运行时异常。
//	 */
//	private void castMap() {
//		Observable.just(1, 2, 3, 4, 5, 6).cast(Integer.class).subscribe(new Action1<Integer>() {
//			@Override
//			public void call(Integer value) {
//				System.out.println("next:" + value);
//			}
//		});
//	}

	// //scan操作符通过遍历源Observable产生的结果，依次对每一个结果项按照指定规则进行运算，计算后的结果作为下一个迭代项参数，每一次迭代项都会把计算结果输出给订阅者。
	// private void scanMap() {
	// Observable.just(1, 2, 3, 4, 5).scan(new Func2<Integer, Integer,
	// Integer>() {
	// @Override
	// public Integer call(Integer sum, Integer item) {
	// //参数sum就是上一次的计算结果
	// return sum + item;
	// }
	// }).subscribe(new Subscriber<Integer>() {
	// @Override
	// public void onNext(Integer item) {
	// System.out.println("Next: " + item);
	// }
	//
	// @Override
	// public void onError(Throwable error) {
	// System.err.println("Error: " + error.getMessage());
	// }
	//
	// @Override
	// public void onCompleted() {
	// System.out.println("Sequence complete.");
	// }
	// });
	//// 运行结果如下：
	//// Next: 1
	//// Next: 3
	//// Next: 6
	//// Next: 10
	//// Next: 15
	// }
	//
	// //window操作符非常类似于buffer操作符，区别在于buffer操作符产生的结果是一个List缓存，而window操作符产生的结果是一个Observable，订阅者可以对这个结果Observable重新进行订阅处理。
	// private void windowUse() {
	// Observable.interval(1, TimeUnit.SECONDS).take(12)
	// .window(3, TimeUnit.SECONDS)
	// .subscribe(new Action1<Observable<Long>>() {
	// @Override
	// public void call(Observable<Long> observable) {
	// System.out.println("subdivide begin......");
	// observable.subscribe(new Action1<Long>() {
	// @Override
	// public void call(Long aLong) {
	// System.out.println("Next:" + aLong);
	// }
	// });
	// }
	// });
	// }
	//
	// //debounce操作符对源Observable每产生一个结果后，如果在规定的间隔时间内没有别的结果产生，则把这个结果提交给订阅者处理，否则忽略该结果。
	// private void debounceUse() {
	// Observable.create(new Observable.OnSubscribe<Integer>() {
	// @Override
	// public void call(Subscriber<? super Integer> subscriber) {
	// if (subscriber.isUnsubscribed()) return;
	// try {
	// //产生结果的间隔时间分别为100、200、300...900毫秒
	// for (int i = 1; i < 10; i++) {
	// subscriber.onNext(i);
	// Thread.sleep(i * 100);
	// }
	// subscriber.onCompleted();
	// } catch (Exception e) {
	// subscriber.onError(e);
	// }
	// }
	// }).subscribeOn(Schedulers.newThread())
	// .debounce(400, TimeUnit.MILLISECONDS) //超时时间为400毫秒
	// .subscribe(
	// new Action1<Integer>() {
	// @Override
	// public void call(Integer integer) {
	// System.out.println("Next:" + integer);
	// }
	// }, new Action1<Throwable>() {
	// @Override
	// public void call(Throwable throwable) {
	// System.out.println("Error:" + throwable.getMessage());
	// }
	// }, new Action0() {
	// @Override
	// public void call() {
	// System.out.println("completed!");
	// }
	// });
	// }
	//
	// //distinct操作符对源Observable产生的结果进行过滤，把重复的结果过滤掉，只输出不重复的结果给订阅者，非常类似于SQL里的distinct关键字。
	// private void distinctUse() {
	// Observable.just(1, 2, 1, 1, 2, 3)
	// .distinct()
	// .subscribe(new Subscriber<Integer>() {
	// @Override
	// public void onNext(Integer item) {
	// System.out.println("Next: " + item);
	// }
	//
	// @Override
	// public void onError(Throwable error) {
	// System.err.println("Error: " + error.getMessage());
	// }
	//
	// @Override
	// public void onCompleted() {
	// System.out.println("Sequence complete.");
	// }
	// });
	// }
	//
	// //elementAt操作符在源Observable产生的结果中，仅仅把指定索引的结果提交给订阅者，索引是从0开始的。
	// private void elementAtUse() {
	// Observable.just(1, 2, 3, 4, 5, 6).elementAt(2)
	// .subscribe(
	// new Action1<Integer>() {
	// @Override
	// public void call(Integer integer) {
	// System.out.println("Next:" + integer);
	// }
	// }, new Action1<Throwable>() {
	// @Override
	// public void call(Throwable throwable) {
	// System.out.println("Error:" + throwable.getMessage());
	// }
	// }, new Action0() {
	// @Override
	// public void call() {
	// System.out.println("completed!");
	// }
	// });
	// }
	//
	// //filter操作符是对源Observable产生的结果按照指定条件进行过滤，只有满足条件的结果才会提交给订阅者
	// private void filterUse() {
	// Observable.just(1, 2, 3, 4, 5)
	// .filter(new Func1<Integer, Boolean>() {
	// @Override
	// public Boolean call(Integer item) {
	// return (item < 4);
	// }
	// }).subscribe(new Subscriber<Integer>() {
	// @Override
	// public void onNext(Integer item) {
	// System.out.println("Next: " + item);
	// }
	//
	// @Override
	// public void onError(Throwable error) {
	// System.err.println("Error: " + error.getMessage());
	// }
	//
	// @Override
	// public void onCompleted() {
	// System.out.println("Sequence complete.");
	// }
	// });
	// }
	//
	// //ofType操作符类似于filter操作符，区别在于ofType操作符是按照类型对结果进行过滤
	// private void opTypeUse() {
	// Observable.just(1, "hello world", true, 200L, 0.23f)
	// .ofType(Float.class)
	// .subscribe(new Subscriber<Object>() {
	// @Override
	// public void onNext(Object item) {
	// System.out.println("Next: " + item);
	// }
	//
	// @Override
	// public void onError(Throwable error) {
	// System.err.println("Error: " + error.getMessage());
	// }
	//
	// @Override
	// public void onCompleted() {
	// System.out.println("Sequence complete.");
	// }
	// });
	// }
	//
	// //first操作符是把源Observable产生的结果的第一个提交给订阅者，first操作符可以使用elementAt(0)和take(1)替代。
	// private void firstUse() {
	// Observable.just(1, 2, 3, 4, 5, 6, 7, 8)
	// .first()
	// .subscribe(new Subscriber<Integer>() {
	// @Override
	// public void onNext(Integer item) {
	// System.out.println("Next: " + item);
	// }
	//
	// @Override
	// public void onError(Throwable error) {
	// System.err.println("Error: " + error.getMessage());
	// }
	//
	// @Override
	// public void onCompleted() {
	// System.out.println("Sequence complete.");
	// }
	// });
	// }
	//
	// //single操作符是对源Observable的结果进行判断，如果产生的结果满足指定条件的数量不为1，则抛出异常，否则把满足条件的结果提交给订阅者
	// private void singleUse() {
	// Observable.just(1, 2, 3, 4, 5, 6, 7, 8)
	// .single(new Func1<Integer, Boolean>() {
	// @Override
	// public Boolean call(Integer integer) {
	// //取大于10的第一个数字
	// return integer > 10;
	// }
	// })
	// .subscribe(new Subscriber<Integer>() {
	// @Override
	// public void onNext(Integer item) {
	// System.out.println("Next: " + item);
	// }
	//
	// @Override
	// public void onError(Throwable error) {
	// System.err.println("Error: " + error.getMessage());
	// }
	//
	// @Override
	// public void onCompleted() {
	// System.out.println("Sequence complete.");
	// }
	// });
	// }
	//
	// //last操作符把源Observable产生的结果的最后一个提交给订阅者，last操作符可以使用takeLast(1)替代。
	// private void lastUse() {
	// Observable.just(1, 2, 3)
	// .last()
	// .subscribe(new Subscriber<Integer>() {
	// @Override
	// public void onNext(Integer item) {
	// System.out.println("Next: " + item);
	// }
	//
	// @Override
	// public void onError(Throwable error) {
	// System.err.println("Error: " + error.getMessage());
	// }
	//
	// @Override
	// public void onCompleted() {
	// System.out.println("Sequence complete.");
	// }
	// });
	// }
	//
	// //ignoreElements操作符忽略所有源Observable产生的结果，只把Observable的onCompleted和onError事件通知给订阅者。ignoreElements操作符适用于不太关心Observable产生的结果，只是在Observable结束时(onCompleted)或者出现错误时能够收到通知。
	// private void ignoreElementsUse() {
	// Observable.just(1, 2, 3, 4, 5, 6, 7, 8).ignoreElements()
	// .subscribe(new Subscriber<Integer>() {
	// @Override
	// public void onNext(Integer item) {
	// System.out.println("Next: " + item);
	// }
	//
	// @Override
	// public void onError(Throwable error) {
	// System.err.println("Error: " + error.getMessage());
	// }
	//
	// @Override
	// public void onCompleted() {
	// System.out.println("Sequence complete.");
	// }
	// });
	// }
	//
	// //sample操作符定期扫描源Observable产生的结果，在指定的时间间隔范围内对源Observable产生的结果进行采样
	// private void sampleUse() {
	// Observable.create(new Observable.OnSubscribe<Integer>() {
	// @Override
	// public void call(Subscriber<? super Integer> subscriber) {
	// if (subscriber.isUnsubscribed()) return;
	// try {
	// //前8个数字产生的时间间隔为1秒，后一个间隔为3秒
	// for (int i = 1; i < 9; i++) {
	// subscriber.onNext(i);
	// Thread.sleep(1000);
	// }
	// Thread.sleep(2000);
	// subscriber.onNext(9);
	// subscriber.onCompleted();
	// } catch (Exception e) {
	// subscriber.onError(e);
	// }
	// }
	// }).subscribeOn(Schedulers.newThread())
	// .sample(2200, TimeUnit.MILLISECONDS) //采样间隔时间为2200毫秒
	// .subscribe(new Subscriber<Integer>() {
	// @Override
	// public void onNext(Integer item) {
	// System.out.println("Next: " + item);
	// }
	//
	// @Override
	// public void onError(Throwable error) {
	// System.err.println("Error: " + error.getMessage());
	// }
	//
	// @Override
	// public void onCompleted() {
	// System.out.println("Sequence complete.");
	// }
	// });
	// }
	//
	// //skip操作符针对源Observable产生的结果，跳过前面n个不进行处理，而把后面的结果提交给订阅者处理
	// private void skipUse() {
	// Observable.just(1, 2, 3, 4, 5, 6, 7).skip(3)
	// .subscribe(new Subscriber<Integer>() {
	// @Override
	// public void onNext(Integer item) {
	// System.out.println("Next: " + item);
	// }
	//
	// @Override
	// public void onError(Throwable error) {
	// System.err.println("Error: " + error.getMessage());
	// }
	//
	// @Override
	// public void onCompleted() {
	// System.out.println("Sequence complete.");
	// }
	// });
	// }
	//
	// //skipLast操作符针对源Observable产生的结果，忽略Observable最后产生的n个结果，而把前面产生的结果提交给订阅者处理
	// private void skipLastUse() {
	// Observable.just(1, 2, 3, 4, 5, 6, 7).skipLast(3)
	// .subscribe(new Subscriber<Integer>() {
	// @Override
	// public void onNext(Integer item) {
	// System.out.println("Next: " + item);
	// }
	//
	// @Override
	// public void onError(Throwable error) {
	// System.err.println("Error: " + error.getMessage());
	// }
	//
	// @Override
	// public void onCompleted() {
	// System.out.println("Sequence complete.");
	// }
	// });
	// }
	//
	// //take操作符是把源Observable产生的结果，提取前面的n个提交给订阅者，而忽略后面的结果
	// private void takeUse() {
	// Observable.just(1, 2, 3, 4, 5, 6, 7, 8)
	// .take(4)
	// .subscribe(new Subscriber<Integer>() {
	// @Override
	// public void onNext(Integer item) {
	// System.out.println("Next: " + item);
	// }
	//
	// @Override
	// public void onError(Throwable error) {
	// System.err.println("Error: " + error.getMessage());
	// }
	//
	// @Override
	// public void onCompleted() {
	// System.out.println("Sequence complete.");
	// }
	// });
	// }
	//
	// //takeFirst操作符类似于take操作符，同时也类似于first操作符，都是获取源Observable产生的结果列表中符合指定条件的前一个或多个，与first操作符不同的是，first操作符如果获取不到数据，则会抛出NoSuchElementException异常，而takeFirst则会返回一个空的Observable，该Observable只有onCompleted通知而没有onNext通知。
	// private void takeFirstUse() {
	// Observable.just(1, 2, 3, 4, 5, 6, 7).takeFirst(new Func1<Integer,
	// Boolean>() {
	// @Override
	// public Boolean call(Integer integer) {
	// //获取数值大于3的数据
	// return integer > 3;
	// }
	// })
	// .subscribe(new Subscriber<Integer>() {
	// @Override
	// public void onNext(Integer item) {
	// System.out.println("Next: " + item);
	// }
	//
	// @Override
	// public void onError(Throwable error) {
	// System.err.println("Error: " + error.getMessage());
	// }
	//
	// @Override
	// public void onCompleted() {
	// System.out.println("Sequence complete.");
	// }
	// });
	// }
	//
	// //takeLast操作符是把源Observable产生的结果的后n项提交给订阅者，提交时机是Observable发布onCompleted通知之时。
	// private void takeLast() {
	// Observable.just(1, 2, 3, 4, 5, 6, 7).takeLast(2)
	// .subscribe(new Subscriber<Integer>() {
	// @Override
	// public void onNext(Integer item) {
	// System.out.println("Next: " + item);
	// }
	//
	// @Override
	// public void onError(Throwable error) {
	// System.err.println("Error: " + error.getMessage());
	// }
	//
	// @Override
	// public void onCompleted() {
	// System.out.println("Sequence complete.");
	// }
	// });
	// }
	//
	// //combineLatest操作符把两个Observable产生的结果进行合并，合并的结果组成一个新的Observable。
	// private void combineLatestUse() {
	// //产生0,5,10,15,20数列
	// Observable<Long> observable1 = Observable.timer(0, 1000,
	// TimeUnit.MILLISECONDS)
	// .map(new Func1<Long, Long>() {
	// @Override
	// public Long call(Long aLong) {
	// return aLong * 5;
	// }
	// }).take(5);
	//
	// //产生0,10,20,30,40数列
	// Observable<Long> observable2 = Observable.timer(500, 1000,
	// TimeUnit.MILLISECONDS)
	// .map(new Func1<Long, Long>() {
	// @Override
	// public Long call(Long aLong) {
	// return aLong * 10;
	// }
	// }).take(5);
	//
	//
	// Observable.combineLatest(observable1, observable2, new Func2<Long, Long,
	// Long>() {
	// @Override
	// public Long call(Long aLong, Long aLong2) {
	// return aLong + aLong2;
	// }
	// }).subscribe(new Subscriber<Long>() {
	// @Override
	// public void onCompleted() {
	// System.out.println("Sequence complete.");
	// }
	//
	// @Override
	// public void onError(Throwable e) {
	// System.err.println("Error: " + e.getMessage());
	// }
	//
	// @Override
	// public void onNext(Long aLong) {
	// System.out.println("Next: " + aLong);
	// }
	// });
	// }
	//
	// //join操作符把类似于combineLatest操作符，也是两个Observable产生的结果进行合并，合并的结果组成一个新的Observable，但是join操作符可以控制每个Observable产生结果的生命周期，在每个结果的生命周期内，可以与另一个Observable产生的结果按照一定的规则进行合并
	// private void joinUse() {
	// //产生0,5,10,15,20数列
	// Observable<Long> observable1 = Observable.timer(0, 1000,
	// TimeUnit.MILLISECONDS)
	// .map(new Func1<Long, Long>() {
	// @Override
	// public Long call(Long aLong) {
	// return aLong * 5;
	// }
	// }).take(5);
	//
	// //产生0,10,20,30,40数列
	// Observable<Long> observable2 = Observable.timer(500, 1000,
	// TimeUnit.MILLISECONDS)
	// .map(new Func1<Long, Long>() {
	// @Override
	// public Long call(Long aLong) {
	// return aLong * 10;
	// }
	// }).take(5);
	//
	// observable1.join(observable2, new Func1<Long, Observable<Long>>() {
	// @Override
	// public Observable<Long> call(Long aLong) {
	// //使Observable延迟600毫秒执行
	// return Observable.just(aLong).delay(600, TimeUnit.MILLISECONDS);
	// }
	// }, new Func1<Long, Observable<Long>>() {
	// @Override
	// public Observable<Long> call(Long aLong) {
	// //使Observable延迟600毫秒执行
	// return Observable.just(aLong).delay(600, TimeUnit.MILLISECONDS);
	// }
	// }, new Func2<Long, Long, Long>() {
	// @Override
	// public Long call(Long aLong, Long aLong2) {
	// return aLong + aLong2;
	// }
	// }).subscribe(new Subscriber<Long>() {
	// @Override
	// public void onCompleted() {
	// System.out.println("Sequence complete.");
	// }
	//
	// @Override
	// public void onError(Throwable e) {
	// System.err.println("Error: " + e.getMessage());
	// }
	//
	// @Override
	// public void onNext(Long aLong) {
	// System.out.println("Next: " + aLong);
	// }
	// });
	// }
	//
	// //groupJoin操作符非常类似于join操作符，区别在于join操作符中第四个参数的传入函数不一致
	// private void groupJoin() {
	// Observable<Long> observable1 = Observable.timer(0, 1000,
	// TimeUnit.MILLISECONDS)
	// .map(new Func1<Long, Long>() {
	// @Override
	// public Long call(Long aLong) {
	// return aLong * 5;
	// }
	// }).take(5);
	//
	// //产生0,10,20,30,40数列
	// Observable<Long> observable2 = Observable.timer(500, 1000,
	// TimeUnit.MILLISECONDS)
	// .map(new Func1<Long, Long>() {
	// @Override
	// public Long call(Long aLong) {
	// return aLong * 10;
	// }
	// }).take(5);
	//
	// observable1.groupJoin(observable2, new Func1<Long, Observable<Long>>() {
	// @Override
	// public Observable<Long> call(Long aLong) {
	// return Observable.just(aLong).delay(1600, TimeUnit.MILLISECONDS);
	// }
	// }, new Func1<Long, Observable<Long>>() {
	// @Override
	// public Observable<Long> call(Long aLong) {
	// return Observable.just(aLong).delay(600, TimeUnit.MILLISECONDS);
	// }
	// }, new Func2<Long, Observable<Long>, Observable<Long>>() {
	// @Override
	// public Observable<Long> call(final Long aLong, Observable<Long>
	// observable) {
	// return observable.map(new Func1<Long, Long>() {
	// @Override
	// public Long call(Long aLong2) {
	// return aLong + aLong2;
	// }
	// });
	// }
	// }).subscribe(new Subscriber<Observable<Long>>() {
	// @Override
	// public void onCompleted() {
	// System.out.println("Sequence complete.");
	// }
	//
	// @Override
	// public void onError(Throwable e) {
	// System.err.println("Error: " + e.getMessage());
	// }
	//
	// @Override
	// public void onNext(Observable<Long> observable) {
	// observable.subscribe(new Subscriber<Long>() {
	// @Override
	// public void onCompleted() {
	//
	// }
	//
	// @Override
	// public void onError(Throwable e) {
	//
	// }
	//
	// @Override
	// public void onNext(Long aLong) {
	// System.out.println("Next: " + aLong);
	// }
	// });
	// }
	// });
	// }
	//
	// //merge操作符是按照两个Observable提交结果的时间顺序，对Observable进行合并，如ObservableA每隔500毫秒产生数据为0,5,10,15,20；而ObservableB每隔500毫秒产生数据0,10,20,30,40，其中第一个数据延迟500毫秒产生，最后合并结果为：0,0,5,10,10,20,15,30,20,40;
	// private void mergeUse() {
	// //产生0,5,10,15,20数列
	// Observable<Long> observable1 = Observable.timer(0, 1000,
	// TimeUnit.MILLISECONDS)
	// .map(new Func1<Long, Long>() {
	// @Override
	// public Long call(Long aLong) {
	// return aLong * 5;
	// }
	// }).take(5);
	//
	// //产生0,10,20,30,40数列
	// Observable<Long> observable2 = Observable.timer(500, 1000,
	// TimeUnit.MILLISECONDS)
	// .map(new Func1<Long, Long>() {
	// @Override
	// public Long call(Long aLong) {
	// return aLong * 10;
	// }
	// }).take(5);
	//
	// Observable.merge(observable1, observable2)
	// .subscribe(new Subscriber<Long>() {
	// @Override
	// public void onCompleted() {
	// System.out.println("Sequence complete.");
	// }
	//
	// @Override
	// public void onError(Throwable e) {
	// System.err.println("Error: " + e.getMessage());
	// }
	//
	// @Override
	// public void onNext(Long aLong) {
	// System.out.println("Next:" + aLong);
	// }
	// });
	//// Next:0
	//// Next:0
	//// Next:5
	//// Next:10
	//// Next:10
	//// Next:20
	//// Next:15
	//// Next:30
	//// Next:20
	//// Next:40
	// }
	//
	// //mergeDelayError操作符
	// 从merge操作符的流程图可以看出，一旦合并的某一个Observable中出现错误，就会马上停止合并，并对订阅者回调执行onError方法，而mergeDelayError操作符会把错误放到所有结果都合并完成之后才执行
	// private void mergeDelayErrorUse() {
	// //产生0,5,10数列,最后会产生一个错误
	// Observable<Long> errorObservable = Observable.error(new Exception("this
	// is end!"));
	// Observable<Long> observable1 = Observable.timer(0, 1000,
	// TimeUnit.MILLISECONDS)
	// .map(new Func1<Long, Long>() {
	// @Override
	// public Long call(Long aLong) {
	// return aLong * 5;
	// }
	// }).take(3).mergeWith(errorObservable.delay(3500, TimeUnit.MILLISECONDS));
	//
	// //产生0,10,20,30,40数列
	// Observable<Long> observable2 = Observable.timer(500, 1000,
	// TimeUnit.MILLISECONDS)
	// .map(new Func1<Long, Long>() {
	// @Override
	// public Long call(Long aLong) {
	// return aLong * 10;
	// }
	// }).take(5);
	//
	// Observable.mergeDelayError(observable1, observable2)
	// .subscribe(new Subscriber<Long>() {
	// @Override
	// public void onCompleted() {
	// System.out.println("Sequence complete.");
	// }
	//
	// @Override
	// public void onError(Throwable e) {
	// System.err.println("Error: " + e.getMessage());
	// }
	//
	// @Override
	// public void onNext(Long aLong) {
	// System.out.println("Next:" + aLong);
	// }
	// });
	// }
	//
	// //startWith操作符是在源Observable提交结果之前，插入指定的某些数据
	// private void startWithUse() {
	// Observable.just(10, 20, 30).startWith(2, 3, 4).subscribe(new
	// Subscriber<Integer>() {
	// @Override
	// public void onCompleted() {
	// System.out.println("Sequence complete.");
	// }
	//
	// @Override
	// public void onError(Throwable e) {
	// System.err.println("Error: " + e.getMessage());
	// }
	//
	// @Override
	// public void onNext(Integer value) {
	// System.out.println("Next:" + value);
	// }
	// });
	// }
	//
	// //switchOnNext操作符是把一组Observable转换成一个Observable，转换规则为：对于这组Observable中的每一个Observable所产生的结果，如果在同一个时间内存在两个或多个Observable提交的结果，只取最后一个Observable提交的结果给订阅者
	// private void switchOnNextUse() {
	// //每隔500毫秒产生一个observable
	// Observable<Observable<Long>> observable = Observable.timer(0, 500,
	// TimeUnit.MILLISECONDS).map(new Func1<Long, Observable<Long>>() {
	// @Override
	// public Observable<Long> call(Long aLong) {
	// //每隔200毫秒产生一组数据（0,10,20,30,40)
	// return Observable.timer(0, 200, TimeUnit.MILLISECONDS).map(new
	// Func1<Long, Long>() {
	// @Override
	// public Long call(Long aLong) {
	// return aLong * 10;
	// }
	// }).take(5);
	// }
	// }).take(2);
	//
	// Observable.switchOnNext(observable).subscribe(new Subscriber<Long>() {
	// @Override
	// public void onCompleted() {
	// System.out.println("Sequence complete.");
	// }
	//
	// @Override
	// public void onError(Throwable e) {
	// System.err.println("Error: " + e.getMessage());
	// }
	//
	// @Override
	// public void onNext(Long aLong) {
	// System.out.println("Next:" + aLong);
	// }
	// });
	// }
	//
	// //zip操作符是把两个observable提交的结果，严格按照顺序进行合并
	// private void zipUse() {
	// Observable<Integer> observable1 = Observable.just(10, 20, 30);
	// Observable<Integer> observable2 = Observable.just(4, 8, 12, 16);
	// Observable.zip(observable1, observable2, new Func2<Integer, Integer,
	// Integer>() {
	// @Override
	// public Integer call(Integer integer, Integer integer2) {
	// return integer + integer2;
	// }
	// }).subscribe(new Subscriber<Integer>() {
	// @Override
	// public void onCompleted() {
	// System.out.println("Sequence complete.");
	// }
	//
	// @Override
	// public void onError(Throwable e) {
	// System.err.println("Error: " + e.getMessage());
	// }
	//
	// @Override
	// public void onNext(Integer value) {
	// System.out.println("Next:" + value);
	// }
	// });
	// }
	//
	// //onErrorReturn操作符是在Observable发生错误或异常的时候（即将回调oError方法时），拦截错误并执行指定的逻辑，返回一个跟源Observable相同类型的结果，最后回调订阅者的onComplete方法
	// private void onErrorReturn() {
	// Observable<Integer> observable = Observable.create(new
	// Observable.OnSubscribe<Integer>() {
	// @Override
	// public void call(Subscriber<? super Integer> subscriber) {
	// if (subscriber.isUnsubscribed()) return;
	// //循环输出数字
	// try {
	// for (int i = 0; i < 10; i++) {
	// if (i == 4) {
	// throw new Exception("this is number 4 error！");
	// }
	// subscriber.onNext(i);
	// }
	// subscriber.onCompleted();
	// } catch (Exception e) {
	// subscriber.onError(e);
	// }
	// }
	// });
	//
	// observable.onErrorReturn(new Func1<Throwable, Integer>() {
	// @Override
	// public Integer call(Throwable throwable) {
	// return 1004;
	// }
	// }).subscribe(new Subscriber<Integer>() {
	// @Override
	// public void onCompleted() {
	// System.out.println("Sequence complete.");
	// }
	//
	// @Override
	// public void onError(Throwable e) {
	// System.err.println("Error: " + e.getMessage());
	// }
	//
	// @Override
	// public void onNext(Integer value) {
	// System.out.println("Next:" + value);
	// }
	// });
	// }
	//
	// //onErrorResumeNext操作符跟onErrorReturn类似，只不过onErrorReturn只能在错误或异常发生时只返回一个和源Observable相同类型的结果，而onErrorResumeNext操作符是在错误或异常发生时返回一个Observable，也就是说可以返回多个和源Observable相同类型的结果
	// private void onErrorEwsumeNextUse() {
	// Observable<Integer> observable = Observable.create(new
	// Observable.OnSubscribe<Integer>() {
	// @Override
	// public void call(Subscriber<? super Integer> subscriber) {
	// if (subscriber.isUnsubscribed()) return;
	// //循环输出数字
	// try {
	// for (int i = 0; i < 10; i++) {
	// if (i == 4) {
	// throw new Exception("this is number 4 error！");
	// }
	// subscriber.onNext(i);
	// }
	// subscriber.onCompleted();
	// } catch (Exception e) {
	// subscriber.onError(e);
	// }
	// }
	// });
	//
	// observable.onErrorResumeNext(new Func1<Throwable, Observable<? extends
	// Integer>>() {
	// @Override
	// public Observable<? extends Integer> call(Throwable throwable) {
	// return Observable.just(100, 101, 102);
	// }
	// }).subscribe(new Subscriber<Integer>() {
	// @Override
	// public void onCompleted() {
	// System.out.println("Sequence complete.");
	// }
	//
	// @Override
	// public void onError(Throwable e) {
	// System.err.println("Error: " + e.getMessage());
	// }
	//
	// @Override
	// public void onNext(Integer value) {
	// System.out.println("Next:" + value);
	// }
	// });
	// }
	//
	// //onExceptionResumeNext操作符和onErrorResumeNext操作符类似，不同的地方在于onErrorResumeNext操作符是当Observable发生错误或异常时触发，而onExceptionResumeNext是当Observable发生异常时才触发。
	// private void onExceptionResumeNextUse() {
	// Observable<Integer> observable = Observable.create(new
	// Observable.OnSubscribe<Integer>() {
	// @Override
	// public void call(Subscriber<? super Integer> subscriber) {
	// if (subscriber.isUnsubscribed()) return;
	// //循环输出数字
	// try {
	// for (int i = 0; i < 10; i++) {
	// if (i == 4) {
	// throw new Exception("this is number 4 error！");
	// }
	// subscriber.onNext(i);
	// }
	// subscriber.onCompleted();
	// } catch (Throwable e) {
	// subscriber.onError(e);
	// }
	// }
	// });
	//
	// observable.onExceptionResumeNext(Observable.just(100, 101,
	// 102)).subscribe(new Subscriber<Integer>() {
	// @Override
	// public void onCompleted() {
	// System.out.println("Sequence complete.");
	// }
	//
	// @Override
	// public void onError(Throwable e) {
	// System.err.println("Error: " + e.getMessage());
	// }
	//
	// @Override
	// public void onNext(Integer value) {
	// System.out.println("Next:" + value);
	// }
	// });
	// }
	//
	// //retry操作符是当Observable发生错误或者异常时，重新尝试执行Observable的逻辑，如果经过n次重新尝试执行后仍然出现错误或者异常，则最后回调执行onError方法；当然如果源Observable没有错误或者异常出现，则按照正常流程执行
	// private void retryUse() {
	// Observable<Integer> observable = Observable.create(new
	// Observable.OnSubscribe<Integer>() {
	// @Override
	// public void call(Subscriber<? super Integer> subscriber) {
	// if (subscriber.isUnsubscribed()) return;
	// //循环输出数字
	// try {
	// for (int i = 0; i < 10; i++) {
	// if (i == 4) {
	// throw new Exception("this is number 4 error！");
	// }
	// subscriber.onNext(i);
	// }
	// subscriber.onCompleted();
	// } catch (Throwable e) {
	// subscriber.onError(e);
	// }
	// }
	// });
	//
	// observable.retry(2).subscribe(new Subscriber<Integer>() {
	// @Override
	// public void onCompleted() {
	// System.out.println("Sequence complete.");
	// }
	//
	// @Override
	// public void onError(Throwable e) {
	// System.err.println("Error: " + e.getMessage());
	// }
	//
	// @Override
	// public void onNext(Integer value) {
	// System.out.println("Next:" + value);
	// }
	// });
	// }
	//
	// //retryWhen操作符类似于retry操作符，都是在源observable出现错误或者异常时，重新尝试执行源observable的逻辑，不同在于retryWhen操作符是在源Observable出现错误或者异常时，通过回调第二个Observable来判断是否重新尝试执行源Observable的逻辑，如果第二个Observable没有错误或者异常出现，则就会重新尝试执行源Observable的逻辑，否则就会直接回调执行订阅者的onError方法。
	// private void retryWhenUse() {
	// Observable<Integer> observable = Observable.create(new
	// Observable.OnSubscribe<Integer>() {
	// @Override
	// public void call(Subscriber<? super Integer> subscriber) {
	// System.out.println("subscribing");
	// subscriber.onError(new RuntimeException("always fails"));
	// }
	// });
	//
	// observable.retryWhen(new Func1<Observable<? extends Throwable>,
	// Observable<?>>() {
	// @Override
	// public Observable<?> call(Observable<? extends Throwable> observable) {
	// return observable.zipWith(Observable.range(1, 3), new Func2<Throwable,
	// Integer, Integer>() {
	// @Override
	// public Integer call(Throwable throwable, Integer integer) {
	// return integer;
	// }
	// }).flatMap(new Func1<Integer, Observable<?>>() {
	// @Override
	// public Observable<?> call(Integer integer) {
	// System.out.println("delay retry by " + integer + " second(s)");
	// //每一秒中执行一次
	// return Observable.timer(integer, TimeUnit.SECONDS);
	// }
	// });
	// }
	// }).subscribe(new Subscriber<Integer>() {
	// @Override
	// public void onCompleted() {
	// System.out.println("Sequence complete.");
	// }
	//
	// @Override
	// public void onError(Throwable e) {
	// System.err.println("Error: " + e.getMessage());
	// }
	//
	// @Override
	// public void onNext(Integer value) {
	// System.out.println("Next:" + value);
	// }
	// });
	// }
	//
	// // RxJava线程控制
	// // Schedulers.immediate(): 直接在当前线程运行，相当于不指定线程。这是默认的 Scheduler。
	// // Schedulers.newThread(): 总是启用新线程，并在新线程执行操作。
	// // Schedulers.io(): I/O 操作（读写文件、读写数据库、网络信息交互等）所使用的 Scheduler
	// // Schedulers.computation(): 计算所使用的 Scheduler。这个计算指的是 CPU 密集型计算，即不会被 I/O
	// 等操作限制性能的操作，例如图形的计算。
	// // Android 还有一个专用的 AndroidSchedulers.mainThread()，它指定的操作将在 Android 主线程运行。
	//
	// // subscribeOn(): 指定 subscribe() 所发生的线程，即 Observable.OnSubscribe
	// 被激活时所处的线程。或者叫做事件产生的线程。
	// // observeOn(): 指定 Subscriber 所运行在的线程。或者叫做事件消费的线程。
	//
	// //通过 observeOn() 的多次调用，程序实现了线程的多次切换。不过，不同于 observeOn() ， subscribeOn()
	// 的位置放在哪里都可以，但它是只能调用一次的。
	// //当使用了多个subscribeOn() 的时候，只有第一个 subscribeOn() 起作用
	// private void SchedulersUse() {
	// Observable.just(1, 2, 3, 4)
	// .subscribeOn(Schedulers.io()) // 指定 subscribe() 发生在 IO 线程
	// .observeOn(AndroidSchedulers.mainThread()) // 指定 Subscriber 的回调发生在主线程
	// .subscribe(new Action1<Integer>() {
	// @Override
	// public void call(Integer number) {
	// Log.d("number:" + number);
	// }
	// });
	//
	// Observable.just(1, 2, 3, 4) // IO 线程，由 subscribeOn() 指定
	// .subscribeOn(Schedulers.io())
	// .observeOn(Schedulers.newThread())
	// .map(mapOperator) // 新线程，由 observeOn() 指定
	// .observeOn(Schedulers.io())
	// .map(mapOperator2) // IO 线程，由 observeOn() 指定
	// .observeOn(AndroidSchedulers.mainThread)
	// .subscribe(subscriber); // Android 主线程，由 observeOn() 指定
	// }
	//
	// //doOnSubscribe() 默认情况下， doOnSubscribe() 执行在 subscribe() 发生的线程；而如果在
	// doOnSubscribe() 之后有 subscribeOn() 的话，它将执行在离它最近的 subscribeOn() 所指定的线程。
	// private void doOnSubscribeUse() {
	// Observable.create(new Observable.OnSubscribe<String>() {
	// @Override
	// public void call(Subscriber<? super String> subscriber) {
	// subscriber.onNext("****");
	// }
	// }).subscribeOn(Schedulers.io())
	// .doOnSubscribe(new Action0() {
	// @Override
	// public void call() {
	// // 需要在流程开始前的初始化之前在主线程执行的操作
	// }
	// })
	// .subscribeOn(AndroidSchedulers.mainThread()) // 指定主线程
	// .observeOn(AndroidSchedulers.mainThread())
	// .subscribe(new Action1<String>() {
	// @Override
	// public void call(String s) {
	//
	// }
	// });
	// }
	//
	// //hrottleFirst():在每次事件触发后的一定时间间隔内丢弃新的事件。常用作去抖动过滤，例如按钮的点击监听器：
	// private void flatUse(Context context) {
	// RxView.clickEvents(new Button(context)) // RxBinding 代码，后面的文章有解释
	// .throttleFirst(500, TimeUnit.MILLISECONDS) // 设置防抖间隔为 500ms
	// .subscribe(new Subscriber<ViewClickEvent>() {
	// @Override
	// public void onCompleted() {
	//
	// }
	//
	// @Override
	// public void onError(Throwable e) {
	//
	// }
	//
	// @Override
	// public void onNext(ViewClickEvent viewClickEvent) {
	//
	// }
	// });
	// }
}
