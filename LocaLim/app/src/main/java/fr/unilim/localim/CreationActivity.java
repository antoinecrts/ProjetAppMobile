package fr.unilim.localim;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class CreationActivity extends AppCompatActivity {
    private static final int RESULT_PICK_IMAGE = 1;

    private OffreParcelable nouvelleOffre; /* nouvelle offre creee */

    protected EditText etTitre, etDescription, etLieu;
    protected ImageView ivImage;

    protected Uri imageURI; /* donnee de l'image selectionnee */

    protected FirebaseAuth auth; /* instance Authentication */
    protected FirebaseDatabase database; /* instance Realtime Database */
    protected StorageReference storage; /* reference du Cloud Storage */
    protected StorageReference emplacementImage; /* reference de l'image uploadee */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_creation);

        etTitre = findViewById(R.id.titre);
        etDescription = findViewById(R.id.description);
        etLieu = findViewById(R.id.lieu);
        ivImage = findViewById(R.id.image);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        storage = FirebaseStorage.getInstance().getReference();
    }

    @Override
    protected void onStart() {
        super.onStart();

        /* Controle du jeton de connexion */
        if(auth.getCurrentUser() == null){
            /*
                Renvoyer vers l'activite de connexion si jeton inexistant
             */
            Intent intent = new Intent(this, ConnexionActivity.class);
            startActivity(intent);
        }
    }

    public void onClickCreerOffre(View view){
        final String titre = etTitre.getText().toString();
        final String description = etDescription.getText().toString();
        final String lieu = etLieu.getText().toString();
        /* reference de l'offre dans la base de donnee */
        final DatabaseReference cleOffre = database.getReference("offres").push();

        /*
            Controle du formulaire
         */
        if(!TextUtils.isEmpty(titre) && !(TextUtils.isEmpty(lieu))){
            nouvelleOffre = new OffreParcelable(titre, "", description, lieu);

            if(imageURI != null){
                /*
                    Une image pour l'offre a ete selectionnee par l'utilisateur
                 */
                emplacementImage = storage.child("images/" + cleOffre.getKey());
                /* Uploader cette image sur le Cloud Storage */
                emplacementImage.putFile(imageURI)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        /* Donner a la nouvelle offre la reference vers cette image sur */
                        /* le Cloud Storage */
                        nouvelleOffre.setCheminImage("images/" + cleOffre.getKey());

                        /* Creer la nouvelle offre sur la base de donnee */
                        cleOffre.setValue(nouvelleOffre);
                        // TODO: cas ou l'offre n'est pas creee

                        /* Message d'information */
                        Toast.makeText(CreationActivity.this, "Offre créée",
                                Toast.LENGTH_SHORT).show();

                        /* Renvoyer vers la liste d'offres */
                        startActivity(new Intent(CreationActivity.this,
                                MainActivity.class));

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        Log.e("CreationActivity",
                                "Echec de l'upload de l'image de l'offre :", e);
                    }
                });
            }

            /* Creer la nouvelle offre sur la base de donnee */
            cleOffre.setValue(nouvelleOffre);
            // TODO: cas ou l'offre n'est pas creee

            /* Message d'information */
            Toast.makeText(this, "Offre créée",
                    Toast.LENGTH_SHORT).show();

            /*
                Renvoyer vers la liste d'offres
             */
            startActivity(new Intent(this, MainActivity.class));
        }
        else{
            Toast.makeText(CreationActivity.this,
                    "Les champs du formulaire ne sont pas valides !",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void onClickPickImage(View view){
        /*
            Envoyer vers la galerie d'images
         */
        Intent intent= new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, RESULT_PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        /*
            Obtenir la donnee de l'emplacement de l'image de l'offre selectionnee
         */
        if(requestCode == RESULT_PICK_IMAGE && resultCode == RESULT_OK && data != null) {
            imageURI = data.getData();
            /* Modifier le contenu de l'ImageView */
            ivImage.setImageURI(imageURI);
        }
        else
            Log.w("CreationActivity", "Echec selection de l'image de l'offre :" +
                    requestCode + ", " + resultCode);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_deconnexion, menu);

        /* Recuperation des objets definis dans le dossier menu que l'on a cree */
        MenuItem miDeconnexion = menu.findItem(R.id.deconnexion);

        /*
            Controle du jeton de connexion : etat du bouton de deconnexion
         */
        miDeconnexion.setEnabled(auth.getCurrentUser() != null);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.deconnexion) {
            /*
                Controle du jeton de connexion
             */
            if(auth.getCurrentUser() != null){
                /* Deconnexion de l'utilisateur courant */
                auth.signOut();
                Toast.makeText(this, "Deconnexion avec succès",
                        Toast.LENGTH_SHORT).show();

                /* Renvoyer sur l'activite d'affichage de la liste d'offres */
                startActivity(new Intent(this, MainActivity.class));
            }

            return true;
        }
        else if (item.getItemId() == R.id.home) {
            startActivity(new Intent(this, MainActivity.class));

            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}