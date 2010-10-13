package net.sourcewalker.binary;

import java.nio.ByteBuffer;
import java.util.Random;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;

public class RandomImage {

    private static final int DEFAULT_WIDTH = 96;
    private static final int DEFAULT_HEIGHT = 96;
    private static final int PIXEL_SIZE = 4;

    private Random rnd;
    private int width;
    private int height;
    private Bitmap bitmap;

    public RandomImage(int width, int height, long seed) {
        this.rnd = new Random(seed);
        this.width = width;
        this.height = height;

        createImage();
    }

    public RandomImage(long seed) {
        this(DEFAULT_WIDTH, DEFAULT_HEIGHT, seed);
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    private void createImage() {
        int pixels = width * height;
        ByteBuffer buffer = ByteBuffer.allocate(pixels * PIXEL_SIZE);
        for (int i = 0; i < pixels; i++) {
            buffer.put((byte) rnd.nextInt(255));
            buffer.put((byte) rnd.nextInt(255));
            buffer.put((byte) rnd.nextInt(255));
            buffer.put((byte) 255);
        }
        buffer.rewind();
        bitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
        bitmap.copyPixelsFromBuffer(buffer);
    }

}
