package fr.unilim.localim;

public class Offre {
    protected String titre; /* titre de l'offre */
    protected String cheminImage; /* chemin de l'image sur le Cloud Storage */
    protected String description; /* description de l'offre */
    protected String lieu; /* lieu de l'offre */

    public Offre(){
        titre = "";
        cheminImage = "";
        description = "";
        lieu = "";
    }

    public Offre(String titre, String cheminImage, String description, String lieu){
        this.titre = titre;
        this.cheminImage = cheminImage;
        this.description = description;
        this.lieu = lieu;
    }

    public String getTitre() {
        return titre;
    }

    public String getDescription() {
        return description;
    }

    public String getCheminImage() {
        return cheminImage;
    }

    public String getLieu() {
        return lieu;
    }

    public void setTitre(String titre) {
        this.titre = titre;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setCheminImage(String cheminImage) {
        this.cheminImage = cheminImage;
    }

    public void setLieu(String lieu) {
        this.lieu = lieu;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        Offre other = (Offre) obj;

        if (titre == null) {
            if (other.titre != null)
                return false;
        } else if (!titre.equals(other.titre))
            return false;

        if (description == null) {
            if (other.description != null)
                return false;
        } else if (!description.equals(other.description))
            return false;

        if (cheminImage == null) {
            if (other.cheminImage != null)
                return false;
        } else if (!cheminImage.equals(other.cheminImage))
            return false;

        if (lieu == null) {
            if (other.lieu != null)
                return false;
        } else if (!lieu.equals(other.lieu))
            return false;

        return true;
    }
}