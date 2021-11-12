package fr.afpa.bataille_navale;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;

public class Rotate {
    /**
     * Do a 90° rotation of tile
     */
    public static Bitmap rotateBitmap(Bitmap bitmap, int rotationAngleDegree) {

        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int newW = w, newH = h;
        if (rotationAngleDegree == 90 || rotationAngleDegree == 270) {
            newW = h;
            newH = w;
        }
        Bitmap rotatedBitmap = Bitmap.createBitmap(newW, newH, bitmap.getConfig());
        Canvas canvas = new Canvas(rotatedBitmap);

        Rect rect = new Rect(0, 0, newW, newH);
        Matrix matrix = new Matrix();
        float px = rect.exactCenterX();
        float py = rect.exactCenterY();
        matrix.postTranslate(-bitmap.getWidth() / 2, -bitmap.getHeight() / 2);
        matrix.postRotate(rotationAngleDegree);
        matrix.postTranslate(px, py);
        canvas.drawBitmap(bitmap, matrix, new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG | Paint.FILTER_BITMAP_FLAG));
        matrix.reset();

        return rotatedBitmap;
    }
}
