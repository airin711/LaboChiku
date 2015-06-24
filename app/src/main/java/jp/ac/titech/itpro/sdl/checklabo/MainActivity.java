package jp.ac.titech.itpro.sdl.checklabo;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.TimeZone;


public class MainActivity extends Activity{
    WifiManager manager;
    WifiInfo info;
    TextView textView;
    TextView timeView;
    TextView spendView;

    private SimpleDateFormat sdf;
    private String TimeList;
    private long time;
    private long starttime;
    String[] apInfo = new String[4];
    ArrayList<String> apData = new ArrayList<String>();



    BufferedReader in = null;

    final String FILENAME = "ap_data.txt";
    //String string = "hello world!";

    private LoopEngine loopEngine = new LoopEngine();
    //SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
    //SharedPreferences pref = this.getSharedPreferences("pref", MODE_PRIVATE);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView)findViewById(R.id.ap_info);
        timeView = (TextView)findViewById(R.id.timeView);
        spendView = (TextView)findViewById(R.id.spendtimeView);

        //for(int i = 0; i < 20; i++){
          //  apData[i] = ""; // initialize labo wifi data to display to list view
        //}

        Button delbutton = (Button)findViewById(R.id.setup_button);
        delbutton.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
//                deleteFile(FILENAME);
//                Toast.makeText(getApplicationContext(), "Delete local file!", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(MainActivity.this, SetupActivity.class);
                //intent.setClassName("jp.ac.titech.itpro.sdl.checklabo", "jp.ac.titech.itpro.sdl.checklabo.SetupActivity");
                intent.putStringArrayListExtra("laboWifi", apData);
                intent.putExtra("nowWifi", info.getSSID());
                intent.putExtra("filename", FILENAME);
                startActivity(intent);
            }
        });

        time = System.currentTimeMillis();
        sdf = new SimpleDateFormat("HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        TimeList = "start=" + sdf.format(time + (9 * 3600 * 1000));
        timeView.setText(TimeList);
        starttime = time;

        manager = (WifiManager)getSystemService(WIFI_SERVICE);
        info = manager.getConnectionInfo();


        // SSID を取得
        apInfo[0] = String.format("SSID : %s", info.getSSID());

        // MAC Addressを取得
        apInfo[1] = String.format("MAC Address : %s", info.getMacAddress());

        // IP Addressを取得
        int ipAdr = info.getIpAddress();
        apInfo[2] = String.format("IP Address : %02d.%02d.%02d.%02d",
                (ipAdr >> 0) & 0xff, (ipAdr >> 8) & 0xff, (ipAdr >> 16) & 0xff, (ipAdr >> 24) & 0xff);

        if(manager.isWifiEnabled()) apInfo[3] = "Enabled!";
        else apInfo[3] = "Not enabled!";


        textView.setText(apInfo[0] + "\n" + apInfo[1] + "\n" + apInfo[2] + "\n" + apInfo[3]);

        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        registerReceiver(mBroadcastReceiver, filter);


        String path = (new StringBuffer()).append(getFilesDir()).append("/").append(FILENAME).toString();
        Log.d("path", path);


        File file = new File(path);
        if (file.exists()) {
            Log.d("FileAccess", "file exists");
            try {

                FileInputStream fis = openFileInput(FILENAME);
                in = new BufferedReader(new InputStreamReader(fis));
                int i = 0;
                //apData[0] = null;
                String str = in.readLine();
                while(str != null){
                    //apData[i] = str;
                    apData.add(str);
                    str = in.readLine();
                    i++;
                }
                textView.append(apData.get(0));
                if(apData.get(0).isEmpty()){
                    Log.d("FileAccess", "don't have!");
                    textView.append("Don't have apInfo\n");

                }
                Log.d("FileAccess", "read!");
                fis.close();

            } catch (IOException e) {
                e.printStackTrace();
                Log.d("FileAccess", "error!");
            }

        }else{
            Log.d("FileAccess", "don't have apdata");
            try {
                FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_APPEND);
                fos.write("".getBytes());
            }catch  (IOException e) {
                e.printStackTrace();
                Log.d("FileAccess", "can't write!");
            }
        }


    }


    @Override
    protected void onResume(){
        super.onResume();

    }

    @Override
    protected void onPause(){
        super.onPause();
//        unregisterReceiver(mBroadcastReceiver);
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(WifiManager.WIFI_STATE_CHANGED_ACTION)){
                int extraWifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);
                if(extraWifiState == WifiManager.WIFI_STATE_DISABLED){
                    textView.append("disabled-!");
                    time = System.currentTimeMillis();
                    TimeList=TimeList + "  Stop= " + sdf.format(time + (9 * 3600 * 1000)) + "\n";
                    TimeList=TimeList + "  (Lap= " + sdf.format(time - starttime) + ")\n";
                    //TimeList=TimeList + "  (Lap= " + (time - starttime) + ")\n";
                    timeView.setText(TimeList);
                    loopEngine.stop();

                }else if(extraWifiState == WifiManager.WIFI_STATE_ENABLED){// && apInfo[0] == idData){

                    //textView.append(String.format("enabled-!, idData:%s\n", idData));
                    spendView.setText(sdf.format(time - starttime));
                    loopEngine.start();
                }
            }
        }
    };

    public void update(){
        time = System.currentTimeMillis();
        spendView.setText(sdf.format(time - starttime)); // 接続時間を計測
    }


    //一定時間後にupdateを呼ぶためのオブジェクト
    class LoopEngine extends Handler {
        private boolean isUpdate;
        public void start(){
            this.isUpdate = true;
            handleMessage(new Message());
        }
        public void stop(){
            this.isUpdate = false;
        }
        @Override
        public void handleMessage(Message msg) {
            this.removeMessages(0);//既存のメッセージは削除
            if(this.isUpdate){
                MainActivity.this.update();//自信が発したメッセージを取得してupdateを実行
                sendMessageDelayed(obtainMessage(0), 1000);//1秒後にメッセージを出力
            }
        }
    };

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

//        return super.onOptionsItemSelected(item);
//    }
}
