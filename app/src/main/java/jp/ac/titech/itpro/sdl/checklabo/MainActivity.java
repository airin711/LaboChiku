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
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.TimeZone;


public class MainActivity extends Activity{
    WifiManager manager;// = (WifiManager)getSystemService(WIFI_SERVICE);
    WifiInfo info;// = manager.getConnectionInfo();
    TextView textView;
    TextView timeView;
    TextView spendView;


    private SimpleDateFormat sdf;
    private String TimeList;
    private long time;
    private long starttime;
    private int year;
    private int today;
    private long todayStaytime;
    String[] apInfo = new String[4];
    ArrayList<String> apData = new ArrayList<String>();
    //ArrayList<String> timeData = new ArrayList<String>();
    LinkedList<Integer> timeData = new LinkedList<Integer>();
    LinkedList<String> dayData = new LinkedList<String>();

    Calendar cal;// = Calendar.getInstance();



    BufferedReader in = null;
    BufferedReader tin = null;
    BufferedReader din = null;
    FileOutputStream ftos;
    FileOutputStream fdos;

    final String FILENAME = "ap_data.txt";
    final String DATAFILE = "time_data.txt";
    final String DATAFILE2 = "day_data.txt";
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
        //timeView.setText(TimeList);

        starttime = time;

//        today = cal.get(Calendar.DAY_OF_MONTH);
//        month = cal.get(Calendar.MONTH);

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
        //filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        registerReceiver(mBroadcastReceiver, filter);


        String path = (new StringBuffer()).append(getFilesDir()).append("/").append(FILENAME).toString();
        Log.d("path", path);
        String path2 = (new StringBuffer()).append(getFilesDir()).append("/").append(DATAFILE).toString();
        Log.d("path", path2);
        String path3 = (new StringBuffer()).append(getFilesDir()).append("/").append(DATAFILE2).toString();
        Log.d("path", path3);


        // file open for wifi data
        File file = new File(path);
        if (file.exists()) {
            Log.d("FileAccess", "file exists");
            try {

                FileInputStream fis = openFileInput(FILENAME);
                in = new BufferedReader(new InputStreamReader(fis));
                //int i = 0;
                //apData[0] = null;
                String str = in.readLine();
                while(str != null){
                    //apData[i] = str;
                    apData.add(str);
                    str = in.readLine();
                  //  i++;
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
                fos.close();
            }catch  (IOException e) {
                e.printStackTrace();
                Log.d("FileAccess", "can't write!");
            }
        }

        deleteFile(path2);

        // file open for time data
        File file2 = new File(path2);
        if (file2.exists()) {
            Log.d("FileAccess", "time file exists");
            try {
                FileInputStream ftis = openFileInput(DATAFILE);
                tin = new BufferedReader(new InputStreamReader(ftis));
                //int i = 0;
                String str = tin.readLine();
                while(str != null){
                    timeData.add(Integer.parseInt(str));
                    str = tin.readLine();
                  //  i++;
                }
                //textView2.append(timeData.get(0));
                Log.d("FileAccess", "read!");
                ftis.close();

            } catch (IOException e) {
                e.printStackTrace();
                Log.d("FileAccess", "error!");
            }
            try {
                ftos = openFileOutput(DATAFILE, Context.MODE_PRIVATE);
            }catch  (IOException e) {
                e.printStackTrace();
                Log.d("FileAccess", "can't write!");
            }

        }else{
//            today = cal.get(Calendar.DAY_OF_YEAR);
//            year = cal.get(Calendar.YEAR);
//            timeData.add(year);
//            timeData.add(today);
            timeData.add(0);
            Log.d("FileAccess", "don't have apdata");
            try {
                ftos = openFileOutput(DATAFILE, Context.MODE_PRIVATE);
                //ftos.write("".getBytes());
                Toast.makeText(getApplicationContext(), "time data file is created", Toast.LENGTH_SHORT).show();
                //ftos.close();
            }catch  (IOException e) {
                e.printStackTrace();
                Log.d("FileAccess", "can't write!");
            }
        }

        // file open for day data
        File file3 = new File(path3);
        if (file3.exists()) {
            Log.d("FileAccess", "day file exists");
            try {
                FileInputStream fdis = openFileInput(DATAFILE2);
                din = new BufferedReader(new InputStreamReader(fdis));
                //int i = 0;
                String str = din.readLine();
                while(str != null){
                    dayData.add(str);
                    str = din.readLine();
                    //  i++;
                }
                Log.d("FileAccess", "read!");
                fdis.close();

            } catch (IOException e) {
                e.printStackTrace();
                Log.d("FileAccess", "error!");
            }
            try {
                fdos = openFileOutput(DATAFILE2, Context.MODE_PRIVATE);
            }catch  (IOException e) {
                e.printStackTrace();
                Log.d("FileAccess", "can't write!");
            }

        }else{
            today = cal.get(Calendar.DAY_OF_YEAR);
            year = cal.get(Calendar.YEAR);
            dayData.add(String.valueOf(year) + "/" + String.valueOf(today));

            Log.d("FileAccess", "don't have apdata");
            try {
                fdos = openFileOutput(DATAFILE2, Context.MODE_PRIVATE);
                Toast.makeText(getApplicationContext(), "day data file is created", Toast.LENGTH_SHORT).show();
            }catch  (IOException e) {
                e.printStackTrace();
                Log.d("FileAccess", "can't write!");
            }
        }

        timeView.append(String.valueOf(timeData.get(0)) + " " +dayData.get(0));


    }

