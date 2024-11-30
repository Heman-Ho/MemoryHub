package ca.sfu.memoryhub;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import ca.sfu.memoryhub.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    // Binding object to reference the views in the activity layout
    private ActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Check if user is logged in
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            // User is not logged in, redirect to StartPage
            Intent intent = new Intent(MainActivity.this, StartPage.class);
            startActivity(intent);
            return;
        }


        // Initialize the binding object to access the layout views
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupWithNavController(binding.navView, navController);

        adjustBottomNavBarHeight(navView);


    }

    private void adjustBottomNavBarHeight(BottomNavigationView navView) {
        // Get screen width
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int screenWidth = displayMetrics.widthPixels;

        // Set Bottom Navigation Bar height based on screen size
        if (screenWidth < 600) { // Small screen (phones)
            navView.getLayoutParams().height = 130; // Small height for small screens
        } else if (screenWidth < 1200) { // Medium screen (tablets)
            navView.getLayoutParams().height = 170; // Medium height for medium screens
        } else { // Large screen (large tablets or other devices)
            navView.getLayoutParams().height = 210; // Larger height for large screens
        }

        // Apply the layout changes
        navView.requestLayout();
    }

}