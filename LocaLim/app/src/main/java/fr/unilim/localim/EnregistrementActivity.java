package fr.unilim.localim;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class EnregistrementActivity extends AppCompatActivity {
    protected EditText etEmail, etMotDePasse, etConfirmMotDePasse;
    protected FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enregistrement);
        etEmail = findViewById(R.id.email);
        etMotDePasse = findViewById(R.id.mot_de_passe);
        etConfirmMotDePasse = findViewById(R.id.confirm_mot_de_passe);
        auth = FirebaseAuth.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();

        /* Controle du jeton de connexion */
        if(auth.getCurrentUser() != null){
            /*
                Renvoyer vers la liste d'offres si jeton existant
             */
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }

    public void onClickSEnregistrer(View view){
        String email = etEmail.getText().toString();
        String motDePasse = etMotDePasse.getText().toString();
        String confirmMotDePasse = etConfirmMotDePasse.getText().toString();

        /*
            Controle du formulaire
         */
        if((!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches())
                && (!TextUtils.isEmpty(motDePasse))) {
            if(!TextUtils.isEmpty(confirmMotDePasse) && motDePasse.equals(confirmMotDePasse)) {
                /*
                    Enregistrement
                 */
                auth.createUserWithEmailAndPassword(email, motDePasse)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(Task<AuthResult> task) {
                                Intent intent; /* intent de renvoi */

                                if (task.isSuccessful()) {
                                    /* Message de succes */
                                    Toast.makeText(EnregistrementActivity.this,
                                            "Enregistrement avec succès",
                                            Toast.LENGTH_SHORT).show();

                                    /*
                                        Renvoyer vers l'activite de creation d'offre
                                     */
                                    intent = new Intent(EnregistrementActivity.this,
                                            CreationActivity.class);
                                    startActivity(intent);
                                } else {
                                    /* Message d'echec */
                                    Toast.makeText(EnregistrementActivity.this,
                                            "Echec de l'entregistrement. " +
                                                    "Vérifiez vos identifiants",
                                            Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
            else{
                Toast.makeText(EnregistrementActivity.this,
                        "Les mots de passe ne correspondent pas !",
                        Toast.LENGTH_SHORT).show();
            }
        }
        else{
            Toast.makeText(EnregistrementActivity.this,
                    "Les champs du formulaire ne sont pas valides !",
                    Toast.LENGTH_SHORT).show();
        }
    }
}