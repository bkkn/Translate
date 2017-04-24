package me.bkkn.translate.ui.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import me.bkkn.translate.data.DB;
import me.bkkn.translate.R;

public class FavoritesFragment extends Fragment {

    public FavoritesFragment() {
        // Required empty public constructor
    }

    private ListView mlistView;
    private DB mDb;
    private SimpleCursorAdapter mAdapter;
    private Cursor mCursor;
    private static final int CM_DELETE_ID = 1;
    private TextView mHeader;
    private ImageView mImageView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup viewGroup,
                             Bundle savedInstanceState) {
        View histView = inflater.inflate(R.layout.fragment_list, viewGroup, false);

        mHeader = (TextView) histView.findViewById(R.id.header);
        mHeader.setText(R.string.title_favorites);

        mDb = new DB(getActivity());
        mDb.open();
        mCursor = mDb.getAllDataFav();

        String[] from = new String[] { DB.COLUMN_FROM, DB.COLUMN_TO, DB.COLUMN_LANG };
        int[] to = new int[] { R.id.fromText , R.id.toText, R.id.langText};

        mAdapter = new SimpleCursorAdapter(getContext(), R.layout.history_item, mCursor, from, to, 0);
        mlistView = (ListView) histView.findViewById(R.id.list_history);
        mlistView.setAdapter(mAdapter);
        registerForContextMenu(mlistView);

        mImageView = (ImageView) histView.findViewById(R.id.clear_image);
        mImageView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                mDb.trunc_fav_table();
                mCursor = mDb.getAllDataFav();
                mAdapter.swapCursor(mCursor);
            }
        });

        return histView;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, CM_DELETE_ID, 0, R.string.api_key);
   }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == CM_DELETE_ID) {
            AdapterView.AdapterContextMenuInfo adapterContextMenuInfo  = (AdapterView.AdapterContextMenuInfo) item
                    .getMenuInfo();
            mDb.delRecFav(adapterContextMenuInfo.id);
            mCursor = mDb.getAllDataFav();
            mAdapter.swapCursor(mCursor);

            return true;
        }
        return super.onContextItemSelected(item);
    }
}
