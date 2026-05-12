# AutoDrive

AutoDrive est une application Android native en Kotlin pour la location de voitures. Elle permet a un utilisateur de consulter des voitures, filtrer le catalogue, reserver un vehicule, consulter ses reservations, gerer son profil et laisser des evaluations. Un espace administrateur permet aussi de gerer les voitures disponibles.

## Membres de l'equipe

- Bafing Keita
- Nzogang Kamte Myguel Wilfried
- Wilkins Saintil

## Fonctionnalites principales

- Connexion simple d'un utilisateur avec sauvegarde de session.
- Consultation d'une liste de voitures.
- Recherche et filtrage par marque, modele, annee, prix et disponibilite.
- Consultation du detail d'une voiture avec images, description et evaluations.
- Reservation d'une voiture avec calcul du cout total.
- Detection des conflits de reservation.
- Consultation de l'historique des reservations.
- Annulation des reservations actives.
- Gestion du profil utilisateur et de preferences locales.
- Ajout et modification d'evaluations.
- Espace administrateur pour ajouter, modifier et supprimer des voitures.

## Architecture MVP

Le projet suit une architecture MVP de facon incrementale :

- **Model** : entites Room, DAO et repositories dans `app/src/main/java/com/example/autodrive/model`.
- **View** : Activities et ecrans Compose qui affichent les donnees et transmettent les evenements utilisateur.
- **Presenter** : classes dans `app/src/main/java/com/example/autodrive/presenter` qui orchestrent les appels entre la View et le Model.
- **Contracts** : interfaces dans `app/src/main/java/com/example/autodrive/presenter/contract`.

Les presenters existants couvrent les voitures, le catalogue client, les reservations et l'historique des reservations. Les ecrans doivent appeler les fonctions des presenters pour la logique metier importante, afin que les Activities restent concentrees sur l'affichage, la navigation et la capture des evenements utilisateur.

## Base de donnees SQLite / Room

La persistance principale utilise Room, qui repose sur SQLite.

Tables principales :

- `voiture` : informations des voitures disponibles a la location.
- `utilisateur` : informations des utilisateurs.
- `reservation` : reservations des utilisateurs, reliees aux voitures.
- `evaluation` : avis et notes des utilisateurs sur les voitures.

La base de donnees est declaree dans `AppDatabase.kt`. Les acces aux donnees passent par les DAO et les repositories.

## Installation

Prerequis :

- Android Studio recent.
- JDK compatible avec Gradle Android.
- SDK Android installe.

Etapes :

1. Cloner le depot.
2. Ouvrir le dossier du projet dans Android Studio.
3. Laisser Gradle synchroniser les dependances.
4. Verifier que le SDK Android requis est installe.

## Execution

Depuis Android Studio :

1. Selectionner un emulateur ou un appareil Android.
2. Lancer la configuration `app`.

Depuis un terminal :

```powershell
.\gradlew.bat assembleDebug
```

Puis installer l'APK debug sur un appareil ou un emulateur.

## Tests

Lancer les tests unitaires :

```powershell
.\gradlew.bat testDebugUnitTest
```

Lancer les tests instrumentes Android :

```powershell
.\gradlew.bat connectedDebugAndroidTest
```

Les tests unitaires visent la logique des presenters et des repositories avec des objets factices pour isoler les composants.

## Repartition des taches

- **Bafing Keita** : integration, merges, structure generale du projet et suivi de conformite.
- **Nzogang Kamte Myguel Wilfried** : profil utilisateur, session et parcours utilisateur.
- **Wilkins Saintil** : evaluations, historique et ameliorations fonctionnelles.

Cette repartition doit rester coherente avec l'historique Git et les contributions finales de chaque membre.

## Limites connues

- Certaines parties de l'application doivent encore etre renforcees pour respecter strictement MVP.
- Les tests bout-en-bout restent a etendre pour couvrir tous les parcours principaux.
- La gestion des migrations Room doit etre verifiee avant une remise finale.
- Certains textes de l'interface peuvent necessiter une correction d'encodage.
- Le rapport d'utilisation de l'IA doit etre ajoute separement.
