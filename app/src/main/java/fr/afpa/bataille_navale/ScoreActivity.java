package fr.afpa.bataille_navale;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class ScoreActivity extends AppCompatActivity {
    private ArrayList scores;
    private ArrayAdapter<String> arrayAdapter;
    private Intent svc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        svc = new Intent(this, BackgroundSoundService.class);
        startService(svc);

        ScoreMgr scoreMgr = new ScoreMgr(this);
        scores = scoreMgr.getAll(this);

        ListView list = findViewById(R.id.list);
        if(scores == null) {
            String noScore[] = {"Aucun score"};
            arrayAdapter = new ArrayAdapter<String>(this, R.layout.list, noScore);
        } else {
            arrayAdapter = new ArrayAdapter<String>(this, R.layout.list, scores);
        }

        list.setAdapter(arrayAdapter);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (svc != null) {
            stopService(svc);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (svc != null) {
            startService(svc);
        }
    }

    /**
     * Exit app
     */
    public void clickExit(View v) {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }
}