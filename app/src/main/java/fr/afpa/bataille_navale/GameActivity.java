package fr.afpa.bataille_navale;

import static java.lang.Math.abs;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
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
    private GridLayout rivalLayout;
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
    private ArrayList<Boat> rivalFleet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        boatMgr = new BoatMgr(this);

        /**
         * Create the boards and place boats
         */
        // Create adverseGrid
        rivalLayout = findViewById(R.id.AdverseGrid);
        Board adverseBoard = new Board();
        adverseBoard.createBoard(this,rivalLayout);

        // Add onClick listener
        final int childCount = rivalLayout.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final LinearLayout linearLayout = (LinearLayout) rivalLayout.getChildAt(i);
            //final View imageview = linearLayout.getChildAt(0);
            linearLayout.setOnClickListener(myListener);
        }

        // Create rival fleet
        rivalFleet = createRivalFleet();
        for(int i = 0; i < rivalFleet.size(); i++) {
            Log.i("boat", String.valueOf(rivalFleet.get(i)));
        }

        // Place rival fleet
        placeFleet(rivalFleet, rivalLayout);

        // Create playerGrid
        myLayout = findViewById(R.id.MyGrid);
        Board myBoard = new Board();
        myBoard.createBoard(this,myLayout);

        // Get player fleet
        ArrayList<Boat> myFleet = boatMgr.getAll("player");
        for(int i = 0; i < myFleet.size(); i++) {
            Log.i("boat", String.valueOf(myFleet.get(i)));
        }

        // Place player fleet
        placeFleet(myFleet, myLayout);
    }

    View.OnClickListener myListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String tag = String.valueOf(v.getTag());
            LinearLayout linearLayout = (LinearLayout) v;
            ImageView imageView = (ImageView)linearLayout.getChildAt(0);
            int idImage = R.drawable.missed;

            for(int i = 0; i < rivalFleet.size(); i++) {
                for(int j = 0; j < rivalFleet.get(i).getPosition().size(); j++) {

                    if(tag.equals(String.valueOf(rivalFleet.get(i).getPosition().get(j)))) {
                        //if view is boat set background image to explosion and modify boat state
                        idImage = R.drawable.explosion;
                        int life = rivalFleet.get(i).getLife();
                        life--;
                        if(life != 0) {
                            rivalFleet.get(i).setLife(life);
                        } else {
                            final int childCount = rivalLayout.getChildCount();
                            for(int k = 0; k < rivalFleet.get(i).getPosition().size(); k++) {
                                for(int l = 0; l < childCount; l++) {
                                    final LinearLayout ll = (LinearLayout) rivalLayout.getChildAt(l);
                                    final String tagLl = String.valueOf(ll.getTag());
                                    if(tagLl.equals(String.valueOf(rivalFleet.get(i).getPosition().get(k)))) {
                                        ll.setBackgroundColor(Color.parseColor("#FF0000"));
                                    }
                                }
                            }
                            // TODO : check for endgame
                        }
                    }
                }
            }
            imageView.setImageResource(idImage);
        }
    };

    public ArrayList<Boat> createRivalFleet() {
        ArrayList<Boat> rivalFleet = new ArrayList<Boat>();
        rivalPorteAvion = new Boat("rival","porteAvion",6,6);
        rivalCroiseur = new Boat("rival","croiseur",3,3);
        rivalContreTorpilleur1 = new Boat("rival","1contreTorpilleur",5,5);
        rivalContreTorpilleur2 = new Boat("rival","2contreTorpilleur",5,5);
        rivalTorpilleur = new Boat("rival","torpilleur",4,4);
        rivalFleet.add(rivalPorteAvion);
        rivalFleet.add(rivalCroiseur);
        rivalFleet.add(rivalContreTorpilleur1);
        rivalFleet.add(rivalContreTorpilleur2);
        rivalFleet.add(rivalTorpilleur);

        setRandomPlacement(rivalFleet);
        return rivalFleet;
    }

    public ArrayList<Boat> setRandomPlacement(ArrayList<Boat> fleet) {
        // TODO : bug, boat over boat
        // Get possible orientations
        ArrayList<String> orientations = new ArrayList<>();
        orientations.add("horizontal_left");
        orientations.add("vertical_top");
        orientations.add("horizontal_right");
        orientations.add("vertical_down");

        // Initialize variables
        int midTile;
        String tileTag = null;
        String orientation;
        ArrayList positions = new ArrayList();
        boolean flag = false;

        // Loop get each boat of fleet
        for(int i = 0; i < fleet.size(); i++) {
            do {
                // clear array of positions
                positions = new ArrayList();

                // Get random orientation
                Random random = new Random();
                int index = random.nextInt(orientations.size());
                orientation = orientations.get(index);

                // Get random midTile and its position on Grid
                int row = ThreadLocalRandom.current().nextInt(0, Board.ROWS);
                int col = ThreadLocalRandom.current().nextInt(0, Board.COLUMNS);
                int size = fleet.get(i).getSize();

                if (size % 2 == 0) {
                    midTile = (int) size / 2;
                } else {
                    midTile = (int) Math.ceil(size / 2);
                }

                // Get placement of each tile of boat according to orientation and midTile position
                for (int j = 0; j < size; j++) {
                    int newRow = 0;
                    int newCol = 0;
                    int diffOrder = midTile - j;

                    if (diffOrder > 0) {
                        if (orientation == "horizontal_left") {
                            newRow = row;
                            newCol = col - abs(diffOrder);
                        } else if (orientation == "vertical_top") {
                            newRow = row - abs(diffOrder);
                            newCol = col;
                        } else if (orientation == "horizontal_right") {
                            newRow = row;
                            newCol = col + abs(diffOrder);
                        } else if (orientation == "vertical_down") {
                            newRow = row + abs(diffOrder);
                            newCol = col;
                        }
                    } else if (diffOrder == 0) {
                        newRow = row;
                        newCol = col;
                    } else if (diffOrder < 0) {
                        if (orientation == "horizontal_left") {
                            newRow = row;
                            newCol = col + abs(diffOrder);
                        } else if (orientation == "vertical_top") {
                            newRow = row + abs(diffOrder);
                            newCol = col;
                        } else if (orientation == "horizontal_right") {
                            newRow = row;
                            newCol = col - abs(diffOrder);
                        } else if (orientation == "vertical_down") {
                            newRow = row - abs(diffOrder);
                            newCol = col;
                        }
                    }

                    // Check validity of tile position
                    tileTag = "row" + newRow + "col" + newCol;
                    if(newRow < 0 || newRow > 9 || newCol < 0 || newRow > 9) {
                        flag = false;
                    } else if(checkValidityOfNewTilePosition(tileTag, fleet)) {
                        positions.add(tileTag);
                        flag = true;
                    } else {
                        flag = false;
                    }
                }
            } while (!flag);

            // Add orientation and position to boat of fleet
            fleet.get(i).setOrientation(orientation);
            fleet.get(i).setPosition(positions);

        }
        return fleet;
    }

    private void placeFleet(ArrayList<Boat> fleet, GridLayout layout) {
        final int childCount = layout.getChildCount();
        for(int i = 0; i < fleet.size(); i++) {
            for(int j = 0; j < fleet.get(i).getPosition().size(); j++) {
                for(int k = 0; k < childCount; k++) {
                    final LinearLayout ll = (LinearLayout) layout.getChildAt(k);
                    final String tagLl = String.valueOf(ll.getTag());
                    if(tagLl.equals(String.valueOf(fleet.get(i).getPosition().get(j)))) {
                        ll.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    }
                }
            }
        }
    }

    private boolean checkValidityOfNewTilePosition(String tag, ArrayList<Boat> fleet) {
        //Log.i("checkNewTilePosition", "true");
        //Log.i("Fleet size", String.valueOf(fleet.size()));
        for(int i = 0; i < fleet.size(); i++) {
            //Log.i("Boat", String.valueOf(fleet.get(i)));
            ArrayList positions = fleet.get(i).getPosition();
            for(int j = 0; j < positions.size(); j++) {
                //Log.i("newTag", tag);
                //Log.i("existing tag", String.valueOf(positions.get(j)));
                if(tag == String.valueOf(positions.get(j))) {
                    return false;
                }
            }
        }
        return true;
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