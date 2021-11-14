package fr.afpa.bataille_navale;

import static java.lang.Math.abs;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
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

        boatMgr = new BoatMgr(this);
        rival = new AI(0);


        //Initialize toast view for player information
        LinearLayout styledToast = findViewById(R.id.main_layout);
        LayoutInflater inflater = getLayoutInflater();
        customToastRoot = inflater.inflate(R.layout.toast, styledToast, false);
        msg = (TextView) customToastRoot.findViewById(R.id.txtToast);


        //Create the boards and place boats
        // Create adverseGrid
        rivalLayout = findViewById(R.id.AdverseGrid);
        Board adverseBoard = new Board();
        adverseBoard.createGameBoard(this,rivalLayout);

        // Add onClick listener
        final int childCount = rivalLayout.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final LinearLayout linearLayout = (LinearLayout) rivalLayout.getChildAt(i);
            final FrameLayout frameLayout = (FrameLayout) linearLayout.getChildAt(0);
            final ImageView imageView = (ImageView) frameLayout.getChildAt(0);
            imageView.setVisibility(View.INVISIBLE);
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
        myBoard.createGameBoard(this,myLayout);

        // Get player fleet
        myFleet = boatMgr.getAll("player");
        for(int i = 0; i < myFleet.size(); i++) {
            Log.i("BT boat", String.valueOf(myFleet.get(i)));
        }

        // Place player fleet
        placeFleet(myFleet, myLayout);

        // Initialize turn, checks and counter
        turn = "player";
        checkHits = new ArrayList();
        counter = 0;

        // Start game toast information
        msg.setText(R.string.tStartTurn);
        msg.setVisibility(View.VISIBLE);
        Toast customToast = new Toast(getApplicationContext());
        customToast.setView(customToastRoot);
        customToast.setDuration(Toast.LENGTH_SHORT);
        customToast.setGravity(Gravity.TOP, 0, 0);
        customToast.show();
    }

    /**
     * onClick calls action fireHit for player
     * calls toast for player information
     * change turn to "rival"
     * calls action rivalAim for AI
     */
    View.OnClickListener myListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (turn.equals("player")) {
                String tag = String.valueOf(v.getTag());
                LinearLayout linearLayout = (LinearLayout) v;
                FrameLayout frameLayout = (FrameLayout) linearLayout.getChildAt(0);
                ImageView imageView = (ImageView) frameLayout.getChildAt(1);

                String txt = fireHit(tag, imageView, rivalFleet, rivalLayout);
                String team = getResources().getString(R.string.player);
                msg.setText(team + " : " + txt);
                Toast customToast = new Toast(getApplicationContext());
                customToast.setView(customToastRoot);
                customToast.setDuration(Toast.LENGTH_SHORT);
                customToast.setGravity(Gravity.TOP, 0, 0);
                customToast.show();

                /*Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        msg.setText("L'ennemi attaque !");
                        Toast customToastTurn = new Toast(getApplicationContext());
                        customToastTurn.setView(customToastRoot);
                        customToastTurn.setDuration(Toast.LENGTH_SHORT);
                        customToastTurn.setGravity(Gravity.TOP, 0, 0);
                        customToastTurn.show();

                    }
                }, 1000);*/

                v.setOnClickListener(null);

                turn = "rival";

                Handler handler1 = new Handler();
                handler1.postDelayed(new Runnable() {
                    @Override
                    public void run() {

                        rivalAim(rival.getLevel());

                    }
                }, 1000);
            }
        }
    };

    /**
     * Create the AI fleet and calls setRandomPlacement
     */
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

    /**
     * Randomly orientate and set boat's positions on grid
     */
    public void setRandomPlacement(ArrayList<Boat> fleet) {
        // Get possible orientations
        ArrayList<String> orientations = new ArrayList<>();
        orientations.add("horizontal");
        orientations.add("vertical");

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

                // Get random position on Grid
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
                        if (orientation == "horizontal") {
                            newRow = row;
                            newCol = col - abs(diffOrder);
                        } else if (orientation == "vertical") {
                            newRow = row - abs(diffOrder);
                            newCol = col;
                        }
                    } else if (diffOrder == 0) {
                        newRow = row;
                        newCol = col;
                    } else if (diffOrder < 0) {
                        if (orientation == "horizontal") {
                            newRow = row;
                            newCol = col + abs(diffOrder);
                        } else if (orientation == "vertical") {
                            newRow = row + abs(diffOrder);
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

    /**
     * Get images of boats
     * Crop images into bitmaps
     * Place bitmaps on their position on grid
     * Orientate bitmap
     */
    private void placeFleet(ArrayList<Boat> fleet, GridLayout layout) {
        final int childCount = layout.getChildCount();
        int idImage = 0;
        int rotationAngleDegree = 0;
        int main = 0;
        String toChangeTag = null;
        int id = 0;
        String endTag = null;
        int neighbourTileI = 0;

        for(int i = 0; i < fleet.size(); i++) {
            ArrayList positions = fleet.get(i).getPosition();

            String name = fleet.get(i).getName();
            if(name.equals("porteAvion")) {
                idImage = R.drawable.t_porte_avion;
                id = R.id.porte_avion;
            } else if(name.equals("croiseur")) {
                idImage = R.drawable.t_croiseur;
                id = R.id.croiseur;
            } else if(name.equals("1contreTorpilleur")) {
                idImage = R.drawable.t_contre_torpilleur;
                id = R.id.contre_torpilleur1;
            } else if(name.equals("2contreTorpilleur")) {
                idImage = R.drawable.t_contre_torpilleur;
                id = R.id.contre_torpilleur2;
            } else if(name.equals("torpilleur")) {
                idImage = R.drawable.t_torpilleur;
                id = R.id.torpilleur;
            }

            String orientation = fleet.get(i).getOrientation();
            if(orientation.equals("horizontal")) {
                rotationAngleDegree = 0;
            } else if(orientation.equals("vertical")) {
                rotationAngleDegree = 90;
            }

            for(int j = 0; j < childCount; j++) {
                final LinearLayout ll = (LinearLayout) layout.getChildAt(j);
                final String tagLl = String.valueOf(ll.getTag());

                if(tagLl.equals(String.valueOf(positions.get(0)))) {
                    ll.setBackgroundColor(Color.parseColor("#FFFFFF"));

                    FrameLayout frameLayout = (FrameLayout) ll.getChildAt(0);
                    ImageView imageView = (ImageView) frameLayout.getChildAt(0);
                    imageView.setImageResource(idImage);
                    imageView.setId(id);

                    /**
                     * Split image in several images according to number of spans
                     */
                    ArrayList<Bitmap> chunkedImages = Crop.defineSplit(imageView);

                    /**
                     * Get My grid cases corresponding to size of chunkedImages list for display
                     * Copy bitmap data into target image data
                     */
                    Log.i("id dropped : ", tagLl);
                    char colI = tagLl.charAt(7);
                    char rowI = tagLl.charAt(3);
                    if(orientation.contains("horizontal")) {
                        main = Character.getNumericValue(colI);
                        toChangeTag = tagLl.substring(0, 7);
                        endTag = "";
                    } else {
                        main = Character.getNumericValue(rowI);
                        toChangeTag = tagLl.substring(0, 3);
                        endTag = tagLl.substring(4);
                    }
                    Log.i("main : ", String.valueOf(main));

                    for (int k = 0; k < chunkedImages.size(); k++) {
                        if(orientation.equals("vertical_down") || orientation.equals("horizontal_right")) {
                            neighbourTileI = main - k;
                        } else {
                            neighbourTileI = main + k;
                        }
                        Log.i("id dropped col-i : ", String.valueOf(neighbourTileI));
                        String sNeighbourTileI = String.valueOf(neighbourTileI);
                        String tagNeighbour = toChangeTag + sNeighbourTileI + endTag;
                        Log.i("id tag previous tile : ", tagNeighbour);
                        LinearLayout neighbourContainer = layout.findViewWithTag(tagNeighbour);
                        neighbourContainer.setBackgroundResource(0);
                        neighbourContainer.setBackgroundResource(R.drawable.grid_stroke);
                        FrameLayout frameLayout1 = (FrameLayout) neighbourContainer.getChildAt(0);
                        ImageView neighbourView = (ImageView) frameLayout1.getChildAt(0);
                        neighbourView.setImageBitmap(chunkedImages.get(k));
                        neighbourView.setImageBitmap(Rotate.rotateBitmap((Bitmap) chunkedImages.get(k), rotationAngleDegree));
                        Log.i("degree ", String.valueOf(rotationAngleDegree));
                    }
                    break;
                }
            }
        }
    }

    /**
     * Check that new proposed position is valid
     */
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

    /**
     * Get hit position from AI
     * calls fireHit
     */
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
                FrameLayout frameLayout = (FrameLayout) linearLayout.getChildAt(0);
                ImageView imageView = (ImageView) frameLayout.getChildAt(1);

                final String tag = tileTag;

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        String txt = fireHit(tag, imageView, myFleet, myLayout);
                        String team = getResources().getString(R.string.rival);
                        msg.setText(team + " : " + txt);
                        Toast customToast = new Toast(getApplicationContext());
                        customToast.setView(customToastRoot);
                        customToast.setDuration(Toast.LENGTH_SHORT);
                        customToast.setGravity(Gravity.TOP, 0, 0);
                        customToast.show();
                                        }
                }, 1000);

            }
        }
        turn = "player";
        /*Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {

                msg.setText("A ton tour !");
                Toast customToastTurn = new Toast(getApplicationContext());
                customToastTurn.setView(customToastRoot);
                customToastTurn.setDuration(Toast.LENGTH_SHORT);
                customToastTurn.setGravity(Gravity.TOP, 0, 0);
                customToastTurn.show();

            }
        }, 3000);*/
    }

    /**
     * Get View by its tag
     */
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

    /**
     * Check if targeted position contains boat
     * Return if missed, touched or sunk
     * Update accordingly state and life of boat
     * Add missed or hit image to imageView
     * Change background and set visibility of rival boat visible if sunk
     * Check for endGame
     * If endGame, calls endGame
     */
    private String fireHit(String tag, ImageView imageView, ArrayList<Boat> fleet, GridLayout layout) {
        int idImage = R.drawable.missed;
        String txt = null;

        outerloop:
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
                        txt = "Touché !";
                    } else {
                        fleet.get(i).setState(2);
                        txt = "Coulé !";
                        final int childCount = layout.getChildCount();
                        for (int k = 0; k < fleet.get(i).getPosition().size(); k++) {
                            for (int l = 0; l < childCount; l++) {
                                final LinearLayout ll = (LinearLayout) layout.getChildAt(l);
                                final String tagLl = String.valueOf(ll.getTag());
                                if (tagLl.equals(String.valueOf(fleet.get(i).getPosition().get(k)))) {
                                    ll.setBackgroundColor(Color.parseColor("#000000"));
                                    if(fleet.get(i).getTeam().equals("rival")) {
                                        FrameLayout frameLayout = (FrameLayout) ll.getChildAt(0);
                                        ImageView imageView1 = (ImageView) frameLayout.getChildAt(0);
                                        imageView1.setVisibility(View.VISIBLE);
                                    }
                                }
                            }
                        }
                        checkForEndGame(fleet);
                    }
                    break outerloop;
                } else {
                    txt = "Raté !";
                }
            }
        }

        imageView.setImageResource(idImage);
        return txt;
    }

    /**
     * Check if all boat of team are sunk
     */
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

    /**
     * Calls alert dialog that indicates if won or lost and offers to start new game or exit
     */
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
    public void clickExit(View v) {
        Intent i = new Intent(this, MainActivity.class);
        startActivity(i);
        finish();
    }
}