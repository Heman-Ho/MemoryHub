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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class Register extends AppCompatActivity{

    EditText edtUsernameReg, edtPasswordReg, edtUsername;
    Button btnRegisterAccount, btnBackToStart;

    FirebaseAuth mAuth;
    FirebaseDatabase db;

    DatabaseReference reference;


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
        edtUsername = findViewById(R.id.edtUsername);

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
                String strUsername = edtUsername.getText().toString();

                if(strEmail.isEmpty() || strPassword.isEmpty() || strUsername.isEmpty()){
                    Toast.makeText(Register.this, getString(R.string.all_fields_are_required), Toast.LENGTH_SHORT).show();
                }else if (strPassword.length() < 6){
                    Toast.makeText(Register.this, getString(R.string.password_must_be_at_least_6_characters), Toast.LENGTH_SHORT).show();
                }
                else{
                    createUser(strEmail, strPassword, strUsername);
                }
            }
        });
    }

    //Method to create User
    private void createUser(String email, String password, String username){
        //Create the user
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(Register.this, getString(R.string.account_successfully_created),
                                    Toast.LENGTH_LONG).show();
                            String uid = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
                            saveUserData(username, uid);
                        } else {
                            // If sign in fails, display a message to the user.
                            Toast.makeText(Register.this, getString(R.string.authentication_failed),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
    //Method to save user data to database
    private void saveUserData(String username, String uid){
        Users user  = new Users(username);
        db = FirebaseDatabase.getInstance();
        reference = db.getReference("Users");

        // Sets the user's username and difficulty value = 1 to the realtime database
        reference.child(uid).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    edtUsername.setText("");
                    edtUsernameReg.setText("");
                    edtPasswordReg.setText("");
                    Toast.makeText(Register.this, getString(R.string.user_data_saved_successfully), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(Register.this, getString(R.string.failed_to_save_user_data), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}

