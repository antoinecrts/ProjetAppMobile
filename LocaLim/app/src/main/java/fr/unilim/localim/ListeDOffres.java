package fr.unilim.localim;

import android.content.Context;

import java.util.ArrayList;

public class ListeDOffres {

    protected Context context; /* Context lie à la ListeDOffres */
    protected ArrayList<OffreParcelable> offres; /* liste d'offres */

    public ListeDOffres(Context context, ArrayList<OffreParcelable> offres){
        this.context = context;
        this.offres = offres;
    }


    public Context getContext() {
        return context;
    }

    public ArrayList<OffreParcelable> getOffres() {
        return offres;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setOffres(ArrayList<OffreParcelable> offres) {
        this.offres = offres;
    }
}
