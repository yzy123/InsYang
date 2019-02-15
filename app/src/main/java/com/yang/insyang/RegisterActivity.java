package com.yang.insyang;

import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

/**
 * Register view, user can register account for this app
 */

public class RegisterActivity extends AppCompatActivity {

    EditText username, fullname, email, password;
    Button register;
    TextView txt_login;

    FirebaseAuth auth;
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        username = findViewById(R.id.username);
        fullname = findViewById(R.id.fullname);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        register = findViewById(R.id.register);
        txt_login = findViewById(R.id.txt_login);
        auth = FirebaseAuth.getInstance();



        txt_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });


        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(RegisterActivity.this, "Please wait for seconds", Toast.LENGTH_SHORT).show();

                String str_username = username.getText().toString();
                String str_fullname = fullname.getText().toString();
                String str_email = email.getText().toString();
                String str_password = password.getText().toString();

                if (TextUtils.isEmpty(str_username) || TextUtils.isEmpty(str_fullname) ||
                        TextUtils.isEmpty(str_email) || TextUtils.isEmpty(str_password)) {
                    Toast.makeText(RegisterActivity.this, "All fields are required", Toast.LENGTH_SHORT).show();
                } else if (str_password.length() < 6) {
                    Toast.makeText(RegisterActivity.this, "Password must have more than 6 characters", Toast.LENGTH_SHORT).show();
                }else {
                    register(str_username, str_fullname, str_email, str_password);
                }

            }
        });


    }

    /**
     * register
     *
     * @param username
     * @param fullname
     * @param email
     * @param password
     *
     * upload all user information to firebase
     */
    private void register(final String username, final String fullname, String email, String password) {
        register.setClickable(false);
        register.setTextColor(Color.GRAY);
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = auth.getCurrentUser();
                            String userid = firebaseUser.getUid();
                            reference = FirebaseDatabase.getInstance().getReference().child("Users").child(userid);
                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("id", userid);
                            hashMap.put("username", username.toLowerCase());
                            hashMap.put("fullname", fullname);
                            hashMap.put("bio", "");
                            /** every user use default icon **/
                            hashMap.put("imageurl", "https://firebasestorage.googleapis.com/v0/b/insyang-19d2e.appspot.com/o/user.png?alt=media&token=4c020b1c-0c4a-46a2-a2a0-46fe52b4c47c");
                            reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                    }
                                }
                            });
                        } else {
                          Toast.makeText(RegisterActivity.this, "Sorry, you can't register with email or password", Toast.LENGTH_SHORT).show();
                            register.setClickable(true);
                            register.setTextColor(Color.BLACK);
                        }
                    }
                });

    }
}
