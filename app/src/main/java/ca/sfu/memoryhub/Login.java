package ca.sfu.memoryhub;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

public class Login extends AppCompatActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    setContentView(R.layout.login);

    Button btnBackToStart2;
    btnBackToStart2 = findViewById(R.id.btnBackToStart2);

    btnBackToStart2.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent i = new Intent(Login.this, StartPage.class);
            startActivity(i);
        }
    });


    }
}
