package ca.sfu.memoryhub;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class StartPage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.start_page);

        // Find the buttons by their IDs
        Button btnLogin = findViewById(R.id.button_go_to_login);
        Button btnRegis = findViewById(R.id.button_to_register);

        btnLogin.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartPage.this, Login.class);
                startActivity(intent); // opens login page
            }
        });
        btnRegis.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(StartPage.this, Register.class);
                startActivity(intent);
            }
        });


    }
}
