package jp.ac.titech.itpro.sdl.checklabo;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
//import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.TimeZone;

import com.echo.holographlibrary.Line;
import com.echo.holographlibrary.LineGraph;
import com.echo.holographlibrary.LinePoint;


public class MainActivity extends Activity{
    WifiManager manager;// = (WifiManager)getSystemService(WIFI_SERVICE);
    WifiInfo info;// = manager.getConnectionInfo();
    TextView textView;
    TextView timeView;
    TextView spendView;
    TextView graphx;
    TextView average;
    TextView graphTitle;
    ImageView labochikuView;
    ImageView labofukiView;

    ImageView labogurashiView;
    ImageView labogurashifukiView;

    ImageView ilovelabView;
    ImageView ilovelabfukiView;

    LineGraph graph;
    GraphValue value;



    private SimpleDateFormat sdf;
    private String TimeList;
    private long time;
    private long starttime;
    private int year;
    private int today;
    private long todayStaytime;
    private long savedTime;
    private int connectFlag;
    private int graphFlag;


    private int labochiku;
    private int labogurashi;
    private int ilovelab;

    private int labohaku; // 1時から6時にいることを確認するためのフラグ
    private int labo; // 吹き出しが見えてるか見えてないか

    String[] apInfo = new String[4];
    ArrayList<String> apData = new ArrayList<String>();
    //ArrayList<String> timeData = new ArrayList<String>();
    LinkedList<Integer> timeData = new LinkedList<Integer>();
    LinkedList<String> dayData = new LinkedList<String>();

    Calendar cal;// = Calendar.getInstance();
    Calendar mcal;

    //Line line;


    BufferedReader in = null;
    BufferedReader tin = null;
    BufferedReader din = null;
    FileOutputStream ftos;
    FileOutputStream fdos;

    final String FILENAME = "ap_data.txt";
    final String DATAFILE = "time_data.txt";
    final String DATAFILE2 = "day_data.txt";
    final String PREF = "acivement";

    //String string = "hello world!";

    SharedPreferences prefer;
    SharedPreferences.Editor editor;

