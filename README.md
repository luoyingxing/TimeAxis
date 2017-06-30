# TimeAxis
TimeAxisView 简单的时光轴
使用如下：
1、在 Activity 中：
public class MainActivity extends AppCompatActivity {
    private TimeAxisView mTimeAxisView;
    private String text[] = {"星期一", "星期二", "星期三", "星期四", "星期五", "星期六"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTimeAxisView = (TimeAxisView) findViewById(R.id.ta_view);
        mTimeAxisView.setTextList(text);
    }
}

2、在activity_main.xml中：
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="@color/white"
    android:orientation="vertical">

    <com.luo.timeaxis.TimeAxisView
        android:id="@+id/ta_view"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:paddingTop="10dp"
        app:circleColor="@color/colorAccent"
        app:circleTextSpace="2dp"
        app:interval="4dp"
        app:lineStrokeWidth="1dp"
        app:orientation="horizontal"
        app:radius="12dp"
        app:strokeWidth="1dp"
        app:textColor="@color/colorAccent"
        app:textSize="13sp" />

</LinearLayout>

3、运行直接可以看到效果。