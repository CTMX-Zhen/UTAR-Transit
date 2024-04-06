package my.edu.utar.utartransit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Find the button by its ID
        Button btnNextPage = findViewById(R.id.button);

        // Set OnClickListener on the button
        btnNextPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // When the button is clicked, start the next activity
                Intent intent = new Intent(MainActivity.this, OptimizationRoute.class);
                startActivity(intent);
            }
        });
    }
}