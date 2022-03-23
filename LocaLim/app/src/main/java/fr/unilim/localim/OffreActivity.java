package fr.unilim.localim;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class OffreActivity extends AppCompatActivity {
    /*
        permet de savoir de quel intent provient l'offre
     */
    private static final String N = "suiv";
    private static final String P = "prec";

    private float x1, x2; /* utilisation lorsque l'on touche l'écran*/

    protected ImageView ivImage;
    protected TextView tvDescription, tvLieu;

    /* liste d'offres que l'on récupère de l'activité principale */
    protected ArrayList<OffreParcelable> offres ;
    /* offre que l'on récupère à partir de l'activité principale ou de cette activite */
    protected OffreParcelable offre;

    protected FirebaseAuth auth; /* instance Authentication */
    protected FirebaseStorage storage; /* instance Cloud Storage */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offre);

        ivImage = findViewById(R.id.image);
        tvDescription = findViewById(R.id.description);
        tvLieu = findViewById(R.id.lieu);

        auth = FirebaseAuth.getInstance();
        storage = FirebaseStorage.getInstance();
        /* reference de l'image de l'offre dans le Cloud Storage */
        StorageReference emplacementImage;

        /*
            Recuperer l'offre envoyee par la ListView
         */
        Intent intent = getIntent();
        Bundle bundleOffres = intent.getExtras(); /* contenant des offres de la liste d'offres */
        if (intent.getStringExtra("origine").equals("suiv"))
            offre = intent.getParcelableExtra("suiv");
        else if (intent.getStringExtra("origine").equals("prec"))
            offre = intent.getParcelableExtra("prec");
        else
            offre = intent.getParcelableExtra("offre");
        offres = bundleOffres.getParcelableArrayList("offres");

        /*
            Modifier le contenu du layout
         */
        setTitle(offre.getTitre());
        /* Recuperer l'image de l'offre sur le cloud storage */
        if(!offre.getCheminImage().isEmpty()) {
            /*
                Une image a ete definie par l'utilisateur
             */
            ivImage = findViewById(R.id.image);
            emplacementImage = storage.getReference(offre.getCheminImage());
            Glide.with(this).using(new FirebaseImageLoader()).load(emplacementImage)
                    .into(ivImage);
        }
        tvDescription.setText(offre.getDescription());
        tvLieu.setText(offre.getLieu());


        tvDescription.setMovementMethod(new ScrollingMovementMethod());

    }

    @Override
    public boolean onTouchEvent(MotionEvent touchEvent) {
        int indexOffre; /* index de la prochaine offre dans la liste d'offres */

        switch (touchEvent.getAction()){
            case MotionEvent.ACTION_DOWN:
                x1 = touchEvent.getX();

                break;

            case MotionEvent.ACTION_UP:
                x2 = touchEvent.getX();
                if (x1 > x2) {
                    /* Swipe vers la gauche */
                    indexOffre = this.offres.indexOf(offre) + 1;

                    /* Cas de la derniere offre */
                    if(indexOffre < this.offres.size())
                        versOffreSuiv();
                }
                else if (x2 > x1) {
                    /* Swipe vers la droite */
                    indexOffre = this.offres.indexOf(offre) - 1;

                    /* Cas de la premiere offre */
                    if(indexOffre >= 0)
                        versOffrePrec();
                }

                break;
        }

        return false;
    }

    private void versOffrePrec(){
        Bundle bundleOffers = new Bundle(); /* contenant des offres de la liste d'offres */
        Intent intent; /* intent de renvoi vers l'offre precedente */
        /* index de l'offre precedente dans la liste d'offres */
        int indexOffre = offres.indexOf(offre) - 1;

        /*
            Envoyer vers l'activite d'affichage du contenu de l'offre precedente
         */
        intent = new Intent(OffreActivity.this, OffreActivity.class);
        bundleOffers.putParcelableArrayList("offres", offres);
        intent.putExtra("prec", offres.get(indexOffre));
        intent.putExtra("origine", P);
        intent.putExtras(bundleOffers);
        startActivity(intent);
    }

    private void versOffreSuiv(){
        Bundle bundleOffers = new Bundle(); /* contenant des offres de la liste d'offres */
        Intent intent; /* intent de renvoi vers l'offre suivante */
        /* index de l'offre suivante dans la liste d'offres */
        int indexOffre = offres.indexOf(offre) + 1;

        /*
            Envoyer vers l'activite d'affichage du contenu de l'offre precedente
         */
        intent = new Intent(OffreActivity.this, OffreActivity.class);
        bundleOffers.putParcelableArrayList("offres", offres);
        intent.putExtra("suiv", offres.get(indexOffre));
        intent.putExtra("origine", N);
        intent.putExtras(bundleOffers);
        startActivity(intent);
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