    @Override
    protected void onDestroy(){
        super.onDestroy();

//        today = cal.get(Calendar.DAY_OF_YEAR);
//        year = cal.get(Calendar.YEAR);
        time = System.currentTimeMillis();
        todayStaytime = time - starttime;
        try{
            for(Integer time : timeData) {
                //ftos.write(String.valueOf(todayStaytime).getBytes());
                ftos.write((String.valueOf(time) + "\n").getBytes());
                ftos.close();
            }
        }catch (IOException e){
            e.printStackTrace();
        }

        try{
            for(String text : dayData){
                fdos.write(text.getBytes());
                fdos.close();
            }
        }catch (IOException e){
            e.printStackTrace();
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

            if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)){
                NetworkInfo netInfo	= intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                NetworkInfo.State state = netInfo.getState();
                switch(state){
                    case CONNECTING:
                        //Toast.makeText(getApplicationContext(), "connecting!", Toast.LENGTH_SHORT).show();
                        Log.d("wifi change", "CONNECTING");
                        break;
                    case CONNECTED:
                        manager = (WifiManager)getSystemService(WIFI_SERVICE);
                        info = manager.getConnectionInfo();
                        if(apData.indexOf(info.getSSID())!=-1) {
                            spendView.setText(sdf.format(time - starttime));
                            time = System.currentTimeMillis();
                            starttime = time;
                            loopEngine.start();
                            Toast.makeText(getApplicationContext(), "connected!", Toast.LENGTH_SHORT).show();
                        }
                        Log.d("wifi change", "CONNECTED");
                        break;
                    case DISCONNECTING:
                        //Toast.makeText(getApplicationContext(), "disconnecting!", Toast.LENGTH_SHORT).show();
                        Log.d("wifi change", "DISCONNECTING");
                        break;
                    case DISCONNECTED:
                        time = System.currentTimeMillis();
                        TimeList=TimeList + "  Stop= " + sdf.format(time + (9 * 3600 * 1000)) + "\n";
                        TimeList=TimeList + "  (Lap= " + sdf.format(time - starttime) + ")\n";
                        //TimeList=TimeList + "  (Lap= " + (time - starttime) + ")\n";
                        //timeView.setText(TimeList);
                        todayStaytime = time - starttime;

                        today = cal.get(Calendar.DAY_OF_YEAR);
                        year = cal.get(Calendar.YEAR);


                        //if((timeData.get(0) == year) & timeData.get(1) == today){
                        if(dayData.get(dayData.size()-1).equals(String.valueOf(year) + "/" + String.valueOf(today))){
                            timeData.set(timeData.size()-1, (int)todayStaytime + timeData.get(timeData.size()-1));
                            timeView.append(String.valueOf(dayData.get(dayData.size() - 1)));
                        }else{
                            timeData.add((int)todayStaytime);
                            dayData.add(String.valueOf(year) + "/" + String.valueOf(today));
                            timeView.append(String.valueOf(dayData.get(dayData.size()-2)) + "yesterday");
                        }

                        loopEngine.stop();
                        //Toast.makeText(getApplicationContext(), "discon
                        // nected!", Toast.LENGTH_SHORT).show();
                        Log.d("wifi change", "DISCONNECTED");
                        break;
                    default:
                        Toast.makeText(getApplicationContext(), "default...", Toast.LENGTH_SHORT).show();
                        Log.d("wifi change", "UNKNOWN");
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
