package uz.xia.ivat.uzbpersiandictionary;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity
        implements Runnable {

    private final int[] mainMenuIds = new int[]{
            R.id.nav_home,
            R.id.nav_favorite,
            R.id.nav_testing,
            R.id.nav_about};
    private AppBarConfiguration mAppBarConfiguration;
    private NavController navController;
    private Handler handler;
    private boolean isDoubleBackPressed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        handler = new Handler(Looper.getMainLooper());
        setupNavigation();
    }

    private void setupNavigation() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        mAppBarConfiguration = new AppBarConfiguration.Builder(mainMenuIds)
                .setOpenableLayout(drawer)
                .build();
        navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
    }

    @Override
    public boolean onSupportNavigateUp() {
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed() {
        try {
            if (navController.getCurrentDestination().getId() == R.id.nav_home)
                if (isDoubleBackPressed)
                    finish();
                else {
                    Toast.makeText(this, R.string.warning_exit_app_back_pressed, Toast.LENGTH_SHORT).show();
                    isDoubleBackPressed = true;
                    handler.postDelayed(this, 2000);
                }
            else
                super.onBackPressed();
        } catch (Exception e) {
            Log.e(getClass().getSimpleName(), e.getMessage());
            super.onBackPressed();
        }

    }

    @Override
    public void run() {
        isDoubleBackPressed = false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(this);
    }
}