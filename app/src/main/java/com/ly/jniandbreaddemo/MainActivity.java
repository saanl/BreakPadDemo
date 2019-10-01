package com.ly.jniandbreaddemo;


import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;
import com.microsoft.appcenter.utils.async.AppCenterConsumer;

import java.util.HashMap;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("breakpad-core");
    }

    public static void initBreakpad(String path){
        initBreakpadNative(path);
    }

    private static native void initBreakpadNative(String path);
    private static native void setupNativeCrashesListener(String path);

    private static native void nativeCrash();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Example of a call to a native method
        TextView tv = (TextView) findViewById(R.id.sample_text);
        tv.setText("Test");

        initAppCenter();
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */


    public void initAppCenter(){
        AppCenter.setLogLevel(Log.VERBOSE);//4761c797-6a06-47ad-afaf-e9cb72c1c57a
        AppCenter.start(getApplication(), "13e87e25-b1f4-4564-a947-2fe56d4368ed",
                Analytics.class, Crashes.class);


       // Crashes.generateTestCrash();
        HashMap<String, String> properties = new HashMap<>();
        properties.put("Model", android.os.Build.MODEL);
        properties.put("Brand", android.os.Build.BRAND);
        properties.put("OS", android.os.Build.VERSION.RELEASE);

        Analytics.trackEvent("APP_METRICS", properties);

        Crashes.getMinidumpDirectory().thenAccept(new AppCenterConsumer<String>() {
            @Override
            public void accept(String path) {

                /* Path is null when Crashes is disabled. */
                if (path != null) {
                    Log.e("MinidumpDirectory"," "+path);
                    setupNativeCrashesListener(path);
                }
            }
        });


    }

    public void clickJava(View view){
        throw new RuntimeException("java level crash");
    }

    public void clickNative(View view){
        nativeCrash();
    }
}
