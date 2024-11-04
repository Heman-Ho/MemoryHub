package ca.sfu.memoryhub;

import android.os.Bundle;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.content.Intent;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Register extends AppCompatActivity{

    EditText edtUsernameReg, edtPasswordReg;
    Button btnRegisterAccount, btnBackToStart;
    TextView txtDisplayInfoReg;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);
        mAuth = FirebaseAuth.getInstance();

        // Find buttons and text inputs
        edtUsernameReg = findViewById(R.id.edtUsernameReg);
        edtPasswordReg = findViewById(R.id.edtPasswordReg);
        btnRegisterAccount = findViewById(R.id.btnRegisterAccount);
        btnBackToStart = findViewById(R.id.btnBackToStart);
        txtDisplayInfoReg = findViewById(R.id.txtDisplayInfoReg);

        btnBackToStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Register.this, StartPage.class);
                startActivity(i);
            }
        });

        btnRegisterAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String strEmail = edtUsernameReg.getText().toString();
                String strPassword = edtPasswordReg.getText().toString();

                if(strEmail.isEmpty() || strPassword.isEmpty()){
                    Toast.makeText(Register.this, "All fields are required", Toast.LENGTH_SHORT).show();
                }else if (strPassword.length() < 6){
                    Toast.makeText(Register.this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                }
                else{
                    //Create the user
                    mAuth.createUserWithEmailAndPassword(strEmail, strPassword)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                    Toast.makeText(Register.this, "Account successfully created.",
                                            Toast.LENGTH_SHORT).show();
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Toast.makeText(Register.this, "Authentication failed.",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                }
            }
        });
    }
}

