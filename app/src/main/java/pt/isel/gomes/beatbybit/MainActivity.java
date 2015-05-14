package pt.isel.gomes.beatbybit;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import pt.isel.gomes.beatbybit.util.Engine;


public class MainActivity extends Activity {
    private Engine engine;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        engine = new Engine();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public void launchRec(View view){
        Intent intent = new Intent(this, Rec.class);
        intent.putExtra("engine",engine);
        startActivity(intent);
    }
    public void launchCooper(View view){
        Intent intent = new Intent(this, Cooper.class);
        startActivity(intent);
    }
    public void launchAct(View view){
        Intent intent = new Intent(this, Activities.class);
        startActivity(intent);
    }
    public void launchSettings(View view){
        Intent intent = new Intent(this, Settings.class);
        startActivity(intent);
    }
}
