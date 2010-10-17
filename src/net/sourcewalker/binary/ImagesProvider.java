package net.sourcewalker.binary;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.util.Log;

public class ImagesProvider extends ContentProvider {

    private static final String TAG = "ImagesProvider";
    private static final String AUTHORITY = "net.sourcewalker.binary";
    private static final String CONTENT_TYPE = "vnd.android.cursor.dir/net.sourcewalker.binary";
    private static final UriMatcher uriMatcher;
    private static final int NUM_IMAGES = 20;

    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
            + "/images");

    private static final int MATCH_LIST = 1;
    private static final int MATCH_ID = 2;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, "images", MATCH_LIST);
        uriMatcher.addURI(AUTHORITY, "images/#", MATCH_ID);
    }

    private int[] idArray;

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)) {
        case MATCH_LIST:
            return CONTENT_TYPE;
        default:
            throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean onCreate() {
        Random idRand = new Random();
        idArray = new int[NUM_IMAGES];
        for (int i = 0; i < NUM_IMAGES; i++) {
            idArray[i] = idRand.nextInt(10000);
        }
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {
        Log.d(TAG, "query(" + uri + ")");
        MatrixCursor cursor = new MatrixCursor(new String[] { "_id", "image",
                "_data" });
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        switch (uriMatcher.match(uri)) {
        case MATCH_LIST:
            for (int id : idArray) {
                addRow(cursor, id);
            }
            break;
        case MATCH_ID:
            long id = ContentUris.parseId(uri);
            addRow(cursor, id);
            break;
        default:
            throw new IllegalArgumentException("Unknown URI: " + uri);
        }
        return cursor;
    }

    private void addRow(MatrixCursor cursor, long id) {
        File imageFile = getImageFile(id);
        String imagePath;
        if (imageFile.exists()) {
            imagePath = imageFile.getAbsolutePath();
        } else {
            imagePath = getWaitingImage().getAbsolutePath();
            createImage(id);
        }
        cursor.addRow(new Object[] { new Long(id),
                ContentUris.withAppendedId(CONTENT_URI, id), imagePath });
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
            String[] selectionArgs) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public ParcelFileDescriptor openFile(Uri uri, String mode)
            throws FileNotFoundException {
        Log.d(TAG, "openFile(" + uri + ", " + mode + ")");
        return super.openFileHelper(uri, mode);
    }

    private File getWaitingImage() {
        File waitingImage = new File(getContext().getExternalFilesDir(null),
                "waiting.png");
        if (!waitingImage.exists()) {
            createWaitingImage(waitingImage);
        }
        return waitingImage;
    }

    private void createWaitingImage(File file) {
        Bitmap bitmap = Bitmap.createBitmap(96, 96, Bitmap.Config.ARGB_8888);
        try {
            FileOutputStream stream = new FileOutputStream(file);
            bitmap.compress(CompressFormat.PNG, 100, stream);
            stream.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void createImage(long id) {
        Intent createIntent = new Intent(getContext(), ImageCreateService.class);
        createIntent.setAction(Long.toString(id));
        getContext().startService(createIntent);
    }

    private File getImageFile(long id) {
        return new File(getContext().getExternalFilesDir(null), Long
                .toString(id)
                + ".png");
    }

}
