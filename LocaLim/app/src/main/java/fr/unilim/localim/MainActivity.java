package fr.unilim.localim;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    protected ListView lvListeDOffres;
    protected FloatingActionButton fab;

    protected ListeDOffres listeDOffres; /* liste d'offres */
    protected OffreAdapter offreAdapter; /* Adapter de la liste d'offres */

    protected FirebaseAuth auth; /* instance Authentication */
    protected FirebaseDatabase database; /* instance Realtime Database */
    protected DatabaseReference refOffres; /* reference sur les offres */

    protected static final String M = "main"; /* permet de savoir d'ou provient l'intent */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lvListeDOffres = findViewById(R.id.liste_d_offres);
        fab = findViewById(R.id.fab);

        listeDOffres = new ListeDOffres(this, new ArrayList<OffreParcelable>());
        offreAdapter = new OffreAdapter(this, listeDOffres.getOffres());
        lvListeDOffres.setAdapter(offreAdapter);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        refOffres = database.getReference("offres");

        /* Ecoute d'evenement pour la selection d'offres dans la ListView */
        lvListeDOffres.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent intent; /* intent d'envoi vers offre */
                Bundle bundleOffres = new Bundle(); /* contenant des offres de la liste d'offres */

                /*
                    Renvoyer vers l'activite d'affichage du contenu d'une offre
                 */
                intent = new Intent(MainActivity.this, OffreActivity.class);
                /* Envoyer l'offre selectionee dans la liste d'offres */
                intent.putExtra("offre",
                        (OffreParcelable) adapterView.getItemAtPosition(position));
                /*
                    Swipe horizontal : Passer la liste d'offres en extra afin de gerer le passage
                    d'offre en offre
                 */
                bundleOffres.putParcelableArrayList("offres", listeDOffres.getOffres());
                intent.putExtras(bundleOffres);
                intent.putExtra("origine", M);
                startActivity(intent);
            }
        });

        /* Ecoute d'evenement sur le Floating Action Button */
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                    Renvoyer vers l'activite de creation d'offre
                 */
                Intent intent = new Intent(MainActivity.this,
                        CreationActivity.class);
                startActivity(intent);
            }
        });

        /* Ecoute d'evenement sur la base de donnee */
        refOffres.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                /*
                    Inserer dans la liste d'offres les offres depuis la base de donnee
                 */
                listeDOffres.getOffres().clear();
                for (DataSnapshot snapshotOffre: snapshot.getChildren()) {
                    listeDOffres.getOffres().add(snapshotOffre.getValue(OffreParcelable.class));
                }
                offreAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Log.w("MainActivity", "Echec de la lecture de donnees :",
                        error.toException());
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_recherche,menu);

        /* Recuperation des objets definis dans le dossier menu que l'on a cree */
        MenuItem myActionMenuItem = menu.findItem(R.id.search) ;
        MenuItem miDeconnexion = menu.findItem(R.id.deconnexion);

        /*
            Controle du jeton de connexion : etat du bouton de deconnexion
         */
        miDeconnexion.setEnabled(auth.getCurrentUser() != null);

        SearchView searchView = (SearchView) myActionMenuItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                /* Application du filtre dans l'adapter sur la chaine s */
                offreAdapter.getFilter().filter(s);

                return true;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.search)
            return true;
        else if (id == R.id.deconnexion) {
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

        return super.onOptionsItemSelected(item);
    }
}