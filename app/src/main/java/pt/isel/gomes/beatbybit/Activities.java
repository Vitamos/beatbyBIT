package pt.isel.gomes.beatbybit;

import android.app.Activity;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import pt.isel.gomes.beatbybit.util.Engine;


public class Activities extends Activity {
    private ListView listView;
    static final String PROVIDER_NAME = "com.example.provider.DownProvider";
    static final String URL = "content://" + PROVIDER_NAME + "/data";
    static final Uri URI = Uri.parse(URL);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activities);
        Engine engine = (Engine) getIntent().getSerializableExtra("engine");
        // Get ListView object from xml
        listView = (ListView) findViewById(R.id.list);

        Cursor cursor= getContentResolver().query(URI,null,null,null,null);

        // Defined Array values to show in ListView
     /*   String[][] analogs = engine.analogString();
        String[] values = new String[analogs.length];
        for (int i = 0; i < analogs.length; i++) {
            String line = "";
            String[] channel = analogs[i];

            values[i] = line;
        }*/
        String[] values = new String[cursor.getCount()];
        Log.i("TESTPROVIDER", String.valueOf(cursor.getCount()));
        Log.i("TESTPROVIDER", String.valueOf(cursor.getColumnCount()));
        cursor.moveToFirst();
        for (int i = 0; i < cursor.getCount(); i++){
            String line = "";
            for (int j = 0; j < cursor.getColumnCount(); j++) {
                line += cursor.getColumnName(j) + " : " + cursor.getString(j)+" ";
            }
            values[i] = line;
        }
        cursor.close();


        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, android.R.id.text1, values);

        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                String itemValue = (String) listView.getItemAtPosition(position);
                Toast.makeText(getApplicationContext(),
                        "Position :" + position + "  ListItem : " + itemValue, Toast.LENGTH_LONG)
                        .show();

            }

        });
    }

}
