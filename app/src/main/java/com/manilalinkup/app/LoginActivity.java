package com.manilalinkup.app;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
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

public class LoginActivity extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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
    }
}