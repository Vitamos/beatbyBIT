package pt.isel.gomes.beatbybit;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import pt.isel.gomes.beatbybit.util.Engine;


public class Activities extends Activity {
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activities);
        Engine engine = (Engine) getIntent().getSerializableExtra("engine");

        listView = (ListView) findViewById(R.id.list);

        String[] values = engine.getFiles();
        if (values != null && values.length > 0) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_list_item_1, android.R.id.text1, values);

            listView.setAdapter(adapter);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {

                    String itemValue = (String) listView.getItemAtPosition(position);
                    Toast.makeText(getApplicationContext(),
                            "  ListItem : " + itemValue, Toast.LENGTH_LONG)
                            .show();

                }

            });
        }
    }

}
