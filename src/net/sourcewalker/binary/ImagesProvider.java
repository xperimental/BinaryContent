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

    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY
            + "/images");
    public static final Uri CONTENT_URI_IMAGE = Uri.parse("content://"
            + AUTHORITY + "/image");

    private static final int MATCH_LIST = 1;

    static {
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, "images", MATCH_LIST);
    }

    private Random idRand = new Random();

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
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
            String[] selectionArgs, String sortOrder) {
        Log.d(TAG, "query(" + uri + ")");
        switch (uriMatcher.match(uri)) {
        case MATCH_LIST:
            MatrixCursor cursor = new MatrixCursor(new String[] { "_id",
                    "image", "image_data" });
            for (int i = 0; i < 100; i++) {
                int id = idRand.nextInt(10000);
                cursor
                        .addRow(new Object[] {
                                new Integer(id),
                                ContentUris.withAppendedId(CONTENT_URI_IMAGE,
                                        id), "" });
            }
            cursor.setNotificationUri(getContext().getContentResolver(), uri);
            return cursor;
        default:
            throw new IllegalArgumentException("Unknown URI: " + uri);
        }
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
        long id = ContentUris.parseId(uri);
        File imageFile = getImageFile(id);
        ParcelFileDescriptor result;
        if (!imageFile.exists()) {
            createImage(id);
            result = ParcelFileDescriptor.open(getWaitingImage(),
                    ParcelFileDescriptor.MODE_READ_ONLY);
        } else {
            result = ParcelFileDescriptor.open(imageFile,
                    ParcelFileDescriptor.MODE_READ_ONLY);
        }
        return result;
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
