LocaLim est une application communautaire de partage d'offres et services,
dans l'esprit du site Le Bon Coin, réalisé dans le cadre d'un projet
universitaire, à l'Université de Limoges.

#

Arborescence descendante de l'application, selon les actions effectuées par
l'utilisateur :

    MainActivity : activité d'affichage de la liste d'offres
    |
    | -> bouton fab
    |
    +-- CreationActivity : activité de création d'une offre
    |   |
    |   | -> si non authentifié
    |   |
    |   +-- ConnexionActivity : activité de connexion à un compte utilisateur
    |       |
    |       | -> bouton S'Enregistrer
    |       |
    |       +-- EnregistrementActivity : activité de création de compte utilisateur
    |       |   |
    |       |   | -> connexion / enregistrement de l'utilisateur
    |       |   |
    |       +---+-- CreationActivity
    |
    | -> sélection d'une offre dans la liste
    |
    +-- OffreActivity : activité d'affichage du contenu d'une offre