    private LoopEngine loopEngine = new LoopEngine();
    //SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
    //SharedPreferences pref = this.getSharedPreferences("pref", MODE_PRIVATE);


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView)findViewById(R.id.ap_info);
        //timeView = (TextView)findViewById(R.id.timeView);
        spendView = (TextView)findViewById(R.id.spendtimeView);
        graphx = (TextView)findViewById(R.id.graph_text3);
        graphx.setTextSize(17.4f);
        average = (TextView)findViewById(R.id.average);
        value = (GraphValue)findViewById(R.id.value);
        graphTitle = (TextView)findViewById(R.id.graph_title);

        labofukiView = (ImageView)findViewById(R.id.labofuki);
        labogurashifukiView = (ImageView)findViewById(R.id.labogurashifuki);
        ilovelabfukiView = (ImageView)findViewById(R.id.ilovelabfuki);
        labochikuView = (ImageView)findViewById(R.id.labochikuView);
        labogurashiView = (ImageView)findViewById(R.id.labogurashiView);
        ilovelabView = (ImageView)findViewById(R.id.ilovelabView);



        prefer = getSharedPreferences("PREF", MODE_PRIVATE);
        editor = prefer.edit();

        labochiku = prefer.getInt("labochiku", 0);
        labogurashi = prefer.getInt("labogurashi", 0);
        ilovelab = prefer.getInt("ilovelab", 0);

        if(labochiku == 0) {
            //labochikuView.setVisibility(View.INVISIBLE);
            labochikuView.setImageResource(R.drawable.labochiku);
            labochikuView.setAlpha(100f);
        }else if(labochiku == 1){
            labochikuView.setImageResource(R.drawable.labochiku_silver);
        }else if(labochiku == 2){
            labochikuView.setImageResource(R.drawable.labochiku_gold);
        }else if(labochiku == 3){
            labochikuView.setImageResource(R.drawable.labochiku_black);
        }
        labochikuView.invalidate();

        //labogurashiView.setVisibility(View.INVISIBLE);

        if(labogurashi == 0) {
            //labochikuView.setVisibility(View.INVISIBLE);
            labogurashiView.setImageResource(R.drawable.labogurashi);
            labogurashiView.setAlpha(100f);
        }else if(labogurashi == 1){
            labogurashiView.setImageResource(R.drawable.labogurashi_silver);
        }else if(labogurashi == 2){
            labogurashiView.setImageResource(R.drawable.labogurashi_gold);
        }else if(labogurashi > 6){
            labogurashiView.setImageResource(R.drawable.labogurashi_black);
        }
        labogurashiView.invalidate();

        //ilovelabView.setVisibility(View.INVISIBLE);
        //ilovelabView.setImageResource(R.drawable.ilovelab);
        //ilovelabView.invalidate();

        // 吹き出したち
        labofukiView.setVisibility(View.INVISIBLE);
        labofukiView.setImageResource(R.drawable.labohiku_fukidashi);
        labofukiView.bringToFront();
        labofukiView.invalidate();

        labogurashifukiView.setVisibility(View.INVISIBLE);
        labogurashifukiView.setImageResource(R.drawable.labogurashi_fukidashi);
        labogurashifukiView.bringToFront();
        labogurashifukiView.invalidate();

        ilovelabfukiView.setVisibility(View.INVISIBLE);
        ilovelabfukiView.setImageResource(R.drawable.ilovelab_fukidashi);
        ilovelabfukiView.bringToFront();
        ilovelabfukiView.invalidate();



        labo = 0; // labochikuの吹き出しが出ているかどうか
        labohaku = 0; // 1時になったら1になる





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

        labochikuView.setOnClickListener(
                    new View.OnClickListener() {
                        public void onClick(View v) {
                            if (labo == 0) {
                                labofukiView.setVisibility(View.VISIBLE);
                                //labofukiView.setImageResource(R.drawable.labohiku_fukidashi);
                                //labofukiView.invalidate();
                                labo = 1;
                            } else if(labo == 1){
                                labofukiView.setVisibility(View.INVISIBLE);
                                labo = 0;
                            }
                            //Toast.makeText(getApplicationContext(), "labochiku", Toast.LENGTH_SHORT).show();
                        }
                    }
        );

        labogurashiView.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        if (labo == 0) {
                            labogurashifukiView.setVisibility(View.VISIBLE);
                            //labofukiView.setImageResource(R.drawable.labohiku_fukidashi);
                            //labofukiView.invalidate();
                            labo = 2;
                        } else if(labo == 2){
                            labogurashifukiView.setVisibility(View.INVISIBLE);
                            labo = 0;
                        }
                        //Toast.makeText(getApplicationContext(), "labochiku", Toast.LENGTH_SHORT).show();
                    }
                }
        );
        ilovelabView.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View v) {
                        if (labo == 0) {
                            ilovelabfukiView.setVisibility(View.VISIBLE);
                            //labofukiView.setImageResource(R.drawable.labohiku_fukidashi);
                            //labofukiView.invalidate();
                            labo = 3;
                        } else if(labo == 3){
                            ilovelabfukiView.setVisibility(View.INVISIBLE);
                            labo = 0;
                        }
                        //Toast.makeText(getApplicationContext(), "labochiku", Toast.LENGTH_SHORT).show();
                    }
                }
        );


        time = System.currentTimeMillis();
        sdf = new SimpleDateFormat("HH:mm:ss");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT"));
        TimeList = "start=" + sdf.format(time + (9 * 3600 * 1000));
        //timeView.setText(TimeList);

        starttime = time;

        connectFlag = 0;

        cal = Calendar.getInstance();
        mcal = Calendar.getInstance();

        today = cal.get(Calendar.DAY_OF_YEAR);
        year = cal.get(Calendar.YEAR);

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


        //textView.setText(apInfo[0] + "\n" + apInfo[1] + "\n" + apInfo[2] + "\n" + apInfo[3]);
        textView.setText(apInfo[0] + "\n接続中...");

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
            Log.d("FileAccess", "wifi file exists");
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
                //textView.append(apData.get(0));
                if(apData.get(0).isEmpty()){
                    Log.d("FileAccess", "don't have!");
                    //textView.append("Don't have apInfo\n");

                }
                Log.d("FileAccess", "wifi read!");
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

