package pt.isel.gomes.beatbybit;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.io.File;

import pt.isel.gomes.beatbybit.util.Engine;


public class Activities extends Activity {
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activities);
        final Engine engine = Engine.getInstance();

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
                    File file = new File(engine.getRootDir() + "/" + itemValue);
                    Intent intent = new Intent();
                    intent.setAction(android.content.Intent.ACTION_VIEW);
                    intent.setDataAndType(Uri.fromFile(file), "text/plain");
                    startActivityForResult(intent, 10);

                }

            });
        }
    }

}
