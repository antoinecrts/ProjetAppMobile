package fr.unilim.localim;

import android.os.Parcel;
import android.os.Parcelable;

public class OffreParcelable extends Offre implements Parcelable {
    public static final Parcelable.Creator<OffreParcelable> CREATOR =
            new Parcelable.Creator<OffreParcelable>(){
                public OffreParcelable createFromParcel(Parcel in){
                    return new OffreParcelable(in);
                }

                public OffreParcelable[] newArray(int size){
                    return new OffreParcelable[size];
                }
            };

    public OffreParcelable(){
        super();
    }

    public OffreParcelable(String titre, String cheminImage, String description, String lieu){
        super(titre, cheminImage, description, lieu);
    }

    private OffreParcelable(Parcel in){
        super(in.readString(),
              in.readString(),
              in.readString(),
              in.readString());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(titre);
        dest.writeString(cheminImage);
        dest.writeString(description);
        dest.writeString(lieu);
    }
}
