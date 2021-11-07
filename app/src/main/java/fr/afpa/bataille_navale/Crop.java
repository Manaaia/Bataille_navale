package fr.afpa.bataille_navale;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;

public class Crop {

    public static ArrayList<Bitmap> defineSplit(ImageView image) {
        int chunkNumbers = 0;

        switch (image.getId()) {
            case R.id.porte_avion:
                chunkNumbers = 6;
                break;

            case R.id.croiseur:
                chunkNumbers = 3;
                break;

            case R.id.contre_torpilleur1:
                chunkNumbers = 5;
                break;

            case R.id.contre_torpilleur2:
                chunkNumbers = 5;
                break;

            case R.id.torpilleur:
                chunkNumbers = 4;
                break;
        }

        //invoking method to split the source image
        return splitImage(image, chunkNumbers);
    }

    private static ArrayList<Bitmap> splitImage(ImageView image, int chunkNumbers) {

        //For the number of rows and columns of the grid to be displayed
        int rows,cols;

        //For height and width of the small image chunks
        int chunkHeight,chunkWidth;

        //To store all the small image chunks in bitmap format in this list
        ArrayList<Bitmap> chunkedImages = new ArrayList<Bitmap>(chunkNumbers);

        //Getting the scaled bitmap of the source image
        BitmapDrawable drawable = (BitmapDrawable) image.getDrawable();
        Bitmap bitmap = drawable.getBitmap();
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), true);
        rows = 1;
        cols = chunkNumbers;
        chunkHeight = bitmap.getHeight() / rows;
        chunkWidth = bitmap.getWidth() / cols;

        //xCoord and yCoord are the pixel positions of the image chunks
        int yCoord = 0;
        for(int x = 0; x < rows; x++) {
            int xCoord = 0;
            for(int y = 0; y < cols; y++) {
                chunkedImages.add(Bitmap.createBitmap(scaledBitmap, xCoord, yCoord, chunkWidth, chunkHeight));
                xCoord += chunkWidth;
            }
            yCoord += chunkHeight;
        }
        return chunkedImages;
    }
}
