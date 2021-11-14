package fr.afpa.bataille_navale;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class ScoreActivity extends AppCompatActivity {
    private ArrayList scores;
    private ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score);

        ScoreMgr scoreMgr = new ScoreMgr(this);
        scores = scoreMgr.getAll();

        ListView list = findViewById(R.id.list);
        if(scores == null) {
            String noScore[] = {"Aucun score"};
            arrayAdapter = new ArrayAdapter<String>(this, R.layout.list, noScore);
        } else {
            arrayAdapter = new ArrayAdapter<String>(this, R.layout.list, scores);
        }

        list.setAdapter(arrayAdapter);
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