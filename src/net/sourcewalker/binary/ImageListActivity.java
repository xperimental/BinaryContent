package net.sourcewalker.binary;

import android.app.ListActivity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.SimpleCursorAdapter;

public class ImageListActivity extends ListActivity {

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Cursor cursor = managedQuery(ImagesProvider.CONTENT_URI, null, null,
                null, null);
        String[] columns = new String[] { "_id", "image" };
        int[] views = new int[] { R.id.list_text, R.id.list_image };
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this,
                R.layout.list_row, cursor, columns, views);
        setListAdapter(adapter);
    }
}
