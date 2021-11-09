package fr.afpa.bataille_navale;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class GameActivity extends AppCompatActivity {
    private GridLayout adverseLayout;
    private GridLayout myLayout;
    private BoatMgr boatMgr;
    private Boat playerTorpilleur;
    private Boat playerPorteAvion;
    private Boat playerCroiseur;
    private Boat playerContreTorpilleur1;
    private Boat playerContreTorpilleur2;
    private Boat rivalTorpilleur;
    private Boat rivalPorteAvion;
    private Boat rivalCroiseur;
    private Boat rivalContreTorpilleur1;
    private Boat rivalContreTorpilleur2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        boatMgr = new BoatMgr(this);

        /**
         * Create the boards and place boats
         */
        // create adverseGrid
        adverseLayout = findViewById(R.id.AdverseGrid);
        Board adverseBoard = new Board();
        adverseBoard.createBoard(this,adverseLayout);
        final int childCount = adverseLayout.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final LinearLayout linearLayout = (LinearLayout) adverseLayout.getChildAt(i);
            final View imageview = linearLayout.getChildAt(0);
            imageview.setOnClickListener(myListener);
        }

        // get boats
        ArrayList<Boat> rivalFleet = new ArrayList<Boat>();
        rivalPorteAvion = new Boat("rival","porteAvion",6);
        rivalCroiseur = new Boat("rival","croiseur",3);
        rivalContreTorpilleur1 = new Boat("rival","1contreTorpilleur",5);
        rivalContreTorpilleur2 = new Boat("rival","2contreTorpilleur",5);
        rivalTorpilleur = new Boat("rival","torpilleur",4);
        rivalFleet.add(rivalPorteAvion);
        rivalFleet.add(rivalCroiseur);
        rivalFleet.add(rivalContreTorpilleur1);
        rivalFleet.add(rivalContreTorpilleur2);
        rivalFleet.add(rivalTorpilleur);

        setRandomPlacement(rivalFleet);

        // place boats

        // create playerGrid
        myLayout = findViewById(R.id.MyGrid);
        Board myBoard = new Board();
        myBoard.createBoard(this,myLayout);

        // get boats
        ArrayList<Boat> listOfBoats = boatMgr.getAll("player");
        for(int i = 0; i < listOfBoats.size(); i++) {
            if(listOfBoats.get(i).getName() == "porteAvion") {
                playerPorteAvion = listOfBoats.get(i);
            } else if(listOfBoats.get(i).getName() == "croiseur") {
                playerCroiseur = listOfBoats.get(i);
            } else if(listOfBoats.get(i).getName() == "torpilleur") {
                playerTorpilleur = listOfBoats.get(i);
            } else if(listOfBoats.get(i).getName() == "1contreTorpilleur") {
                playerContreTorpilleur1 = listOfBoats.get(i);
            } else if(listOfBoats.get(i).getName() == "2contreTorpilleur") {
                playerContreTorpilleur2 = listOfBoats.get(i);
            }
        }
        // place boats



    }

    View.OnClickListener myListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // TODO : CREATE ONCLICK FUNCTION
            //if view is empty set background image to miss
            //else if view is boat set background image to explosion and modify boat state
        }
    };

    public void setRandomPlacement(ArrayList<Boat> fleet) {
        // TODO : CREATE RANDOM PLACEMENT OF BOATS ON GRID
        ArrayList<String> orientations = new ArrayList<String>();
        orientations.add("horizontal");
        orientations.add("vertical");
        int midTile;
        for(int i = 0; i < fleet.size(); i++) {
            ArrayList pos = new ArrayList();
            Random random = new Random();
            int index = random.nextInt(orientations.size());
            String orientation = orientations.get(index);
            int x = ThreadLocalRandom.current().nextInt(0, Board.ROWS);
            int y = ThreadLocalRandom.current().nextInt(0, Board.COLUMNS);
            int size = fleet.get(i).getSize();
            String tileTag = "row" + x + "col" + y;
            if (size % 2 == 0) {
                midTile = (int) size / 2;
            } else {
                midTile = (int) Math.ceil(size / 2);
            }
        }


    }

    /**
     * Set intent to go back to MainActivity
     */
    public void clickAbandon(View v) {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }
}