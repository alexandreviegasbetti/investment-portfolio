package br.com.alexandre.investmentportfolio.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import br.com.alexandre.investmentportfolio.R;

import static br.com.alexandre.investmentportfolio.config.FireBaseConfig.getFirebaseAuth;

public class MainActivity extends AppCompatActivity {

    private final FirebaseAuth user = getFirebaseAuth();

    private EditText etEmail;
    private EditText etPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.Theme_AppCompat_Light_NoActionBar);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        Button btnRegister = findViewById(R.id.btnRegister);
        Button btnLogin = findViewById(R.id.btnLogin);
        Button btnReset = findViewById(R.id.btnReset);

        btnLogin.setOnClickListener(v -> loginUser());
        btnRegister.setOnClickListener(v -> createFirebaseUser());
        btnReset.setOnClickListener(v -> {
            user.sendPasswordResetEmail(etEmail.getText().toString());
            Toast.makeText(this, getResources().getString(R.string.reset_password),
                    Toast.LENGTH_LONG).show();
        });

        if (Objects.nonNull(user.getCurrentUser())) {
            Intent intent = new Intent(MainActivity.this, ListItemsActivity.class);
            startActivity(intent);
        }
    }

    private void createFirebaseUser() {
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();
        if (!email.isEmpty() && !password.isEmpty()) {
            user.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(MainActivity.this, task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, getResources().getString(R.string.user_created), Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(MainActivity.this, ListItemsActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(MainActivity.this, getResources().getString(R.string.erro_creating_user), Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }

    private void loginUser() {
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();
        if (!email.isEmpty() && !password.isEmpty()) {
            user.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(MainActivity.this, task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this,
                                    getResources().getString(R.string.logging_success), Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(MainActivity.this, ListItemsActivity.class);
                            startActivity(intent);
                        } else {
                            Toast.makeText(MainActivity.this,
                                    getResources().getString(R.string.error_when_logging), Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }

}