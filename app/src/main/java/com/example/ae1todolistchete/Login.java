package com.example.ae1todolistchete;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.auth.api.signin.GoogleSignInAccount;

import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class Login extends AppCompatActivity {
    MediaPlayer mp;
    MediaPlayer mp2;
    Button acceder;
    TextView botonRegistro;
    private FirebaseAuth mAuth;
    EditText mail, pass, cpass;
    ProgressDialog progressDialog;
    Button btnGoogle;

    int RC_SIGN_IN = 1;
    String TAG = "GoogleSignInLogin";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        getSupportActionBar().hide();


        // Inicializamos la variable Auth
        mAuth = FirebaseAuth.getInstance();

        mail = findViewById(R.id.cajaCorreo);
        pass = findViewById(R.id.cajaPass);


        acceder = findViewById(R.id.botonLogin);
        mp = MediaPlayer.create(this, R.raw.acceso);
        acceder.setOnClickListener(new View.OnClickListener() {


            @Override
            public void onClick(View v) {
                //Login en Firebase
                String email = mail.getText().toString();
                String password = pass.getText().toString();

                if (email.isEmpty() || !email.contains("@") || !email.contains(".")) {
                    showError(mail, "Email no válido");
                } else if (password.isEmpty() || password.length() < 7) {
                    showError(pass, "La contraseña necesita 7 caracteres");

                } else {
                    //crearCuenta();
                    mAuth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information


                                        Intent intent = new Intent(Login.this, MainActivity.class);
                                        startActivity(intent);
                                        mp.start();

                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Toast.makeText(Login.this, "Authentication failed.", Toast.LENGTH_SHORT).show();

                                    }

                                }


                            });
                }


            }

            private void showError(EditText input, String s) {
                input.setError(s);
                input.requestFocus();
            }

        });

        botonRegistro = findViewById(R.id.botonRegistro);
        progressDialog = new ProgressDialog(Login.this);
        progressDialog.setTitle("Creando su cuenta, un momento por favor");
        progressDialog.setCanceledOnTouchOutside(false);
        mp2 = MediaPlayer.create(this, R.raw.mouseclick);
        botonRegistro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Creamos usuario en Firebase

                String email = mail.getText().toString();
                String password = pass.getText().toString();
                if (email.isEmpty() || !email.contains("@") || !email.contains(".")) {
                    showError(mail, "Email no válido");
                } else if (password.isEmpty() || password.length() < 7) {
                    showError(pass, "La contraseña necesita 7 caracteres");

                }else {

                    Task<AuthResult> authResultTask = mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        crearCuenta();


                                        toastCorrecto("El usuario ha sido registrado satisfactoriamente");

                                        Intent intent = new Intent(Login.this, MainActivity.class);
                                        startActivity(intent);
                                        mp2.start();
                                        //mProgressBar.dismiss();

                                    } else {
                                        // If sign in fails, display a message to the user.
                                        toastCancelar("El usuario no ha sido registrado correctamente");

                                        //Toast.makeText(Login.this, "Authentication failed.", Toast.LENGTH_SHORT).show();


                                    }

                                    //mProgressBar = new ProgressDialog(Login.this);
                                }
                            });


                    //Toast.makeText(Login.this, "El Usuario ha sido registrado satisfactoriamente", Toast.LENGTH_LONG).show();
                }

            }
            private void showError(EditText input, String s) {
                input.setError(s);
                input.requestFocus();
            }




            public void toastCorrecto(String mensaje) {
                LayoutInflater layoutInflater = getLayoutInflater();
                View view = layoutInflater.inflate(R.layout.custom_toast, (ViewGroup) findViewById(R.id.toast));
                TextView txtMensaje = view.findViewById(R.id.MensajeToast1);
                txtMensaje.setText(mensaje);

                Toast toast = new Toast(getApplicationContext());
                toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.BOTTOM, 0, 200);
                toast.setDuration(Toast.LENGTH_LONG);
                toast.setView(view);
                toast.show();
            }

            public void toastCancelar(String mensaje) {
                LayoutInflater layoutInflater = getLayoutInflater();
                View view = layoutInflater.inflate(R.layout.custom_toast_delete, (ViewGroup) findViewById(R.id.toastCancelar));
                TextView txtMensaje = view.findViewById(R.id.MensajeToastError);
                txtMensaje.setText(mensaje);

                Toast toast = new Toast(getApplicationContext());
                toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.BOTTOM, 0, 200);
                toast.setDuration(Toast.LENGTH_LONG);
                toast.setView(view);
                toast.show();

            }


        });
    }
    private void crearCuenta() {
        progressDialog.setMessage("Creando su cuenta");
        progressDialog.show();
    }

}




















                            
                        
            
        
    
                        
            
   
                            
                        

            

    








