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

public class ConnexionActivity extends AppCompatActivity {
    protected EditText etEmail, etMotdePasse;
    protected FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connexion);
        etEmail = findViewById(R.id.email);
        etMotdePasse = findViewById(R.id.mot_de_passe);
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

    public void onClickSeConnecter(View view){
        String email = etEmail.getText().toString();
        String motDePasse = etMotdePasse.getText().toString();

        /*
            Controle du formulaire
         */
        if((!TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches())
                && (!TextUtils.isEmpty(motDePasse))) {
            /*
                Connexion
             */
            auth.signInWithEmailAndPassword(email, motDePasse)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(Task<AuthResult> task) {
                            Intent intent; /* intent de renvoi */

                            if (task.isSuccessful()) {
                                /* Message de succes */
                                Toast.makeText(ConnexionActivity.this,
                                        "Connexion avec succès", Toast.LENGTH_SHORT).show();

                                /*
                                    Renvoyer vers la creation d'offre
                                 */
                                intent = new Intent(ConnexionActivity.this,
                                        CreationActivity.class);
                                startActivity(intent);
                            }
                            else {
                                /* Message d'echec */
                                Toast.makeText(ConnexionActivity.this,
                                        "Echec de la connexion. Vérifiez vos identifiants",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
        else{
            Toast.makeText(ConnexionActivity.this,
                    "Les champs du formulaire ne sont pas valides !",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void onClickSEnregistrer(View view){
        Intent intent = new Intent(this, EnregistrementActivity.class);
        startActivity(intent);
    }
}