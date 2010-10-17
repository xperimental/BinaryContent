package net.sourcewalker.binary;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap.CompressFormat;
import android.util.Log;

public class ImageCreateService extends IntentService {

    private static final String TAG = "ImageCreateService";

    private AtomicInteger queued = new AtomicInteger(0);

    public ImageCreateService() {
        super("ImageCreateService");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        queued.incrementAndGet();
        Log.d(TAG, "queueing image: " + intent.getAction() + " at "
                + queued.get());
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        long id = Long.parseLong(intent.getAction());
        File outputFile = new File(getExternalFilesDir(null), id + ".png");
        if (!outputFile.exists()) {
            RandomImage image = new RandomImage(id);
            try {
                FileOutputStream outputStream = new FileOutputStream(outputFile);
                image.getBitmap().compress(CompressFormat.PNG, 100,
                        outputStream);
                outputStream.close();
            } catch (FileNotFoundException e) {
                Log.e(TAG, e.getMessage());
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }
        }
        int left = queued.decrementAndGet();
        Log.d(TAG, "finished image " + intent.getAction() + ". left: " + left);
        if ((left % 5) == 0) {
            Log.d(TAG, "  notifying listeners...");
            getContentResolver().notifyChange(ImagesProvider.CONTENT_URI, null);
        }
    }

}
