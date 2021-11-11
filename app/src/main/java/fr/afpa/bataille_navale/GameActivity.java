package fr.afpa.bataille_navale;

import static java.lang.Math.abs;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

public class GameActivity extends AppCompatActivity {
    private GridLayout rivalLayout;
    private GridLayout myLayout;
    private BoatMgr boatMgr;
    private ArrayList<Boat> rivalFleet;
    private ArrayList<Boat> myFleet;
    private String turn;
    private AI rival;
    private ArrayList checkHits;
    private int counter;
    private View customToastRoot;
    private TextView msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        // Initialize Toast view for player turn information
        LinearLayout styledToast = findViewById(R.id.main_layout);
        LayoutInflater inflater = getLayoutInflater();
        customToastRoot = inflater.inflate(R.layout.toast, styledToast, false);
        msg = (TextView) customToastRoot.findViewById(R.id.txtToast);

        boatMgr = new BoatMgr(this);
        rival = new AI(0);

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
            linearLayout.setOnClickListener(myListener);
        }

        // Create rival fleet
        rivalFleet = createRivalFleet();
        for(int i = 0; i < rivalFleet.size(); i++) {
            Log.i("BT boat", String.valueOf(rivalFleet.get(i)));
        }

        // Place rival fleet
        placeFleet(rivalFleet, rivalLayout);

        // Create playerGrid
        myLayout = findViewById(R.id.MyGrid);
        Board myBoard = new Board();
        myBoard.createBoard(this,myLayout);

        // Get player fleet
        myFleet = boatMgr.getAll("player");
        for(int i = 0; i < myFleet.size(); i++) {
            Log.i("BT boat", String.valueOf(myFleet.get(i)));
        }

        // Place player fleet
        placeFleet(myFleet, myLayout);

        // Initialize turn, checks and counter
        turn = "player";
        msg.setText("A toi de commencer");
        msg.setVisibility(View.VISIBLE);
        Toast customToast = new Toast(getApplicationContext());
        customToast.setView(customToastRoot);
        customToast.setDuration(Toast.LENGTH_SHORT);
        customToast.setGravity(Gravity.TOP, 0, 0);
        customToast.show();

        checkHits = new ArrayList();
        counter = 0;
    }

    View.OnClickListener myListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (turn.equals("player")) {
                String tag = String.valueOf(v.getTag());
                LinearLayout linearLayout = (LinearLayout) v;
                ImageView imageView = (ImageView) linearLayout.getChildAt(0);

                fireHit(tag, imageView, rivalFleet, rivalLayout);

                v.setOnClickListener(null);

                turn = "rival";

                msg.setText("L'ennemi attaque !");
                Toast customToast = new Toast(getApplicationContext());
                customToast.setView(customToastRoot);
                customToast.setDuration(Toast.LENGTH_SHORT);
                customToast.setGravity(Gravity.TOP, 0, 0);
                customToast.show();

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        rivalAim(rival.getLevel());

                    }
                }, 2000);
            }
        }
    };

    public ArrayList<Boat> createRivalFleet() {
        ArrayList<Boat> rivalFleet = new ArrayList<Boat>();
        Boat rivalPorteAvion = new Boat("rival","porteAvion",6,6);
        Boat rivalCroiseur = new Boat("rival","croiseur",3,3);
        Boat rivalContreTorpilleur1 = new Boat("rival","1contreTorpilleur",5,5);
        Boat rivalContreTorpilleur2 = new Boat("rival","2contreTorpilleur",5,5);
        Boat rivalTorpilleur = new Boat("rival","torpilleur",4,4);
        rivalFleet.add(rivalPorteAvion);
        rivalFleet.add(rivalCroiseur);
        rivalFleet.add(rivalContreTorpilleur1);
        rivalFleet.add(rivalContreTorpilleur2);
        rivalFleet.add(rivalTorpilleur);

        setRandomPlacement(rivalFleet);
        return rivalFleet;
    }

    public void setRandomPlacement(ArrayList<Boat> fleet) {
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
                //Log.i("BT boat", String.valueOf(fleet.get(i).getName()));

                if (size % 2 == 0) {
                    midTile = (int) size / 2;
                } else {
                    midTile = (int) Math.ceil(size / 2);
                }

                // Set placement of each tile of boat according to orientation and midTile position
                for (int j = 0; j < size; j++) {
                    int newRow = 0;
                    int newCol = 0;
                    int diffOrder = midTile - j;
                    //Log.i("BT tile", String.valueOf("tile"+j));
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
                    //Log.i("BT checkedTag", tileTag);
                    if(newRow < 0 || newRow > 9 || newCol < 0 || newCol > 9) {
                        flag = false;
                        //Log.i("BT flag", String.valueOf(flag));
                        break;
                    } else if(checkValidityOfNewTilePosition(tileTag, fleet)) {
                        positions.add(tileTag);
                        flag = true;
                        //Log.i("BT flag", String.valueOf(flag));
                    } else {
                        flag = false;
                        //Log.i("BT flag", String.valueOf(flag));
                        break;
                    }
                }
            } while (!flag);

            // Add orientation and position to boat of fleet
            fleet.get(i).setOrientation(orientation);
            fleet.get(i).setPosition(positions);

        }
    }

    private void placeFleet(ArrayList<Boat> fleet, GridLayout layout) {
        final int childCount = layout.getChildCount();
        for(int i = 0; i < fleet.size(); i++) {
            for(int j = 0; j < fleet.get(i).getPosition().size(); j++) {
                for(int k = 0; k < childCount; k++) {
                    final LinearLayout ll = (LinearLayout) layout.getChildAt(k);
                    final String tagLl = String.valueOf(ll.getTag());
                    if(tagLl.equals(String.valueOf(fleet.get(i).getPosition().get(j)))) {
                        // TODO : set boat bitmaps on positions and orientation
                        ll.setBackgroundColor(Color.parseColor("#FFFFFF"));
                    }
                }
            }
        }
    }

    private boolean checkValidityOfNewTilePosition(String tag, ArrayList<Boat> fleet) {
        for(int i = 0; i < fleet.size(); i++) {
            ArrayList positions = fleet.get(i).getPosition();
            for(int j = 0; j < positions.size(); j++) {
                if(tag.equals(String.valueOf(positions.get(j)))) {
                    Log.i("BT flag", "false");
                    return false;
                }
            }
        }
        return true;
    }

    private void rivalAim(int level) {
        if(turn.equals("rival")) {
            String tileTag;

            if (level == 0) {
                do {
                    int row = ThreadLocalRandom.current().nextInt(0, Board.ROWS);
                    int col = ThreadLocalRandom.current().nextInt(0, Board.COLUMNS);
                    tileTag = "row" + row + "col" + col;
                } while (checkHits.contains(tileTag));

                checkHits.add(tileTag);

                LinearLayout linearLayout = (LinearLayout) getViewByTag(tileTag);
                ImageView imageView = (ImageView) linearLayout.getChildAt(0);

                fireHit(tileTag, imageView, myFleet, myLayout);

            }
        }
        turn = "player";
        msg.setText("A ton tour !");
        Toast customToast = new Toast(getApplicationContext());
        customToast.setView(customToastRoot);
        customToast.setDuration(Toast.LENGTH_SHORT);
        customToast.setGravity(Gravity.TOP, 0, 0);
        customToast.show();
    }

    private View getViewByTag(String tag) {
        LinearLayout view = null;
        final int childCount = myLayout.getChildCount();
        for (int i = 0; i < childCount; i++) {
            view = (LinearLayout) myLayout.getChildAt(i);
            final String tagObj = String.valueOf(view.getTag());
            if (tagObj.equals(tag)) {
                break;
            }
        }
        return view;
    }

    private void fireHit(String tag, ImageView imageView, ArrayList<Boat> fleet, GridLayout layout) {
        int idImage = R.drawable.missed;

        for (int i = 0; i < fleet.size(); i++) {
            for (int j = 0; j < fleet.get(i).getPosition().size(); j++) {

                if (tag.equals(String.valueOf(fleet.get(i).getPosition().get(j)))) {
                    //if view is boat set background image to explosion and modify boat state and life
                    idImage = R.drawable.explosion;
                    int life = fleet.get(i).getLife();
                    life--;
                    //Log.i("BT life", String.valueOf(life));
                    if (life != 0) {
                        fleet.get(i).setLife(life);
                        fleet.get(i).setState(1);
                    } else {
                        fleet.get(i).setState(2);
                        final int childCount = layout.getChildCount();
                        for (int k = 0; k < fleet.get(i).getPosition().size(); k++) {
                            for (int l = 0; l < childCount; l++) {
                                final LinearLayout ll = (LinearLayout) layout.getChildAt(l);
                                final String tagLl = String.valueOf(ll.getTag());
                                if (tagLl.equals(String.valueOf(fleet.get(i).getPosition().get(k)))) {
                                    ll.setBackgroundColor(Color.parseColor("#FF0000"));
                                }
                            }
                        }
                        checkForEndGame(fleet);
                    }
                }
            }
        }
        imageView.setImageResource(idImage);
    }

    private void checkForEndGame(ArrayList<Boat> fleet) {
        boolean flag = true;
        String team = fleet.get(0).getTeam();

        for(int i = 0; i < fleet.size(); i++) {
            int state = fleet.get(i).getState();
            if(state != 2) {
                flag = false;
            }
        }
        if (flag) {
            endGame(team);
        }
    }

    private void endGame(String losingTeam) {
        AlertDialog.Builder builder= new AlertDialog.Builder(this);
        if(losingTeam.equals("rival")) {
            // alerte Vous avez gagné
            builder.setMessage("Vous avez gagné !");
        } else {
            // alerte vous avez perdu
            builder.setMessage("Vous avez perdu...");
        }
        // alerte Recommencer ? Quitter ?
        builder.setCancelable(false);

        builder.setNegativeButton("Recommencer", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(GameActivity.this, PlacementActivity.class);
                startActivity(intent);
                finish();
            }
        });

        builder.setPositiveButton("Quitter", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                GameActivity.this.finish();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
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