package ca.sfu.memoryhub;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.content.Intent;

import androidx.appcompat.app.AppCompatActivity;

public class Register extends AppCompatActivity{

    EditText edtUsernameReg, edtPasswordReg;
    Button btnRegisterAccount, btnBackToStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        edtUsernameReg = findViewById(R.id.edtUsernameReg);
        edtPasswordReg = findViewById(R.id.edtPasswordReg);
        btnRegisterAccount = findViewById(R.id.btnRegisterAccount);
        btnBackToStart = findViewById(R.id.btnBackToStart);

        btnBackToStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Register.this, StartPage.class);
                startActivity(i);
            }
        });


    }
}

