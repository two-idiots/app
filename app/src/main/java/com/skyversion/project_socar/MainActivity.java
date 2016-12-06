package com.skyversion.project_socar;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.skyversion.project_socar.bluetooth.BtManager;

public class MainActivity extends Activity {

    private WebView mWebView;
    private Handler mHandler;
    private BtManager btManager;
    private Toast toast;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        init();
    }

    private void init(){
        mHandler = new Handler();
        toast = new Toast(this);

        View view = Toast.makeText(getBaseContext(), "", Toast.LENGTH_SHORT).getView();
        toast.setView(view);

        btManager = new BtManager(this, toast);
        mWebView = (WebView)findViewById(R.id.myWebView);

        mWebView.getSettings().setJavaScriptEnabled(true);
        mWebView.getSettings().setDisplayZoomControls(false);
        // hide zoom control
        mWebView.setWebChromeClient(new WebChromeClient());
        mWebView.addJavascriptInterface(new AndroidBridge(), "android");
        mWebView.loadUrl("http://10.1.65.64:3000");
    }

    public void btOn(View view){
        if(btManager.isBluetoothEnable())
            btManager.enable();
    }

    public void btOff(View view){
        btManager.disable();
    }

    public void btScan(View view){
        if(btManager.isBluetoothEnable())
            return;

        btManager.scanLeDevice(true);
    }

    public void showBtList(View view){
        btManager.showDeviceList();
    }

    public void connect(View view){
        if(btManager.connect("68:9E:19:07:DE:B8"))
            Toast.makeText(MainActivity.this, "Connect", Toast.LENGTH_SHORT).show();

        else
            Toast.makeText(MainActivity.this, "no Connect", Toast.LENGTH_SHORT).show();
    }

    public void disconnect(View view){
        btManager.disconnect();
    }

    public void go(View view){
        NotificationManager nm = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mCompatBuilder = new NotificationCompat.Builder(this);
        mCompatBuilder.setSmallIcon(R.drawable.icon);
//        mCompatBuilder.setTicker("NotificationCompat.Builder");
//        mCompatBuilder.setContentTitle("Title");
//        mCompatBuilder.setContentText("NotificationCompat.Builder Message");
        mCompatBuilder.setContentIntent(pendingIntent);

        mCompatBuilder.setAutoCancel(false);
        // 알람 터치 시 반응 후 알림 삭제 여부

//        mCompatBuilder.addAction(R.mipmap.ic_launcher, "Show", pendingIntent);
//        mCompatBuilder.addAction(R.mipmap.ic_launcher, "Hide", pendingIntent);
//        mCompatBuilder.addAction(R.mipmap.ic_launcher, "Remove", pendingIntent);

        RemoteViews expandedView = new RemoteViews(this.getPackageName(), R.layout.notification);
        mCompatBuilder.setContent(expandedView);

        nm.notify(222, mCompatBuilder.build());
        // 고유 id로 알림 생성
    }

    private class AndroidBridge{
        @JavascriptInterface
        public void setMessage(final String argument){
            mHandler.post(new Runnable(){
                @Override
                public void run() {
                    Toast.makeText(getBaseContext(), argument, Toast.LENGTH_SHORT).show();

                }
            });
        }
    } // webview to android

    @Override
    protected void onPause() {
        super.onPause();


    }

    @Override
    protected void onDestroy() {
        btManager.close();

        super.onDestroy();
    }
}
