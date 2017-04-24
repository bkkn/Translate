package me.bkkn.translate.ui.fragments;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import me.bkkn.translate.data.DB;
import me.bkkn.translate.R;

public class HistoryFragment extends Fragment {

    ListView mListView;
    DB mDB;
    SimpleCursorAdapter mAdapter;
    Cursor mCursor;
    TextView mHeader;
    ImageView mImageView;

    public HistoryFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View histView = inflater.inflate(R.layout.fragment_list, container, false);

        mDB = new DB(getActivity());
        mDB.open();
        mCursor = mDB.getAllData();

        mHeader = (TextView) histView.findViewById(R.id.header);
        mHeader.setText(R.string.title_history);

        String[] from = new String[] { DB.COLUMN_FROM, DB.COLUMN_TO, DB.COLUMN_LANG, DB.COLUMN_FAV };
        int[] to = new int[] { R.id.fromText , R.id.toText, R.id.langText, R.id.imageGrey};

        mAdapter = new SimpleCursorAdapter(getContext(), R.layout.history_item, mCursor, from, to, 0);
        mListView = (ListView) histView.findViewById(R.id.list_history);

        mListView.setAdapter(mAdapter);
        registerForContextMenu(mListView);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View itemClicked, int position, long id) {

                TextView myFromView = (TextView) itemClicked.findViewById(R.id.fromText);
                String favFrom = myFromView.getText().toString();

                TextView myToView = (TextView) itemClicked.findViewById(R.id.toText);
                String favTo = myToView.getText().toString();

                TextView myLangView = (TextView) itemClicked.findViewById(R.id.langText);
                String favLang = myLangView.getText().toString();

                mDB.addRecFav(favFrom, favTo, favLang);

                Toast.makeText(getActivity(), R.string.added_to_favorites, Toast.LENGTH_SHORT).show();
            }

        });

        mImageView = (ImageView) histView.findViewById(R.id.clear_image);
        mImageView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                mDB.trunc_table();
                mCursor = mDB.getAllData();
                mAdapter.swapCursor(mCursor);
            }
        });

        return histView;

    }
}
