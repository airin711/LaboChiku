package jp.ac.titech.itpro.sdl.checklabo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

/**
 * Created by airi on 15/06/23.
 */
public class SetupActivity extends Activity {
    /* Called for Wifi setting */

    ListView labolv;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        Intent intent = getIntent();
        String[] labowifi = intent.getStringArrayExtra("laboWifi");
        //Toast.makeText(this, labowifi[0], Toast.LENGTH_LONG).show();

        labolv = (ListView)findViewById(R.id.listView);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, labowifi);

        labolv.setAdapter(adapter);

        Button addbutton = (Button)findViewById(R.id.add_button); // add wifi SSID to local text
        addbutton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
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
