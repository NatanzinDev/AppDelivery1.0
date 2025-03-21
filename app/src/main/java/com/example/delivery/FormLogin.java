package com.example.delivery;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Firebase;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;

public class FormLogin extends AppCompatActivity {
    private TextView txt_criarConta,txt_msgErro;
    private EditText edit_email,edit_senha;
    private Button bt_entrar;
    private ProgressBar progressBar;


    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_form_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        iniciarComponentes();

        txt_criarConta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(FormLogin.this, FormCadastro.class);
                startActivity(i);
            }
        });

        bt_entrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = edit_email.getText().toString();
                String senha = edit_senha.getText().toString();

                if(email.isEmpty() || senha.isEmpty()){
                    txt_msgErro.setText("Preencha todos os campos.");
                }else {
                    txt_msgErro.setText("");
                    AutenticarUsuario();
                }
            }

            public void AutenticarUsuario() {
                String email = edit_email.getText().toString();
                String senha = edit_senha.getText().toString();

                FirebaseAuth.getInstance().signInWithEmailAndPassword(email,senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                txt_msgErro.setText("");
                                progressBar.setVisibility(View.VISIBLE);

                                new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        IniciarTelaProdutos();
                                    }
                                },3000);
                            }else{
                                String erro;



                                try {
                                    throw task.getException();


                                } catch (FirebaseAuthInvalidCredentialsException e){
                                    erro = "Algum problema com seu email ou sua senha, tente novamente";

                                }catch (FirebaseNetworkException e){
                                    erro = "Verifique sua conexão com a internt";
                                }catch (Exception e){
                                    erro = "Erro ao cadastrar usuário";
                                }

                                txt_msgErro.setText(erro);

                            }
                    }
                });
            }
        });

    }

    public void IniciarTelaProdutos(){
        Intent intent = new Intent(FormLogin.this,Lista_Produtos.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser usuarioAtual = FirebaseAuth.getInstance().getCurrentUser();

        if(usuarioAtual != null){
            IniciarTelaProdutos();
        }
    }

    public void iniciarComponentes(){
        txt_criarConta = findViewById(R.id.txt_criarConta);
        txt_msgErro = findViewById(R.id.txt_msgErro);
        edit_email = findViewById(R.id.edit_email);
        edit_senha = findViewById(R.id.edit_senha);
        bt_entrar = findViewById(R.id.bt_entrar);
        progressBar = findViewById(R.id.progressBar);

    }
}