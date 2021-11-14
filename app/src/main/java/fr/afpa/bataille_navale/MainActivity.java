package fr.afpa.bataille_navale;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * Set intent to PlacementActivity
     */
    public void clickNewGame(View v) {
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