//        SharedPreferences.Editor editor = prefer.edit();
//        editor.putInt("kanzume", 1);
//        editor.commit();


        //deleteFile(DATAFILE);
        //deleteFile(DATAFILE2);

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
                Log.d("FileAccess", "time read!");
               // Log.d("FileContents", String.valueOf(timeData.get(0)));
                ftis.close();

            } catch (IOException e) {
                e.printStackTrace();
                Log.d("FileAccess", "error!");
            }
//            try {
//                ftos = openFileOutput(DATAFILE, Context.MODE_PRIVATE);
//            }catch  (IOException e) {
//                e.printStackTrace();
//                Log.d("FileAccess", "can't write!");
//            }

        }else{
//            today = cal.get(Calendar.DAY_OF_YEAR);
//            year = cal.get(Calendar.YEAR);
//            timeData.add(year);
//            timeData.add(today);
            timeData.add(18324125); //max 86,400,000
            timeData.add(32148073);
            timeData.add(15857302);
            timeData.add(38472628);
            timeData.add(34957384);
            timeData.add(0);
            timeData.add(30484728);
            Log.d("FileAccess", "don't have time data");
            try {
                ftos = openFileOutput(DATAFILE, Context.MODE_PRIVATE);
                ftos.close();
                //ftos.write("".getBytes());
                //Toast.makeText(getApplicationContext(), "time data file is created", Toast.LENGTH_SHORT).show();
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
                String str2 = din.readLine();
                while(str2 != null){
                    dayData.add(str2);
                    str2 = din.readLine();
                    //  i++;
                }
                Log.d("FileAccess", "read!");
                fdis.close();

            } catch (IOException e) {
                e.printStackTrace();
                Log.d("FileAccess", "error!");
            }
//            try {
//                fdos = openFileOutput(DATAFILE2, Context.MODE_PRIVATE);
//            }catch  (IOException e) {
//                e.printStackTrace();
//                Log.d("FileAccess", "can't write!");
//            }

        }else{
//            today = cal.get(Calendar.DAY_OF_YEAR);
//            year = cal.get(Calendar.YEAR);
            dayData.add(String.valueOf(year) + String.valueOf(today-7));
            dayData.add(String.valueOf(year) + String.valueOf(today-6));
            dayData.add(String.valueOf(year) + String.valueOf(today-5));
            dayData.add(String.valueOf(year) + String.valueOf(today-4));
            dayData.add(String.valueOf(year) + String.valueOf(today-3));
            dayData.add(String.valueOf(year) + String.valueOf(today-2));
            dayData.add(String.valueOf(year) + String.valueOf(today-1));
            //dayData.add(String.valueOf(year) + String.valueOf(today));

            Log.d("FileAccess", "don't have day data");
            try {
                fdos = openFileOutput(DATAFILE2, Context.MODE_PRIVATE);
                fdos.close();
                Log.d("FileAccess", "day file is created!");
                //Toast.makeText(getApplicationContext(), "day data file is created", Toast.LENGTH_SHORT).show();
            }catch  (IOException e) {
                e.printStackTrace();
                Log.d("FileAccess", "can't write!");
            }
        }

        // I love lab 判定
        int lovelab = 0;
        if(ilovelab == 0){
            for(int i = 1; i < 7; i++){
                if(timeData.get(timeData.size()-i) == 0){
                    lovelab = 1;
                    break;
                }
            }
            if(lovelab == 0){
                ilovelab = 1;
                editor.putInt("ilovelab", 1);
                editor.commit();
                //ilovelabView.setVisibility(View.VISIBLE);
            }
         }else if(ilovelab == 1){
            for(int i = 1; i < 14; i++){
                if(timeData.get(timeData.size()-i) == 0){
                    lovelab = 1;
                    break;
                }
            }
            if(lovelab == 0){
                ilovelab = 2;
                editor.putInt("ilovelab", 2);
                editor.commit();
                //ilovelabView.setVisibility(View.VISIBLE);
            }
        }else if(ilovelab == 2){
            for(int i = 1; i < 30; i++){
                if(timeData.get(timeData.size()-i) == 0){
                    lovelab = 1;
                    break;
                }
            }
            if(lovelab == 0){
                ilovelab = 3;
                editor.putInt("ilovelab", 3);
                editor.commit();
                //ilovelabView.setVisibility(View.VISIBLE);
            }
        }

        labogurashi = 1;
        editor.putInt("labogurashi", 1);
        editor.commit();

        if(ilovelab == 0) {
            //labochikuView.setVisibility(View.INVISIBLE);
            ilovelabView.setImageResource(R.drawable.ilovelab);
            ilovelabView.setAlpha(100f);
        }else if(ilovelab == 1){
            ilovelabView.setImageResource(R.drawable.ilovelab_silver);
        }else if(ilovelab == 2){
            ilovelabView.setImageResource(R.drawable.ilovelab_gold);
        }else if(ilovelab == 3){
            ilovelabView.setImageResource(R.drawable.ilovelab_black);
        }
        ilovelabView.invalidate();




        //int kanzume = prefer.getInt("kanzume", 0);
        //Toast.makeText(getApplicationContext(), "kanzume " + kanzume, Toast.LENGTH_LONG).show();

        //timeView.append(String.valueOf(timeData.get(0)) + " " + dayData.get(0) + "\n");
        //timeView.append("size of timeData = " + timeData.size() + "\n");
        //timeView.append("size of dayData = " + dayData.size() + "\n");

        // Draw line graph
        int number = 0;
        int ave = 0;
        Line line = new Line();
        if(timeData.size() > 6){
            while(number < 7){
                LinePoint linePoint = new LinePoint();
                linePoint.setY(timeData.get(timeData.size()- 7 + number) / (1000 * 60 * 60)); // 分単位
                ave += timeData.get(timeData.size()- 7 + number) / (1000 * 60);
                linePoint.setX(number);
                linePoint.setColor(Color.parseColor("#9acd32"));
                line.addPoint(linePoint);
                number++;
            }
        }else{
            while(number < timeData.size()){
                LinePoint linePoint = new LinePoint();
                linePoint.setY(timeData.get(number) / (1000 * 60 * 60)); // 分単位
                ave += timeData.get(number) / (1000 * 60);
                linePoint.setX(number);
                linePoint.setColor(Color.parseColor("#9acd32"));
                line.addPoint(linePoint);
                number++;
            }
        }

        ave = ave / 7;
        average.setText("週平均: " + ave / 60 + "時間" + ave % 60 + "分");


        line.setColor(Color.parseColor("#9acd32")); // 線のいろ
        graph = (LineGraph) findViewById(R.id.graph);
        graph.addLine(line);
        graph.setRangeX(0, 6.5f);
        graph.setRangeY(0, 24);

        Calendar gcal = Calendar.getInstance();

        gcal.set(gcal.get(Calendar.YEAR), gcal.get(Calendar.MONTH), gcal.get(Calendar.DAY_OF_MONTH) - 6);
        int gmonth = gcal.get(Calendar.MONTH);
        int gday = gcal.get(Calendar.DAY_OF_MONTH);

        for(int x = 0; x < 7; x++) {
            graphx.append("   " + String.valueOf(gmonth+1) + "/" + String.valueOf(gday) + "           ");
            gcal.add(Calendar.DAY_OF_MONTH, 1);
            gmonth = gcal.get(Calendar.MONTH);
            gday = gcal.get(Calendar.DAY_OF_MONTH);
        }

        graphTitle.setText("一週間のらぼちく時間");

        graphFlag = 0;

        // グラフが押された時
        graph.setOnPointClickedListener(new LineGraph.OnPointClickedListener() {
            @Override
            public void onClick(int lineIndex, int pointIndex) {
                float x = graph.xPixels(lineIndex, pointIndex);
                float y = graph.yPixels(lineIndex, pointIndex);

                if (graphFlag == 0) {
                    value.setValue(timeData.get(timeData.size() - 7 + pointIndex));
                    value.setXY(x, y);
                } else if (graphFlag == 1) {
                    mcal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), 1);
                    if (dayData.lastIndexOf(String.valueOf(year) + String.valueOf(mcal.get(Calendar.DAY_OF_YEAR) + pointIndex)) == -1) {
                        value.setValue(0);
                        //Log.d("gvalue", "value= " + timeData.lastIndexOf(dayData.lastIndexOf(String.valueOf(year) + String.valueOf(mcal.get(Calendar.DAY_OF_YEAR) + pointIndex))));
                    } else {
                        value.setValue(timeData.get(dayData.lastIndexOf(String.valueOf(year) + String.valueOf(mcal.get(Calendar.DAY_OF_YEAR) + pointIndex)))); // 分単位
                    }
                    value.setXY(x, y);
                }

                //Toast.makeText(getApplicationContext(), "Line " + lineIndex + "/ Point " + pointIndex + "clicked", Toast.LENGTH_SHORT).show();
            }
        });


        Switch sw = (Switch)findViewById(R.id.switch1);
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                //true -> 月間 faluse -> 週間
                //Toast.makeText(getApplicationContext(), isChecked + "clicked", Toast.LENGTH_SHORT).show();
                int number = 0;
                value.resetValue();
                if(isChecked){

                    int ave = 0;
                    graphFlag = 1;
                    graph.removeAllLines();

                    Line mline = new Line();
                    int days;
                    number = 0;
                    //mcal = Calendar.getInstance();

                    mcal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), 1);
                    int month = mcal.get(Calendar.MONTH);

                    if(month == 1 || month == 3 || month == 5 || month == 8 || month == 10){
                        days = 30;
                    }else{
                        days = 31;
                    }

                    graphTitle.setText((month + 1) +"月のらぼちく時間");

                    while(days > number){
                        LinePoint linePoint = new LinePoint();

                        if(dayData.lastIndexOf(String.valueOf(year) + String.valueOf(mcal.get(Calendar.DAY_OF_YEAR))) == -1){
                            linePoint.setY(0); // 分単位
                        }else {
                            linePoint.setY(timeData.get(dayData.lastIndexOf(String.valueOf(year) + String.valueOf(mcal.get(Calendar.DAY_OF_YEAR)))) / (1000 * 60 * 60)); // 分単位
                            ave += timeData.get(dayData.lastIndexOf(String.valueOf(year) + String.valueOf(mcal.get(Calendar.DAY_OF_YEAR)))) / (1000 * 60); // 分単位
                        }
                        linePoint.setX(number);
                        linePoint.setColor(Color.parseColor("#9acd32"));
                        mline.addPoint(linePoint);
                        number++;
                        if ((String.valueOf(year) + String.valueOf(mcal.get(Calendar.DAY_OF_YEAR))).equals(dayData.get(dayData.size()-1))){
                            break;
                        }
                        mcal.add(Calendar.DAY_OF_MONTH, 1);
                    }
                    mline.setColor(Color.parseColor("#9acd32")); // 線のいろ
                    //LineGraph graph = (LineGraph) findViewById(R.id.graph);
                    graph.addLine(mline);
                    graph.setRangeX(0, 31);
                    graph.setRangeY(0, 24);

                    ave = ave / days;
                    average.setText("月平均: " + (ave / 60) + "時間" + (ave % 60) + "分");

                    mcal.set(mcal.get(Calendar.YEAR), mcal.get(Calendar.MONTH), 1);
                    graphx.setText("");
                    for(int x = 0; x <= (days/2); x++) {
                        if(mcal.get(Calendar.DAY_OF_MONTH) < 10){
                            //graphx.setTextSize(40);
                            graphx.append("      " + String.valueOf(String.valueOf(mcal.get(Calendar.DAY_OF_MONTH))) + " ");
                        }else{

                            graphx.append("     " + String.valueOf(String.valueOf(mcal.get(Calendar.DAY_OF_MONTH))) + "");
                        }
                        mcal.add(Calendar.DAY_OF_MONTH, 2);
                    }


                }else{
                    graphTitle.setText("一週間のらぼちく時間");
                    graphFlag = 0;
                    int ave = 0;
                    graph.removeAllLines();
                    number = 0;
                    Line line = new Line();
                    if(timeData.size() > 6){
                        while(number < 7){
                            LinePoint linePoint = new LinePoint();
                            linePoint.setY(timeData.get(timeData.size() - 7 + number) / (1000 * 60 * 60)); // 分単位
                            ave += timeData.get(timeData.size() - 7 + number) / (1000 * 60);
                            linePoint.setX(number);
                            linePoint.setColor(Color.parseColor("#9acd32"));
                            line.addPoint(linePoint);
                            number++;
                        }
                    }else {
                        while (number < timeData.size()) {
                            LinePoint linePoint = new LinePoint();
                            linePoint.setY(timeData.get(number) / (1000 * 60 * 60)); // 分単位
                            ave += timeData.get(number) / (1000 * 60);
                            linePoint.setX(number);
                            linePoint.setColor(Color.parseColor("#9acd32"));
                            line.addPoint(linePoint);
                            number++;
                        }
                    }

                    line.setColor(Color.parseColor("#9acd32")); // 線のいろ
                    //LineGraph graph = (LineGraph) findViewById(R.id.graph);
                    graph.addLine(line);
                    graph.setRangeX(0, 6.5f);
                    graph.setRangeY(0, 24);

                    ave = ave / 7;
                    average.setText("週平均: " + (ave / 60) + "時間" + (ave % 60) + "分");

                    Calendar gcal = Calendar.getInstance();

                    gcal.set(gcal.get(Calendar.YEAR), gcal.get(Calendar.MONTH), gcal.get(Calendar.DAY_OF_MONTH) - 6);
                    int gmonth = gcal.get(Calendar.MONTH);
                    int gday = gcal.get(Calendar.DAY_OF_MONTH);

                    graphx.setText("");
                    for(int x = 0; x < 7; x++) {
                        graphx.append("   " + String.valueOf(gmonth + 1) + "/" + String.valueOf(gday) + "           ");
                        gcal.add(Calendar.DAY_OF_MONTH, 1);
                        gmonth = gcal.get(Calendar.MONTH);
                        gday = gcal.get(Calendar.DAY_OF_MONTH);
                    }
                }
            }
        });
    }

    @Override
    protected void onPause(){
        super.onPause();

//        today = cal.get(Calendar.DAY_OF_YEAR);
//        year = cal.get(Calendar.YEAR);
        time = System.currentTimeMillis();
        todayStaytime = time - starttime;

        today = cal.get(Calendar.DAY_OF_YEAR);
        year = cal.get(Calendar.YEAR);

        //textView.setText(dayData.get(dayData.size()-1) + String.valueOf(year) + String.valueOf(today) + (dayData.get(dayData.size() - 1)).equals(String.valueOf(year) + String.valueOf(today)));
        if(connectFlag == 1) {
            if ((dayData.get(dayData.size() - 1)).equals(String.valueOf(year) + String.valueOf(today))) {
                timeData.set(timeData.size() - 1, (int) todayStaytime + timeData.get(timeData.size() - 1));
                Log.d("onDisconnected", "equal data's date today");
            }else if(dayData.size() != 0){
                timeData.add((int)todayStaytime);
                dayData.add(String.valueOf(year) + String.valueOf(today));
                int cnt = 1;
                while(true){
                    if(dayData.get(dayData.size() - 1).equals(String.valueOf(year) + String.valueOf(today-cnt))){
                        break;
                    }else{
                        timeData.add(0);
                        dayData.add(String.valueOf(year) + String.valueOf(today-cnt));
                        cnt++;
                    }
                }
            } else {
                timeData.add((int) todayStaytime);
                dayData.add(String.valueOf(year) + String.valueOf(today));
                Log.d("onDisconnected", "not equal");
            }
        }



        try{
            ftos = openFileOutput(DATAFILE, Context.MODE_PRIVATE);
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(ftos));
            for(int timedata : timeData) {
                out.write(String.valueOf(timedata) + "\n");
                //out.newLine();
            }
            Log.d("FileAccess", "onDestroy, time data is written");
            out.close();
            ftos.close();
        }catch (IOException e){
            e.printStackTrace();
            Log.d("FileAccess", "onDestroy, time data cannot be written");
        }

        try{
            fdos = openFileOutput(DATAFILE2, Context.MODE_PRIVATE);
            BufferedWriter out2 = new BufferedWriter(new OutputStreamWriter(fdos));
            for(String text : dayData){
                out2.write(text + "\n");
                //out2.newLine();
            }
            Log.d("FileAccess", "onDestroy, day data is written");
            out2.close();
            fdos.close();
        }catch (IOException e){
            e.printStackTrace();
            Log.d("FileAccess", "onDestroy, day data cannot be written");
        }
    }

    @Override
    protected void onResume(){
        super.onResume();

    }

