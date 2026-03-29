package com.manilalinkup.app;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {
    private android.app.ProgressDialog progressDialog;
    private com.google.firebase.auth.FirebaseAuth mAuth;
    MaterialToolbar toolbar;
    MaterialButton loginNowButton;
    TextInputEditText emailInput;
    TextInputEditText passwordInput;
    TextInputLayout emailLayout;
    TextInputLayout passwordLayout;
    TextView forgetPassword;

    //for testing Dashboards - Irish
    ImageView googleLogin;
    ImageView facebookLogin; // Added for Facebook shortcut


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        loginNowButton = findViewById(R.id.material_button_login_now_2);
        emailInput = findViewById(R.id.text_input_email_input);
        passwordInput = findViewById(R.id.text_input_password_input);
        emailLayout = findViewById(R.id.text_input_layout_email_address);
        passwordLayout = findViewById(R.id.text_input_layout_password);
        forgetPassword = findViewById(R.id.text_view_forget_password);

        // For testing Dashboards - Irish
        googleLogin = findViewById(R.id.image_view_login_google);
        googleLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent testIntents = new Intent(LoginActivity.this, EmployerDashboard.class);
                startActivity(testIntents);
            }
        });

        // For testing Seeker Dashboard via Facebook shortcut
        facebookLogin = findViewById(R.id.image_view_login_facebook);
        facebookLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Direct jump to Seeker Dashboard
                Intent intent = new Intent(LoginActivity.this, SeekerDashboardActivity.class);
                startActivity(intent);
                finish(); // Optional: closes login screen so back button doesn't return here
            }
        });



        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        progressDialog = new android.app.ProgressDialog(this);
        progressDialog.setMessage("Verifying account...");
        progressDialog.setCancelable(false); // Prevents user from dismissing it by clicking outside

        mAuth = com.google.firebase.auth.FirebaseAuth.getInstance();
        loginNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = emailInput.getText().toString().trim();
                String password = passwordInput.getText().toString().trim();

                // Reset errors
                emailLayout.setError(null);
                passwordLayout.setError(null);

                // Validation Checks
                if(email.isEmpty()){
                    emailLayout.setError("Email is required.");
                    emailInput.requestFocus();
                    return; // Stop here
                }

                if(password.isEmpty()){
                    passwordLayout.setError("Password is required.");
                    passwordInput.requestFocus();
                    return; // Stop here
                }

                if(password.length() < 8){
                    passwordLayout.setError("Password must be at least 8 characters.");
                    passwordInput.requestFocus();
                    return; // Stop here
                }

                // Start the Firebase Login (The app waits for this result)
                Toast.makeText(LoginActivity.this, "Authenticating...", Toast.LENGTH_SHORT).show();

                progressDialog.show();

                mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user != null) {
                                    user.reload().addOnCompleteListener(reloadTask -> {
                                        if (user.isEmailVerified()) {
                                            // NEW: Don't jump to an activity yet. Check the role first!
                                            checkUserRole(user.getUid());
                                        } else {
                                            progressDialog.dismiss();
                                            Toast.makeText(LoginActivity.this, "Please verify your email first!", Toast.LENGTH_LONG).show();
                                            mAuth.signOut();
                                        }
                                    });
                                }
                            } else {
                                progressDialog.dismiss();
                                Toast.makeText(LoginActivity.this, "Login Failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        });

            }
        });
    }

    private void checkUserRole(String uid) {
        com.google.firebase.database.DatabaseReference dbRef = com.google.firebase.database.FirebaseDatabase.getInstance().getReference();

        dbRef.child("seekers").child(uid).addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(com.google.firebase.database.DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    progressDialog.dismiss();
                    startActivity(new Intent(LoginActivity.this, SeekerDashboardActivity.class));
                    finish();
                } else {
                    // If not found in seekers, check employers
                    checkEmployerNode(uid, dbRef);
                }
            }

            @Override
            public void onCancelled(com.google.firebase.database.DatabaseError error) {
                // IF PERMISSION DENIED: It just means this user isn't a Seeker.
                // Don't show an error toast yet; just move to the next check!
                if (error.getCode() == com.google.firebase.database.DatabaseError.PERMISSION_DENIED) {
                    checkEmployerNode(uid, dbRef);
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(LoginActivity.this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    // Separate method to keep code clean
    private void checkEmployerNode(String uid, com.google.firebase.database.DatabaseReference dbRef) {
        dbRef.child("employers").child(uid).addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(com.google.firebase.database.DataSnapshot snapshot) {
                progressDialog.dismiss();
                if (snapshot.exists()) {
                    startActivity(new Intent(LoginActivity.this, EmployerDashboard.class));
                    finish();
                } else {
                    // Now we can safely say the user is truly not found anywhere
                    Toast.makeText(LoginActivity.this, "User profile not found in our system.", Toast.LENGTH_LONG).show();
                    mAuth.signOut();
                    progressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(com.google.firebase.database.DatabaseError error) {
                progressDialog.dismiss();
                // If both checks return Permission Denied, something is wrong with the Rules or UID
                Toast.makeText(LoginActivity.this, "Access Denied. Please contact support.", Toast.LENGTH_SHORT).show();
                mAuth.signOut();
            }
        });
    }
}