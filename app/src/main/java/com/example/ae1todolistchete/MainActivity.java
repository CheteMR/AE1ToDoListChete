package com.example.ae1todolistchete;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private String idUser;
    private FirebaseFirestore db;

    ListView listViewTareas;
    ArrayAdapter<String> adapterTareas;

    List<String> listaTareas = new ArrayList<>();
    List<String> listaIDTareas = new ArrayList<>();

    // TextView mensajeTextView;





    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Inicializamos la variable Auth
        mAuth = FirebaseAuth.getInstance();
        idUser = mAuth.getCurrentUser().getUid();
        db = FirebaseFirestore.getInstance();

        listViewTareas = findViewById(R.id.listTareas);
        //mensajeTextView= findViewById(R.id.textViewTarea);






    actualizarUI();



}




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
       getMenuInflater().inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }





    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {


        if (item.getItemId() == R.id.mas) {

            //activar el cuadro de dialogo para añadir tarea
            // Toast.makeText(this,"Tarea añadida", Toast.LENGTH_LONG).show();
            final EditText textEditText = new EditText(this);

            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("Nueva tarea")
                    .setMessage("¿Qué quieres hacer a continuación?")

                    .setView(textEditText)
                    .setPositiveButton("Añadir", new DialogInterface.OnClickListener() {


                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String miTarea = textEditText.getText().toString();
                            // AÑADIR TAREA A LA BBDD
                            Map<String, Object> data = new HashMap<>();
                            data.put("nombreTarea", miTarea);
                            data.put("usuario", idUser);




                            db.collection("Tareas")
                                    .add(data)
                                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                        @Override
                                        public void onSuccess(DocumentReference documentReference) {
                                            toastCorrecto("Tarea añadida");
                                            return;
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            toastCancelar("Fallo al crear la tarea");

                                        }
                                    });


                        }
                    })
                    .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            toastCancelar("Tarea Cancelada");
                        }
                    })
                    .create();
            dialog.show();
            return true;



        } else if (item.getItemId() == R.id.logout) {
            //cierre de sesión de Firebase
            mAuth.signOut();

            startActivity(new Intent(MainActivity.this, Login.class));
            finish();
            return true;
        } else return super.onOptionsItemSelected(item);


    }

    public void botonEditar(View view) {
        View parent = (View) view.getParent();
        TextView tareaTextView = parent.findViewById(R.id.textViewTarea);
        String tarea = tareaTextView.getText().toString();
        int posicion = listaTareas.indexOf(tarea);
        editarTarea(posicion);

    }



    public void borrarTarea (View view){
       View parent = (View) view.getParent();
       TextView tareaTextView = parent.findViewById(R.id.textViewTarea);
       String tarea = tareaTextView.getText().toString();
       int posicion = listaTareas.indexOf(tarea);

       db.collection("Tareas").document(listaIDTareas.get(posicion)).delete();
    }



        public void editarTarea(final int posicion) {

            Log.d("MainActivity", "Editar tarea en posición: " + posicion);
            final EditText textEditText = new EditText(this);


            textEditText.setText(listaTareas.get(posicion));

            AlertDialog dialog = new AlertDialog.Builder(this)
                    .setTitle("Editar Tarea")
                    .setView(textEditText)
                    .setPositiveButton("Actualizar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            String nuevaTarea = textEditText.getText().toString();
                            String tareaId = listaIDTareas.get(posicion);


                            if (!TextUtils.isEmpty(nuevaTarea)) {
                                // Actualizar la tarea en la base de datos
                                db.collection("Tareas").document(tareaId)
                                        .update("nombreTarea", nuevaTarea)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                toastCorrecto("Tarea actualizada");
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                toastCancelar("Fallo al actualizar la tarea");
                                            }
                                        });
                            } else {
                                toastCancelar("La tarea no puede estar vacia");
                            }
                        }
                    })
                    .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // El usuario ha cancelado la edición
                            dialog.cancel();
                        }
                    })
                    .create();

            dialog.show();
        }






    public void toastCorrecto(String mensaje) {
                LayoutInflater layoutInflater = getLayoutInflater();
                View view = layoutInflater.inflate(R.layout.custom_toast_tarea, (ViewGroup) findViewById(R.id.toastTarea));
                TextView txtMensaje = view.findViewById(R.id.MensajeToast2);
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



                    private void actualizarUI() {
                        db.collection("Tareas")
                                .whereEqualTo("usuario", idUser)
                                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                                    @Override
                                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException e) {
                                        if (e != null) {
                                            return;
                                        }


                                        listaTareas.clear();
                                        listaIDTareas.clear();
                                        for (QueryDocumentSnapshot doc : value) {
                                            listaTareas.add(doc.getString("nombreTarea"));
                                            listaIDTareas.add(doc.getId());

                                        }
                                        //RELLENAMOS EL LISTVIEW CON EL ADAPTER
                                        if (listaTareas.size() == 0) {
                                            listViewTareas.setAdapter(null);
                                        } else {



                                            adapterTareas = new ArrayAdapter<>(MainActivity.this, R.layout.item_tarea, R.id.textViewTarea, listaTareas);
                                            listViewTareas.setAdapter(adapterTareas);



                                        }





                                    }


                                });


                    }
    public static boolean nombreCliente (String nombre){
        boolean nombreClienteCorrecto = false;
        Pattern patron = Pattern.compile("[0-9A-Za-zñÑáéíóúÁÉÍÓÚ¡!¿?@#$%()=+-€/,.]{1,50}");
        Matcher comprobacion= patron.matcher(nombre);
        if(comprobacion.matches()){
            nombreClienteCorrecto = true;

        }
        return nombreClienteCorrecto;
    }


                }