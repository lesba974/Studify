# Studify 

Application mobile Android de gestion de routines académiques avec générateur automatique et déclencheurs contextuels intelligents.


## Description

Studify est une application Android développée en Kotlin avec Jetpack Compose qui transforme la planification académique en un processus automatique et intelligent. L'application génère automatiquement des routines de cours et des plans de révision adaptatifs, tout en utilisant des notifications contextuelles basées sur la localisation et l'heure pour rappeler les routines au moment opportun.

## Fonctionnalités principales

### Générateur automatique de routines académiques

**Import d'emploi du temps**
- Création de cours avec jour, horaires, localisation et heure de révision préférée
- Génération automatique de 2 routines : cours hebdomadaire + révision hebdomadaire
- Personnalisation de l'heure de révision selon les disponibilités de l'étudiant

**Gestion intelligente des examens**
- Plans de révision adaptatifs selon le temps disponible :
  - **Plan 1 semaine (INTENSIF)** : 21 sessions sur 7 jours (3 sessions/jour × 90 min)
  - **Plan 2 semaines (ÉQUILIBRÉ)** : Progressif (1/jour puis 2/jour avec intensité croissante)
  - **Plan 3 semaines (RELAXE)** : 21 sessions réparties (1 session/jour × 60 min)
- Validation intelligente des plans selon la date de l'examen
- Avertissement automatique si examen dans moins de 7 jours
- Évitement automatique des conflits horaires avec les cours existants

**Cohérence des données**
- Mise à jour automatique de toutes les routines lors de la modification d'un cours
- Suppression en cascade : supprimer un cours supprime l'examen et toutes ses révisions
- Préservation de l'intégrité référentielle

### Déclenchement contextuel

**Notifications temporelles**
- Rappels programmés à l'heure exacte de chaque routine
- Format clair : "Titre de la routine - Description"
- Intégration avec le système de notifications Android

**Geofencing (Notifications géolocalisées)**
- Zone GPS de 150 mètres autour d'une adresse configurée automatiquement
- Géocodage automatique des adresses en coordonnées GPS
- Notification contextuelle à l'arrivée : "📍 Tu arrives à [lieu]"
- Gestion des permissions de localisation (fine, coarse)

### Gestion des routines

**CRUD complet**
- Création, modification, suppression et duplication de routines
- Catégories : Cours, Révision, Sport, Loisir, Travail, Santé
- Priorités : Élevée, Moyenne, Faible (avec indicateurs de couleur)
- Périodicité : Une fois, Quotidienne, Hebdomadaire, Mensuelle
- Horaires de début et fin avec calcul automatique de la durée
- Localisation avec géocodage pour le geofencing

**Archivage intelligent**
- Séparation automatique entre routines actives et archivées
- Onglets "Actives" / "Archivées" pour navigation facile
- Vérification date ET heure pour éviter l'archivage prématuré
- Mise à jour automatique des routines récurrentes

### Interface utilisateur

**Design moderne**
- Thème sombre avec palette cohérente
- Material Design 3 avec Jetpack Compose
- Composants réutilisables uniformisés
- Animations fluides et transitions naturelles

**Composants spécialisés**
- TimePickerField avec Material TimePicker
- DatePickerField avec blocage des dates passées
- ExposedDropdownMenuBox avec animations
- Cartes d'information de taille égale (InfoItem)
- Messages d'erreur stylisés (Snackbar rouge)

**Navigation intuitive**
- Menu hamburger pour accès rapide aux fonctionnalités académiques
- Navigation entre écrans avec transitions
- Bouton d'action flottant pour création rapide
- Prévisualisation des routines avant création

## Architecture

### Stack technique

| Composant | Technologie |
|-----------|-------------|
| **Langage** | Kotlin |
| **UI Framework** | Jetpack Compose + Material Design 3 |
| **Architecture** | MVVM (Model-View-ViewModel) |
| **Base de données** | Room Database (SQLite) |
| **Navigation** | Jetpack Navigation Compose |
| **Async** | Coroutines + Flow/StateFlow |
| **Services** | Google Play Services (Geofencing) |
| **Permissions** | Activity Result API |
| **Notifications** | AlarmManager + PendingIntent |
| **Min SDK** | API 29 (Android 10) |
| **Target SDK** | API 36 (Android 14+) |

## Installation et utilisation

### Prérequis

- Android Studio Hedgehog (2023.1.1) ou plus récent
- JDK 17 ou supérieur
- Android SDK avec API 29 minimum
- Émulateur Android ou appareil physique (Android 10+)

### Installation

1. **Clonez le repository**
```bash
git clone https://github.com/lesba974/Studify.git
cd Studify
```

2. **Ouvrez dans Android Studio**

3. **Synchronisez les dépendances Gradle**

4. **Configurez l'émulateur**
- Créez un AVD avec API 29+ (recommandé: Pixel 5, API 34)
- Activez la localisation dans les paramètres de l'émulateur

5. **Lancez l'application**

### Permissions nécessaires

L'application demande les permissions suivantes :
- `ACCESS_FINE_LOCATION` - Pour le geofencing précis
- `ACCESS_COARSE_LOCATION` - Pour la localisation approximative
- `POST_NOTIFICATIONS` - Pour les rappels (Android 13+)
- `SCHEDULE_EXACT_ALARM` - Pour les notifications à l'heure exacte

## Tests

### Tests unitaires

Le projet comprend 22 tests unitaires couvrant la logique métier essentielle :


**Couverture des tests :**
- Validation des plans de révision (5 tests)
- Détection de conflits horaires (5 tests)
- Archivage intelligent des routines (5 tests)
- Calculs de durée et formatage (5 tests)
- État de l'interface (2 tests)

## Contribution

Ce projet est un travail académique. Les contributions externes ne sont pas acceptées.

### Équipe de développement

- Fonseca Iliann
- Lesbarrères Emma
- Pedro Priscillya
- Slimani Fairouz

## Licence

Ce projet est réalisé dans le cadre du cours **8INF257 - Informatique Mobile** à l'UQAC (Hiver 2026).

© 2026 Groupe 13 - Tous droits réservés à des fins académiques.