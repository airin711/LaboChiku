package jp.ac.titech.itpro.sdl.checklabo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by airi on 15/06/23.
 */
public class SetupActivity extends Activity {
    /* Called for Wifi setting */

    ListView labolv;
    String[] labowifi;
    String nowwifi;
    String FILENAME;
    String clickedItem;
    ArrayList<String> laboList;
    ArrayAdapter<String> adapter;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        Intent intent = getIntent();
        laboList = intent.getStringArrayListExtra("laboWifi");
        nowwifi = intent.getStringExtra("nowWifi");
        FILENAME = intent.getStringExtra("filename");

        //Toast.makeText(this, labowifi[0], Toast.LENGTH_LONG).show();

        //laboList = new ArrayList<String>(Arrays.asList(labowifi)); // array to list to become easy to remove
        labolv = (ListView)findViewById(R.id.listView);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, laboList);

        labolv.setAdapter(adapter);


        labolv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListView listView = (ListView) parent;
                clickedItem = (String) listView.getItemAtPosition(position);
                //Toast.makeText(getApplicationContext(), item + " clicked",
                //        Toast.LENGTH_LONG).show();
            }
        });


        Button addbutton = (Button)findViewById(R.id.add_button); // add wifi SSID to local text
        addbutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    laboList.add(nowwifi);
                    //adapter = new ArrayAdapter<String>(SetupActivity.this, android.R.layout.simple_list_item_1, laboList);
                    adapter.notifyDataSetChanged();
                    //labolv.setAdapter(adapter);

                    //setContentView(labolv);

                    FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_APPEND);
                    fos.write((nowwifi + "\n").getBytes());
                    fos.close();
                    Toast.makeText(SetupActivity.this, "nowwifi:" + nowwifi, Toast.LENGTH_LONG).show();

                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d("FileAccess", "can't write!");
                    Toast.makeText(getApplicationContext(), "error!", Toast.LENGTH_LONG).show();
                }
//                deleteFile(FILENAME);

            }
        });

        Button delbutton = (Button)findViewById(R.id.del_button); // delete wifi SSID from local text
        delbutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    laboList.remove(clickedItem);
                    adapter.notifyDataSetChanged();
                    FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
                    for (int i = 0; i < laboList.size(); i++) {
                        fos.write((laboList.get(i) + "\n").getBytes());
                    }
                    fos.close();
                    Toast.makeText(SetupActivity.this, "delete:" + clickedItem, Toast.LENGTH_LONG).show();
                }catch (IOException e){
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "error!", Toast.LENGTH_LONG).show();
                }

            }
        });



    }

}
