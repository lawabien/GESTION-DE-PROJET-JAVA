-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: localhost
-- Generation Time: Aug 24, 2025 at 01:52 AM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `gestion_projets`
--

-- --------------------------------------------------------

--
-- Table structure for table `Projet`
--

CREATE TABLE `Projet` (
  `id` int(11) NOT NULL,
  `nom` varchar(100) NOT NULL,
  `description` text DEFAULT NULL,
  `date_debut` date DEFAULT NULL,
  `date_fin` date DEFAULT NULL,
  `statut` enum('EN_ATTENTE','EN_COURS','TERMINE','ANNULE') DEFAULT 'EN_ATTENTE',
  `id_utilisateur` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `Projet`
--

INSERT INTO `Projet` (`id`, `nom`, `description`, `date_debut`, `date_fin`, `statut`, `id_utilisateur`) VALUES
(8, 'projet1', 'simulation d\'ajout de projet1', '2025-08-23', '2025-08-26', 'EN_ATTENTE', 12),
(9, 'projet2', 'simulation d\'ajout de projet2', '2025-08-24', '2025-08-26', 'EN_COURS', 13),
(10, 'projet3', 'simulation d\'ajout de projet3', '2025-08-25', '2025-08-26', 'EN_ATTENTE', 14),
(11, 'projet4', 'simulation d\'ajout de projet4', '2025-08-24', '2025-08-25', 'EN_COURS', 14);

-- --------------------------------------------------------

--
-- Table structure for table `Tache`
--

CREATE TABLE `Tache` (
  `id` int(11) NOT NULL,
  `titre` varchar(100) NOT NULL,
  `description` text DEFAULT NULL,
  `statut` enum('A_FAIRE','EN_COURS','TERMINE','BLOQUE') DEFAULT 'A_FAIRE',
  `priorite` enum('BASSE','MOYENNE','HAUTE','CRITIQUE') DEFAULT 'MOYENNE',
  `date_creation` datetime DEFAULT current_timestamp(),
  `date_echeance` date DEFAULT NULL,
  `id_projet` int(11) DEFAULT NULL,
  `id_utilisateur` int(11) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `Tache`
--

INSERT INTO `Tache` (`id`, `titre`, `description`, `statut`, `priorite`, `date_creation`, `date_echeance`, `id_projet`, `id_utilisateur`) VALUES
(7, 'tache du projet1', 'simaulation d\'ajout d\'une tache dans le projet1', 'A_FAIRE', 'MOYENNE', '2025-08-23 00:00:00', '2025-08-26', 8, 12),
(8, 'tache du projet2', 'simulation d\'ajout d\'une tache dans le projet2', 'EN_COURS', 'CRITIQUE', '2025-08-23 00:00:00', '2025-08-24', 9, 13),
(9, 'tache du projet2', 'simulation d\'ajout d\'une tache du projet3', 'TERMINE', 'BASSE', '2025-08-23 00:00:00', '2025-08-25', 10, 14);

-- --------------------------------------------------------

--
-- Table structure for table `Utilisateur`
--

CREATE TABLE `Utilisateur` (
  `id` int(11) NOT NULL,
  `nom` varchar(50) NOT NULL,
  `email` varchar(100) NOT NULL,
  `mot_de_passe` varchar(64) NOT NULL,
  `role` enum('MEMBRE','CHEF_DE_PROJET','ADMINISTRATEUR') DEFAULT 'MEMBRE'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `Utilisateur`
--

INSERT INTO `Utilisateur` (`id`, `nom`, `email`, `mot_de_passe`, `role`) VALUES
(12, 'utilisateur0', 'user0@gmail.com', '60fb80e255eb0a1c9a95a23b86ef179ca68806ed824c698e01c076058c7294fc', 'MEMBRE'),
(13, 'utilisateur1', 'user1@gmail.com', '9cc2b6f91a9aa858e66c44eb6b86b3473d46cfde68378063f16e3ca2feea7a74', 'CHEF_DE_PROJET'),
(14, 'utilsateur2', 'user2@gmail.com', 'd3f89a9fdd6a60b9556d463aac06dc19486b3a55520807ace6b24bf9f22cec0a', 'ADMINISTRATEUR');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `Projet`
--
ALTER TABLE `Projet`
  ADD PRIMARY KEY (`id`),
  ADD KEY `idx_projet_utilisateur` (`id_utilisateur`);

--
-- Indexes for table `Tache`
--
ALTER TABLE `Tache`
  ADD PRIMARY KEY (`id`),
  ADD KEY `id_utilisateur` (`id_utilisateur`),
  ADD KEY `idx_tache_projet` (`id_projet`);

--
-- Indexes for table `Utilisateur`
--
ALTER TABLE `Utilisateur`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `email` (`email`),
  ADD KEY `idx_utilisateur_email` (`email`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `Projet`
--
ALTER TABLE `Projet`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=12;

--
-- AUTO_INCREMENT for table `Tache`
--
ALTER TABLE `Tache`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- AUTO_INCREMENT for table `Utilisateur`
--
ALTER TABLE `Utilisateur`
  MODIFY `id` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=15;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `Projet`
--
ALTER TABLE `Projet`
  ADD CONSTRAINT `Projet_ibfk_1` FOREIGN KEY (`id_utilisateur`) REFERENCES `Utilisateur` (`id`) ON DELETE SET NULL;

--
-- Constraints for table `Tache`
--
ALTER TABLE `Tache`
  ADD CONSTRAINT `Tache_ibfk_1` FOREIGN KEY (`id_projet`) REFERENCES `Projet` (`id`) ON DELETE CASCADE,
  ADD CONSTRAINT `Tache_ibfk_2` FOREIGN KEY (`id_utilisateur`) REFERENCES `Utilisateur` (`id`) ON DELETE SET NULL;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
