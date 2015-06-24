package jp.ac.titech.itpro.sdl.checklabo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by airi on 15/06/23.
 */
public class SetupActivity extends Activity {
    /* Called for Wifi setting */

    ListView labolv;
    String[] labowifi;
    String nowwifi;
    String FILENAME;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        Intent intent = getIntent();
        labowifi = intent.getStringArrayExtra("laboWifi");
        nowwifi = intent.getStringExtra("nowWifi");
        FILENAME = intent.getStringExtra("filename");

        //Toast.makeText(this, labowifi[0], Toast.LENGTH_LONG).show();

        labolv = (ListView)findViewById(R.id.listView);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, labowifi);

        labolv.setAdapter(adapter);


        Button addbutton = (Button)findViewById(R.id.add_button); // add wifi SSID to local text
        addbutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    FileOutputStream fos = openFileOutput(FILENAME, Context.MODE_APPEND);
                    fos.write(nowwifi.getBytes());
                    Toast.makeText(SetupActivity.this, "add labo wifi", Toast.LENGTH_LONG).show();

                }catch  (IOException e) {
                    e.printStackTrace();
                    Log.d("FileAccess", "can't write!");
                }
//                deleteFile(FILENAME);
//                Toast.makeText(getApplicationContext(), "Delete local file!", Toast.LENGTH_LONG).show();
            }
        });

        Button delbutton = (Button)findViewById(R.id.del_button); // delete wifi SSID from local text
        delbutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
//                deleteFile(FILENAME);
//                Toast.makeText(getApplicationContext(), "Delete local file!", Toast.LENGTH_LONG).show();
            }
        });



    }

}
