🚀 ProjetManager - Application de Gestion de Projets
📋 Table des matières

    1] Description

    2] Fonctionnalités

    3] Architecture technique

    4] Prérequis

    5] Installation

    6] Configuration

    7] Utilisation

    8] Structure de la base de données

    9] Dépannage

    10] Développement


📖 Description

ProjetManager est une application desktop Java Swing complète pour la gestion de projets, tâches et utilisateurs. Elle permet aux chefs de projet et aux équipes de suivre l'avancement des projets, assigner des tâches et gérer les ressources humaines.
🌟 Fonctionnalités
👥 Gestion des Utilisateurs

    ✅ Création, modification et suppression d'utilisateurs

    ✅ Trois rôles : Membre, Chef de Projet, Administrateur

    ✅ Système d'authentification sécurisé avec mots de passe hashés

    ✅ Profils utilisateur avec attribution de projets

📊 Gestion des Projets

    ✅ Création de projets avec dates de début/fin

    ✅ Quatre statuts : En attente, En cours, Terminé, Annulé

    ✅ Attribution d'un chef de projet

    ✅ Description détaillée et suivi temporel

✅ Gestion des Tâches

    ✅ Création de tâches assignées à des projets

    ✅ Quatre statuts : À faire, En cours, Terminé, Bloqué

    ✅ Quatre niveaux de priorité : Basse, Moyenne, Haute, Critique

    ✅ Dates d'échéance et d'affectation

    ✅ Filtrage par projet et statut

📁 Fonctionnalités Avancées

    ✅ Interface graphique intuitive avec onglets

    ✅ Export CSV des projets (pas implementer dans le code)

    ✅ Recherche et filtrage avancé

    ✅ Double-clic pour éditer rapidement

    ✅ Validation des données en temps réel






🏗️ Architecture Technique
Stack Technologique

    []Langage : Java 11+

    []Interface : Java Swing

    []Base de données : MySQL 8.0+

    []Connexion BD : JDBC avec pool DBCP2

    []Gestion de projet : Maven



📋 Prérequis
Logiciels Requis

    Java : JDK 11 ou supérieur

    MySQL : Serveur 8.0 ou supérieur pour cote base de donnees

    Maven : 3.6+ (pour la compilation)

    Git : Pour le clonage du dépôt

Configuration Système

    RAM : 4 GB minimum (8 GB recommandé)

    Disque : 500 MB d'espace libre


🛠️ Installation
1. Cloner le projet
 ----->   https://github.com/lawabien/GESTION-DE-PROJET-JAVA.git
---------> cd 'GESTION-DE-PROJET-JAVA'


🖥️ Utilisation
Premier Lancement

    Démarrage de l'application : Exécutez ProjetManager.java

    Création du compte admin : L'application propose de créer un admin si aucun n'existe

    Connexion : Utilisez les identifiants par défaut ou créez un nouveau compte

[--] ensuite dans la page qui va suivre:

    Créer des utilisateurs dans l'onglet "Utilisateurs"

    Créer un projet dans l'onglet "Projets"

    Assigner des tâches dans l'onglet "Tâches"

    Suivre l'avancement via les tableaux de bord



🗃️ Structure de la Base de Données
Tables Principales:

-- Table Utilisateur
CREATE TABLE Utilisateur (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(50) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    mot_de_passe VARCHAR(64) NOT NULL,
    role ENUM('MEMBRE', 'CHEF_DE_PROJET', 'ADMINISTRATEUR')
);

-- Table Projet
CREATE TABLE Projet (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(100) NOT NULL,
    description TEXT,
    date_debut DATE,
    date_fin DATE,
    statut ENUM('EN_ATTENTE', 'EN_COURS', 'TERMINE', 'ANNULE'),
    id_utilisateur INT
);

-- Table Tache
CREATE TABLE Tache (
    id INT AUTO_INCREMENT PRIMARY KEY,
    titre VARCHAR(100) NOT NULL,
    description TEXT,
    statut ENUM('A_FAIRE', 'EN_COURS', 'TERMINE', 'BLOQUE'),
    priorite ENUM('BASSE', 'MOYENNE', 'HAUTE', 'CRITIQUE'),
    date_creation DATETIME,
    date_echeance DATE,
    id_projet INT,
    id_utilisateur INT
);







Contact Développement et informations

    Email : lawabiens@gmail.com
    Version : 1.0
    Dernière mise à jour : 2025
    Auteur : lawabien judicael
    Statut : Fonctionnel !
    
