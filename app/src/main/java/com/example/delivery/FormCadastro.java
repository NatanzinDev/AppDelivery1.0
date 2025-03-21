package com.example.delivery;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Firebase;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class FormCadastro extends AppCompatActivity {
    private String IDuser;
    private CircleImageView foto;
    private Button btCadastrar, btSelecionarFoto;
    private EditText eemail;
    private EditText enome;
    private EditText esenha;
    private TextView msgErro;
    private Uri selecionarUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_form_cadastro);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        iniciarComponente();

        enome.addTextChangedListener(cadastroTextWatcher);
        esenha.addTextChangedListener(cadastroTextWatcher);
        eemail.addTextChangedListener(cadastroTextWatcher);

        btCadastrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cadastrarUsuario(v);
            }
        });

        btSelecionarFoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelecionarFt();
            }
        });
    }

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult o) {
                    if(o.getResultCode() == Activity.RESULT_OK){
                        Intent data = o.getData();
                        selecionarUri = data.getData();

                        try {
                            foto.setImageURI(selecionarUri);
                        }catch (Exception e){
                            e.printStackTrace();
                        }


                    }
                }
            }
    );
    public void SelecionarFt() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        activityResultLauncher.launch(intent);

    }

    public void cadastrarUsuario(View v) {
        String email = eemail.getText().toString();
        String senha = esenha.getText().toString();

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){
                    salvarDados();
                    Snackbar snack = Snackbar.make(v,"Cadastro realizado com sucesso!",Snackbar.LENGTH_INDEFINITE).setAction("OK", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            finish();
                        }
                    });

                    snack.show();
                }else{
                    String erro;

                    try {
                        throw task.getException();

                        //excessão do tamanho de senha
                    }catch (FirebaseAuthWeakPasswordException e){
                        erro = "Digite uma senha com no mínimo 6 caracteres.";

                        // || vericar se usuario digitou um email valido
                    }catch (FirebaseAuthInvalidCredentialsException e){
                        erro = "Seu email não é válido, digite um email válido.";

                        // || verifica se o email já está cadastrada
                    }catch (FirebaseAuthUserCollisionException e){
                        erro = "Email já cadastrado, tente outro email.";

                        // || para verificar se o usuario está com internet
                    }catch (FirebaseNetworkException e){
                        erro = "Verifique sua conexão com a internt";
                    }catch (Exception e){
                        erro = "Erro ao cadastrar usuário";
                    }

                    msgErro.setText(erro);
                }
            }
        });

    }

    public void salvarDados() {
        String nomeArquivo = UUID.randomUUID().toString();
        final StorageReference reference = FirebaseStorage.getInstance().getReference("/imagens/"+nomeArquivo);

        reference.putFile(selecionarUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {

                        String ufoto = uri.toString();

                        //Iniciar BD - FireStore
                        String nome = enome.getText().toString();
                        FirebaseFirestore db = FirebaseFirestore.getInstance();

                        Map<String,Object> usuarios = new HashMap<>();
                        usuarios.put("nome",nome);
                        usuarios.put("foto",ufoto);

                        //getCurrentUser corresponde ao usuario atual
                        IDuser = FirebaseAuth.getInstance().getCurrentUser().getUid();
                        DocumentReference documentReference = db.collection("Usuarios").document(IDuser);
                        documentReference.set(usuarios).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.i("db","Sucesso ao salvar os dados");
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.i("db_erro","Erro ao salvar os dados"+ e.toString());

                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    //para visualizar e trocar a cor do botão caso os campos estejam preechidos
    TextWatcher cadastroTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String nome = enome.getText().toString();
            String email = eemail.getText().toString();
            String senha = esenha.getText().toString();

            if(!nome.isEmpty() && !email.isEmpty() && !senha.isEmpty()){
                btCadastrar.setEnabled(true);
                btCadastrar.setBackgroundColor(getResources().getColor(R.color.darkred));

            }else{
                btCadastrar.setEnabled(false);
                btCadastrar.setBackgroundColor(getResources().getColor(R.color.gray));

            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    public void iniciarComponente(){
        foto = findViewById(R.id.fotoUsuario);
        btCadastrar = findViewById(R.id.bt_cadastrar);
        btSelecionarFoto = findViewById(R.id.bt_selecionar);
        esenha = findViewById(R.id.edit_senha);
        enome = findViewById(R.id.edit_nome);
        eemail = findViewById(R.id.edit_email);
        msgErro = findViewById(R.id.txt_msgErro);

    }
}