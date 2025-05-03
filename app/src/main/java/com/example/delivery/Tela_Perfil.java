package com.example.delivery;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import de.hdodenhof.circleimageview.CircleImageView;

public class Tela_Perfil extends AppCompatActivity {

    private Button bt_editar;
    private CircleImageView fotoUsuario;
    private TextView txtNome, txtEmail;
    private String usuarioID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tela_perfil);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        iniciarComponentes();
    }

    @Override
    protected void onStart() {
        super.onStart();

        //fazendo conexão com o banco buscando os dados
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        usuarioID = FirebaseAuth.getInstance().getUid();
        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        //buscando referencia do banco a patir do hashmap definido na formcadastro
        DocumentReference documentReference = db.collection("Usuarios").document(usuarioID);
        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value != null){
                    // recuperando dados com a chave do hashmap
                    Glide.with(getApplicationContext()).load(value.getString("foto")).into(fotoUsuario);
                    txtNome.setText(value.getString("nome"));
                    txtEmail.setText(email);
                }
            }
        });
    }

    public void iniciarComponentes(){
        bt_editar = findViewById(R.id.bt_editarPerfil);
        txtEmail = findViewById(R.id.txt_email);
        txtNome = findViewById(R.id.txt_nome);
        fotoUsuario = findViewById(R.id.fotoUsuario);
    }
}