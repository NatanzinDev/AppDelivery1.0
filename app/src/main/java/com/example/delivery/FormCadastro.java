package com.example.delivery;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Firebase;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

import de.hdodenhof.circleimageview.CircleImageView;

public class FormCadastro extends AppCompatActivity {
    private CircleImageView foto;
    private Button btCadastrar, btSelecionarFoto;
    private EditText eemail;
    private EditText enome;
    private EditText esenha;
    private TextView msgErro;

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
    }

    public void cadastrarUsuario(View v) {
        String email = eemail.getText().toString();
        String senha = esenha.getText().toString();

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email,senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()){

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