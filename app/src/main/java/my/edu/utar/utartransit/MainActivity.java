package my.edu.utar.utartransit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.os.Debug;
import android.util.Log;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import my.edu.utar.utartransit.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        //FloatingActionButton floatingActionButton = findViewById(R.id.floating_button);
        replaceFragment(new HomeFragment());
        binding.bottomNavView.setBackground(null);

        binding.bottomNavView.setOnNavigationItemSelectedListener(item -> {

            if (item.getItemId() == R.id.home_btn) {
                replaceFragment(new HomeFragment());
                //floatingActionButton.set
            } else if (item.getItemId() == R.id.search_btn) {
                Log.i("Debug","here is btn clicked");
                replaceFragment(new OptimizationRoute());
            } else if (item.getItemId() == R.id.favourite_btn) {
                Log.i("Debug","here is btn clicked");

                replaceFragment(new FavouriteFragment());
            } else if (item.getItemId() == R.id.time_btn) {
                replaceFragment(new TimeFragment());
            } else if (item.getItemId() == R.id.notification_btn) {
                //replaceFragment(new ());
            }

            return true;
        });
    }

    private void replaceFragment(Fragment fragment){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout,fragment);
        fragmentTransaction.commit();
    }


}