//    @Override
//    protected void onPause(){
//        super.onPause();
//        unregisterReceiver(mBroadcastReceiver);
//    }

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

                            today = cal.get(Calendar.DAY_OF_YEAR);
                            year = cal.get(Calendar.YEAR);
                            if (dayData.get(dayData.size() - 1).equals(String.valueOf(year) + String.valueOf(today))) {
                                savedTime = timeData.get(timeData.size() - 1);
                            }else{
                                savedTime = 0;
                            }
                            loopEngine.start();
                            connectFlag = 1;

                            Toast.makeText(getApplicationContext(), "labochiku" + savedTime, Toast.LENGTH_SHORT).show();



                        }
                        Log.d("wifi change", "CONNECTED");
                        break;
                    case DISCONNECTING:
                        //Toast.makeText(getApplicationContext(), "disconnecting!", Toast.LENGTH_SHORT).show();
                        Log.d("wifi change", "DISCONNECTING");
                        break;
                    case DISCONNECTED:
                        if(connectFlag == 1) {
                            connectFlag = 0;
                            time = System.currentTimeMillis();
                            TimeList = TimeList + "  Stop= " + sdf.format(time + (9 * 3600 * 1000)) + "\n";
                            TimeList = TimeList + "  (Lap= " + sdf.format(time - starttime) + ")\n";
                            //TimeList=TimeList + "  (Lap= " + (time - starttime) + ")\n";
                            //timeView.setText(TimeList);
                            todayStaytime = time - starttime;

                            today = cal.get(Calendar.DAY_OF_YEAR);
                            year = cal.get(Calendar.YEAR);


                            if (dayData.get(dayData.size() - 1).equals(String.valueOf(year) + String.valueOf(today))) {
                                timeData.set(timeData.size() - 1, (int) todayStaytime + timeData.get(timeData.size() - 1));
                                //timeView.append(String.valueOf(dayData.get(dayData.size() - 1)) + "\n");
                                //timeView.append(String.valueOf("time = " + timeData.get(timeData.size() - 1)) + "\n");
                                Log.d("onDisconnected", "equal data's date today");
                            }else if(dayData.size() != 0){
                                timeData.add((int)todayStaytime);
                                dayData.add(String.valueOf(year) + String.valueOf(today));
                                int cnt = 1;
                                while(true){
                                    if(dayData.get(dayData.size() - 1).equals(String.valueOf(year) + String.valueOf(today-cnt))){
                                        break;
                                    }else{
                                        timeData.add(0);
                                        dayData.add(String.valueOf(year) + String.valueOf(today-cnt));
                                        cnt++;
                                    }
                                }
                            } else {
                                timeData.add((int)todayStaytime);
                                dayData.add(String.valueOf(year) + String.valueOf(today));
                                //timeView.append(String.valueOf(dayData.get(dayData.size() - 2)) + "yesterday\n");
                                Log.d("onDisconnected", "not equal");
                            }


                            loopEngine.stop();
                            //Toast.makeText(getApplicationContext(), "discon
                            // nected!", Toast.LENGTH_SHORT).show();
                            Log.d("wifi change", "DISCONNECTED");
                        }
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
        spendView.setText("今日のらぼちく時間\n" + sdf.format(time - starttime + savedTime)); // 接続時間を計測
        if((time - starttime + savedTime) > 86400000 && labochiku == 2) {
            editor.putInt("labochiku", 3);
            editor.commit();
            labochikuView.setVisibility(View.VISIBLE);
            labochiku = 3;

        }else if((time - starttime + savedTime) > 43200000 && labochiku == 1){
            editor.putInt("labochiku", 2);
            editor.commit();
            labochikuView.setVisibility(View.VISIBLE);
            labochiku = 2;

        }else if((time - starttime + savedTime) > 21600000 && labochiku == 0) {
            editor.putInt("labochiku", 1);
            editor.commit();
            labochikuView.setVisibility(View.VISIBLE);
            labochiku = 1;
            //Toast.makeText(getApplicationContext(), "labochiku", Toast.LENGTH_SHORT).show();

        }


        if(labohaku == 0 && cal.get(Calendar.HOUR_OF_DAY) == 1){
            labohaku = 1;
        }else if(labohaku == 1 && cal.get(Calendar.HOUR_OF_DAY) == 6){
            editor.putInt("labogurashi", labogurashi+1);
            editor.commit();
            //labogurashiView.setVisibility(View.VISIBLE);
            labogurashi += 1;
            labohaku = 0;
        }
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
