package com.threehalf.rxstudy;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.ObservableSource;
import io.reactivex.Observer;
import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private Button mBtnStart;
    private Subscription subscription;
    private TextView mTvText;
    private String file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mBtnStart = findViewById(R.id.btn_start);
        Button mBtnOk = findViewById(R.id.btn_ok);
        mTvText = findViewById(R.id.tv_text);
        file = Environment.getExternalStorageDirectory().toString() + "/shige.txt";
        mBtnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //                create();
//                fromArray();
//                just();
//                map();
//                flatMap();
//                zip();
//                fLowable();
                redDocFile();


            }
        });
        mBtnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (subscription != null) {
                    subscription.request(1);
                }

            }
        });

        findViewById(R.id.tv_start_mata).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Uri uri=Uri.parse("mata://ee.mata.chat.share");
                Intent intent = getPackageManager().getLaunchIntentForPackage("com.xxqp.cqqp");
                startActivity(intent);
                String invitationCode="123456";
                int sdkInt = Build.VERSION.SDK_INT;
                if (sdkInt > Build.VERSION_CODES.HONEYCOMB) {// api11
                    ClipboardManager copy = (ClipboardManager) MainActivity.this
                            .getSystemService(Context.CLIPBOARD_SERVICE);
                    copy.setText(invitationCode);
                    Toast.makeText(MainActivity.this, "邀请码成功复制到粘贴板",
                            Toast.LENGTH_SHORT).show();
                } else if (sdkInt <= Build.VERSION_CODES.HONEYCOMB) {
                    android.text.ClipboardManager copyq = (android.text.ClipboardManager) MainActivity.this
                            .getSystemService(Context.CLIPBOARD_SERVICE);
                    copyq.setText(invitationCode);
                    Toast.makeText(MainActivity.this, "邀请码成功复制到粘贴板",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });


    }

    private void create() {
        Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                Log.e(TAG, "emit 1");
                emitter.onNext(1);
                Log.e(TAG, "emit 2");
                emitter.onNext(2);
                Log.e(TAG, "emit 3");
                emitter.onNext(3);
                Log.e(TAG, "emit complete");
                emitter.onComplete();
                Log.e(TAG, "emit 4");
                emitter.onNext(4);
            }
        }).subscribe(new Observer<Integer>() {
            private Disposable mDisposable;
            private int i;

            @Override
            public void onSubscribe(Disposable d) {
                Log.e(TAG, "subscribe");
                mDisposable = d;
            }

            @Override
            public void onNext(Integer value) {
                Log.e(TAG, "onNext: " + value);
                i++;
                if (i == 2) {
                    Log.e(TAG, "dispose");
                    mDisposable.dispose();
                    Log.e(TAG, "isDisposed : " + mDisposable.isDisposed());
                }
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "error");
            }

            @Override
            public void onComplete() {
                Log.e(TAG, "complete");
            }
        });
    }

    /**
     * 使用just时，just是将数据作为一个完整的对象一次性发射的,最后调用的fromArray
     * map 是一个变形的过程eg 由△--->口
     */
    private void just() {
        String[] students = {"jay", "tom"};
        String[] students22 = {"jay22", "tom22"};
        //------------------对两个数组进行合并后一次性发出------------------------
        Observable.just(students, students22).subscribe(new Consumer<String[]>() {
            @Override
            public void accept(String[] strings) throws Exception {
                for (String s : strings) {
                    Log.e(TAG, "1=just===" + s);
                }
            }
        });
        Observable observable1 = Observable.just("100", "2000").map(new Function<String, Object>() {
            @Override
            public Integer apply(String s) throws Exception {

                return Integer.parseInt(s);
            }
        });
        observable1.subscribe(new Consumer<Integer>() {
            @Override
            public void accept(Integer integer) throws Exception {
                Log.e(TAG, "1====" + integer);
            }
        });
        //---------------------------------------------------
        Observable<String> observable2 = Observable.just(students).map(new Function<String[], String>() {
            @Override
            public String apply(String[] strings) throws Exception {
                String str = "";
                for (String s : strings) {
                    str = str + s;
                }
                return str;
            }
        });
        observable2.subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                Log.e(TAG, "observable2==" + s);
            }
        });


        //==============================================
        Observable observable = Observable.just(students, students22).map(new Function<String[], String>() {
            @Override
            public String apply(String[] strings) throws Exception {
                String str = "";
                for (String s : strings) {
                    str = str + s;
                }
                return str;
            }
        });

        observable.subscribe(new Consumer<String>() {
            @Override
            public void accept(String strings) throws Exception {
                Log.e(TAG, "222===" + strings);

            }
        });


    }

    /**
     * ，使用fromArray接收的数据源是逐个发射的
     */
    private void fromArray() {
        String[] students = {"jay", "tom"};
        Observable.fromArray(students).subscribe(new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {

            }

            @Override
            public void onNext(String s) {
                Log.e(TAG, s);
            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    /**
     * map 是一个变形的过程eg 由△--->口
     */
    private void map() {
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                e.onNext("111111");
            }
        }).map(new Function<String, Integer>() {
            @Override
            public Integer apply(String s) throws Exception {
                return Integer.parseInt(s);
            }
        })
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer s) throws Exception {
                        Log.e(TAG, "map===" + s);
                    }
                });
    }

    private void flatMap() {
        // concatMap它和flatMap的作用几乎一模一样, 只是它的结果是严格按照上游发送的顺序来发送的, flatMap并不保证事件的顺序
        Disposable subscribe = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
                emitter.onNext(7);
                emitter.onNext(8);
                emitter.onNext(9);
            }
        }).flatMap(new Function<Integer, ObservableSource<List<String>>>() {
            @Override
            public ObservableSource<List<String>> apply(Integer integer) throws Exception {
                final List<String> list = new ArrayList<>();
                for (int i = 0; i < 3; i++) {
                    list.add("I am value " + integer);
                }
                return Observable.just(list);


            }
        }).flatMap(new Function<List<String>, ObservableSource<String>>() {
            @Override
            public ObservableSource<String> apply(List<String> strings) throws Exception {
                String str = strings.toString();
                return Observable.just(str);
            }
        })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        mBtnStart.setText(s.toString());
                    }
                });
    }

    private void zip() {
        Observable observable = Observable.create(new ObservableOnSubscribe<Integer>() {
            @Override
            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
                for (int i = 0; ; i++) {
                    e.onNext(i);
//                    Thread.sleep(1000);
                }

            }
        }).subscribeOn(Schedulers.io());
        Observable observable2 = Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> e) throws Exception {
                e.onNext("A");
                Thread.sleep(2000);
                e.onNext("B");
                Thread.sleep(2000);
                e.onNext("C");
                Thread.sleep(2000);
            }
        }).subscribeOn(Schedulers.io());
        Observable.zip(observable, observable2, new BiFunction<Integer, String, String>() {
            @Override
            public String apply(Integer o, String o2) throws Exception {
                return o + o2;
            }
        }).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<String>() {
            @Override
            public void accept(String s) throws Exception {
                mBtnStart.setText(s);
            }
        });


    }

    /**
     * 上下流分配
     * 我们把上游看成小日本, 把下游当作叶问, 当调用Subscription.request(1)时, 叶问就说我要打一个!
     * 然后小日本就拿出一个鬼子给叶问, 让他打, 等叶问打死这个鬼子之后, 再次调用request(10),
     * 叶问就又说我要打十个! 然后小日本又派出十个鬼子给叶问, 然后就在边上看热闹, 看叶问能不能打死十个鬼子,
     * 等叶问打死十个鬼子后再继续要鬼子接着打...
     */
    private void fLowable() {
        Flowable.create(new FlowableOnSubscribe<Integer>() {
            @Override
            public void subscribe(FlowableEmitter<Integer> emitter) throws Exception {
                for (int i = 0; i < 128; i++) {
                    Log.d(TAG, "emit " + i);
                    emitter.onNext(i);
                }
            }
        }, BackpressureStrategy.ERROR)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<Integer>() {
            @Override
            public void onSubscribe(Subscription s) {
                subscription = s;
                s.request(1);// 每次从水缸里去取一个
            }

            @Override
            public void onNext(Integer integer) {
                mBtnStart.setText("第" + integer + "个");
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onComplete() {

            }
        });
    }

    private void redDocFile() {
        //Flowable里默认有一个大小为128的水缸
        Flowable flowable = Flowable.create(new FlowableOnSubscribe<String>() {
            @Override
            public void subscribe(FlowableEmitter<String> emitter) throws Exception {
                try {
                    System.out.println("==="+Thread.currentThread().getName());
                    InputStreamReader fileReader = new InputStreamReader(new FileInputStream(file), "UTF-8"); //GBK或GB2312,GB18030
                    BufferedReader br = new BufferedReader(fileReader);
                    String str = "";
                    while ((str = br.readLine()) != null && !emitter.isCancelled()) {
                        while (emitter.requested() == 0) {
                            if (emitter.isCancelled()) {
                                break;
                            }
                        }
                        emitter.onNext(str);
                    }
                    br.close();
                    fileReader.close();

                    emitter.onComplete();
                } catch (Exception e) {
                    emitter.onError(e);
                }
            }
        }, BackpressureStrategy.ERROR).subscribeOn(Schedulers.io())
                .flatMap(new Function<String, Publisher<String>>() {
                    @Override
                    public Publisher<String> apply(String s) throws Exception {
                        System.out.println("==="+Thread.currentThread().getName());
                        return Flowable.just(s + "\n");
                    }
                }).subscribeOn(Schedulers.io());

        flowable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onSubscribe(Subscription s) {
                        subscription = s;
                        s.request(1);
                    }

                    @Override
                    public void onNext(final String s) {
                        try {
                            System.out.println("=onNext=="+Thread.currentThread().getName());
                            sb.append(s);
                            mTvText.setText(sb.toString());
                            Observable.timer(1,TimeUnit.SECONDS).observeOn(Schedulers.io()).subscribe(new Consumer<Long>() {
                                @Override
                                public void accept(Long aLong) throws Exception {
                                    subscription.request(1);
                                }
                            });


                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(Throwable t) {
                        Log.d(TAG, "ERROR" + t.getMessage());
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    StringBuilder sb = new StringBuilder();

}
