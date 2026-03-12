package com.manilalinkup.app;

<<<<<<< Updated upstream
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
=======
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
>>>>>>> Stashed changes
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class LoginActivity extends AppCompatActivity {

<<<<<<< Updated upstream
    MaterialToolbar toolbar;
    MaterialButton loginNowButton;
    TextInputEditText emailInput;
    TextInputEditText passwordInput;
    TextInputLayout emailLayout;
    TextInputLayout passwordLayout;
    TextView createPassword;


    @SuppressLint("MissingInflatedId")
=======
    private static final String TAG = "LoginActivity";

    private GoogleSignInClient googleSignInClient;
    private FirebaseAuth firebaseAuth;
    private ActivityResultLauncher<Intent> googleSignInLauncher;

    private TextInputLayout emailLayout;
    private TextInputLayout passwordLayout;
    private MaterialButton btnLogin;
    private ImageView btnGoogle;
    private ImageView btnFacebook;
    private TextView tvForgotPassword;
    private TextView tvSignUp;

>>>>>>> Stashed changes
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
<<<<<<< Updated upstream
        loginNowButton = findViewById(R.id.material_button_login_now_2);
        emailInput = findViewById(R.id.text_input_email_input);
        passwordInput = findViewById(R.id.text_input_password_input);
        emailLayout = findViewById(R.id.text_input_layout_email_address);
        passwordLayout = findViewById(R.id.text_input_layout_password);
        createPassword = findViewById(R.id.text_view_forget_password);


        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        loginNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String email = emailInput.getText().toString().trim();
                String password = passwordInput.getText().toString().trim();
                boolean isValid = true;

                emailLayout.setError(null);
                passwordLayout.setError(null);

                if(email.isEmpty()){
                    emailLayout.setError("Email is required.");
                    emailInput.requestFocus();
                    isValid = false;
                } else if(password.isEmpty()){
                    passwordLayout.setError("Password is required.");
                    passwordInput.requestFocus();
                    isValid = false;
                } else if(password.length() < 8){
                    passwordLayout.setError("Password must be at least 8 characters.");
                    passwordInput.requestFocus();
                    isValid = false;
                } else{
                    Intent loginNowActivityIntent = new Intent(LoginActivity.this, OTPVerificationActivity.class);
                    startActivity(loginNowActivityIntent);
                    Toast.makeText(LoginActivity.this,"Verification Code sent.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        createPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent createPasswordActivityIntent = new Intent(LoginActivity.this, OTPVerificationActivity.class);
                startActivity(createPasswordActivityIntent);
            }
        });

=======

        firebaseAuth = FirebaseAuth.getInstance();



        initViews();
        setupGoogleSignIn();

        googleSignInLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(result.getData());
                    try {
                        GoogleSignInAccount account = task.getResult(ApiException.class);
                        firebaseAuthWithGoogle(account);
                    } catch (ApiException e) {
                        Log.e(TAG, "Google sign in failed: " + e.getStatusCode());
                        Toast.makeText(this, "Google Sign-In failed: " + e.getStatusCode(), Toast.LENGTH_SHORT).show();
                    }
                }
        );

        setupClickListeners();
    }

    private void initViews() {
        emailLayout      = findViewById(R.id.text_input_layout_email_address);
        passwordLayout   = findViewById(R.id.text_input_layout_password);
        btnLogin         = findViewById(R.id.material_button_login_now_2);
        btnGoogle        = findViewById(R.id.image_view_login_google);
        btnFacebook      = findViewById(R.id.image_view_login_facebook);
        tvForgotPassword = findViewById(R.id.text_view_forget_password);
        tvSignUp         = findViewById(R.id.text_view_login_sign_up);
    }

    private void setupGoogleSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    private void setupClickListeners() {
        btnGoogle.setOnClickListener(v -> launchGoogleSignIn());


        tvSignUp.setOnClickListener(v ->
                Toast.makeText(this, "Sign Up coming soon!", Toast.LENGTH_SHORT).show());

        btnFacebook.setOnClickListener(v ->
                Toast.makeText(this, "Facebook login coming soon!", Toast.LENGTH_SHORT).show());
    }


    private void launchGoogleSignIn() {
        googleSignInClient.signOut().addOnCompleteListener(this, task -> {
            googleSignInLauncher.launch(googleSignInClient.getSignInIntent());
        });
    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        firebaseAuth.signInWithCredential(credential)
                .addOnSuccessListener(authResult -> {
                    Log.d(TAG, "Google auth success: " + authResult.getUser().getEmail());
                    goToMain();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Google auth failed", e);
                    Toast.makeText(this, "Authentication failed", Toast.LENGTH_SHORT).show();
                });
    }

    private void goToMain() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
>>>>>>> Stashed changes
    }
}