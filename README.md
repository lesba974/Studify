# Studify 

Application mobile Android de gestion de routines étudiantes avec déclencheurs contextuels intelligents.

## Description

Studify est une application Android développée en Kotlin avec Jetpack Compose qui aide les étudiants à gérer leur vie quotidienne grâce à des routines personnalisées. L'application utilise des déclencheurs contextuels basés sur la localisation, l'heure, l'activité physique et les conditions météorologiques pour fournir des rappels automatiques au moment opportun.

## Fonctionnalités

### Gestion des routines
- Création, modification et suppression de routines
- Catégories personnalisées 
- Niveaux de priorité avec indicateur visuel
- Périodicité flexible 
- Gestion des horaires 
- Localisation associée à chaque routine
- Duplication de routines existantes

### Affichage intelligent
- Page d'accueil avec liste des routines actives
- Onglet "Archivées" pour consulter l'historique
- Calcul automatique de la prochaine occurrence
- Mise à jour automatique des dates passées 
- Indicateurs visuels de priorité 
- Badges de catégorie avec codes couleur

### Interface utilisateur
- Design moderne avec thème sombre
- Navigation fluide entre les écrans
- Sélecteur de date avec calendrier visuel
- Formulaires de saisie intuitifs
- Messages d'erreur contextuels

### Base de données
- Stockage local avec Room Database
- Persistance des données entre sessions

## Architecture

### Stack technique
- **Langage :** Kotlin
- **Framework UI :** Jetpack Compose avec Material Design 3
- **Architecture :** MVVM (Model-View-ViewModel)
- **Base de données :** Room Database
- **Navigation:** Jetpack Navigation Compose
- **Gestion d'état :** StateFlow et Compose State
- **Min SDK :** API 29 
- **Target SDK :** API 36 
- **IDE :** Android Studio


## Installation

1. Clonez le repository:
```bash
git clone https://github.com/lesba974/Studify.git
```

2. Ouvrez le projet dans Android Studio

3. Synchronisez les dépendances Gradle

4. Lancez l'application sur un émulateur ou appareil physique (API 29+)


## Équipe

Fonseca Iliann
Lesbarrères Emma
Pedro Priscillya
Slimani Fairouz

## Licence

Ce projet est réalisé dans le cadre du cours **8INF257 - Informatique Mobile** à l'UQAC (Hiver 2026).