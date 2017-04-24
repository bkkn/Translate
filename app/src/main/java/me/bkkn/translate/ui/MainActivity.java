package me.bkkn.translate.ui;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import me.bkkn.translate.R;
import me.bkkn.translate.ui.fragments.FavoritesFragment;
import me.bkkn.translate.ui.fragments.HistoryFragment;
import me.bkkn.translate.ui.fragments.TranslateFragment;

public class MainActivity extends AppCompatActivity {

    private Fragment mFragment;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        FragmentManager manager = getSupportFragmentManager();

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FragmentTransaction transaction = manager.beginTransaction();
            switch (item.getItemId()) {
                case R.id.navigation_translate:
                    mFragment = new TranslateFragment();
                    transaction
                            .replace(R.id.fragment_place, mFragment)
                            .commit();
                    return true;
                case R.id.navigation_history:
                    mFragment = new HistoryFragment();
                    transaction
                            .replace(R.id.fragment_place, mFragment)
                            .commit();
                    return true;
                case R.id.navigation_favorites:
                    mFragment = new FavoritesFragment();
                    transaction
                            .replace(R.id.fragment_place, mFragment)
                            .commit();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null) {
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction()
                    .replace(R.id.fragment_place, new TranslateFragment())
                    .commit();
        }
        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
    }
}
