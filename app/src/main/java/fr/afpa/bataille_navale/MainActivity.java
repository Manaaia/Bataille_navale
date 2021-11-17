package fr.afpa.bataille_navale;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

public class MainActivity extends AppCompatActivity {
    private SharedPreferences preferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        MediaPlayer mediaPlayer = MediaPlayer.create(MainActivity.this,R.raw.wave2);
        mediaPlayer.setLooping(true);
        mediaPlayer.start();

        preferences = getApplicationContext().getSharedPreferences("DiffPref", 0); // 0 - for private mode

        //get the spinner from the xml.
        Spinner spinner = findViewById(R.id.spinner);
        //create a list of items for the spinner.
        String[] items = new String[]{getString(R.string.beginner), getString(R.string.easy)};
        //create an adapter to describe how the items are displayed, adapters are used in several places in android.
        //There are multiple variations of this, but this is the basic variant.
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        //set the spinners adapter to the previously created one.
        spinner.setAdapter(adapter);

        if(!preferences.contains("level")) {
            spinner.setSelection(0);
        } else {
            spinner.setSelection(preferences.getInt("level",0));
        }
    }

    /**
     * Set intent to PlacementActivity
     */
    public void clickNewGame(View v) {
        Spinner difficulty = findViewById(R.id.spinner);
        String chosenLevel = difficulty.getSelectedItem().toString();
        int level = 0;
        if(chosenLevel.equals(getString(R.string.beginner))) {
            level = 0;
        } else if(chosenLevel.equals(getString(R.string.easy))) {
            level = 1;
        }

        SharedPreferences.Editor editor = preferences.edit();

        if(!preferences.contains("level")) {
            editor.putInt("level", 0); // Storing integer
        } else {
            editor.putInt("level", level); // Storing integer
        }
        editor.commit();

        Intent intent = new Intent(this, PlacementActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Set intent to ScoreActivity
     */
    public void clickScore(View v) {
        Intent intent = new Intent(this, ScoreActivity.class);
        startActivity(intent);
        finish();
    }



    /**
     * Exit app
     */
    public void clickExit(View v) {
        finish();
        System.exit(0);
    }
}