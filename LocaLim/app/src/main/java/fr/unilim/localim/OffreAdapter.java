package fr.unilim.localim;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class OffreAdapter extends ArrayAdapter {
    private ArrayList<OffreParcelable> offres; /* offres de la liste d'offres */
    private ArrayList<OffreParcelable> offresFiltrees; /* ArrayList filtree d'offres */

    protected FirebaseStorage storage; /* instance Cloud Storage */

    public OffreAdapter(Context context, ArrayList<OffreParcelable> offres) {
        super(context, 0, offres);

        storage = FirebaseStorage.getInstance();

        offresFiltrees = offres;
        this.offres = offres;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        ImageView ivImage;
        TextView tvTitre, tvLieu;
        OffreParcelable offre; /* offre courante dans la ListView */
        /* reference de l'image de l'offre dans le Cloud Storage */
        StorageReference emplacementImage;

        /* Créer le layout des lignes de la ListView */
        if(convertView == null)
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.lig_liste_d_offres,
                    parent, false);

        offre = (OffreParcelable) getItem(position);

        /* Recuperer l'image de l'offre sur le cloud storage */
        if(!offre.getCheminImage().isEmpty()) {
            /*
                Une image a ete definie par l'utilisateur
             */
            ivImage = convertView.findViewById(R.id.image);
            emplacementImage = storage.getReference(offre.getCheminImage());
            Glide.with(getContext()).using(new FirebaseImageLoader()).load(emplacementImage)
                    .into(ivImage);
        }

        /*
            Initialiser les elements du layout
         */
        tvTitre = convertView.findViewById(R.id.titre);
        tvLieu = convertView.findViewById(R.id.lieu);

        /*
            Modifier le contenu du layout
         */
        tvTitre.setText(offre.getTitre());
        tvLieu.setText(offre.getLieu());

        return convertView;
    }

    @Override
    public int getCount() {
        return offresFiltrees.size();
    }

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {

                FilterResults filterResults = new FilterResults();
                /* on compare la sequence de caractère */
                if (charSequence == null || charSequence.length() == 0){
                    filterResults.count = offres.size();
                    filterResults.values = offres;
                }
                else {
                    String searchStr = charSequence.toString().toLowerCase();

                    ArrayList<OffreParcelable> result = new ArrayList<>() ;

                    for (OffreParcelable offre : offres) {
                        /* recherche à partir du titre et du lieu */
                        if(offre.getTitre().toLowerCase().contains(searchStr) || offre.getLieu().toLowerCase().contains(searchStr) ) {
                            result.add(offre);
                        }

                        filterResults.count = result.size();
                        filterResults.values = result;
                    }

                }

                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                offresFiltrees = (ArrayList<OffreParcelable>) filterResults.values ;
            }
        };

        return filter;
    }
}
