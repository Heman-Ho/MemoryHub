package ca.sfu.memoryhub;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.content.Intent;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Login extends AppCompatActivity{
    FirebaseAuth mAuth;
    Button btnBackToStart;
    Button btnLogin;
    EditText edtEmailLog;
    EditText edtPasswordLog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    setContentView(R.layout.login);
    mAuth = FirebaseAuth.getInstance();

    //Find buttons and edit text
    btnBackToStart = findViewById(R.id.btnBackToStart2);
    btnLogin = findViewById(R.id.btnLoginToApp);
    edtEmailLog = findViewById(R.id.edtEmailAddressLog);
    edtPasswordLog = findViewById(R.id.edtPasswordLog);

    btnBackToStart.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent i = new Intent(Login.this, StartPage.class);
            startActivity(i);
        }
    });

    btnLogin.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            //Get what was inputted into email and password fields
            String strEmail = edtEmailLog.getText().toString();
            String strPassword = edtPasswordLog.getText().toString();

            //Don't do anything if there is an invalid input
            if(strEmail.isEmpty() || strPassword.isEmpty()){
                return;
            }

            //Authenticate login credentials
            mAuth.signInWithEmailAndPassword(strEmail, strPassword)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // If sign in is successful, display message
                                Toast.makeText(Login.this, "Authentication Successful.",
                                        Toast.LENGTH_SHORT).show();

                            } else {
                                // If sign in fails, display a message to the user.
                                Toast.makeText(Login.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });



        }

    });


    }
}
