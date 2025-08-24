package model;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

public class Utilisateur {
    private int id;
    private String nom;
    private String email;
    private String motDePasse; // ici va Stocker le mot de passe hashé
    private Role role;

    public enum Role {
        MEMBRE,
        CHEF_DE_PROJET, 
        ADMINISTRATEUR;

        public static Role fromString(String value) {
            if (value == null) return MEMBRE;
            try {
                return Role.valueOf(value.toUpperCase());
            } catch (IllegalArgumentException e) {
                return MEMBRE;
            }
        }
    }

    // Constructeurs
    public Utilisateur() {
    }

    public Utilisateur(String nom, String email, String motDePasse, Role role) {
        this.nom = nom;
        this.email = email;
        this.setMotDePasse(motDePasse); // Utilisation du setter pour hasher
        this.role = role;
    }

    public Utilisateur(int id, String nom, String email, String motDePasse, Role role) {
        this(nom, email, motDePasse, role);
        this.id = id;
    }

    // Getters et setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return Le mot de passe hashé (ne doit jamais être null)
     */
    public String getMotDePasse() {
        return motDePasse != null ? motDePasse : "";
    }

    /**
     * Hash et stocke le mot de passe
     * @param motDePasse Le mot de passe en clair
     */
    public void setMotDePasse(String motDePasse) {
        this.motDePasse = hashPassword(motDePasse);
    }

    public Role getRole() {
        return role != null ? role : Role.MEMBRE;
    }

    public void setRole(Role role) {
        this.role = role != null ? role : Role.MEMBRE;
    }

    // Méthodes de hachage
    private String hashPassword(String password) {
        if (password == null || password.isEmpty()) {
            return "";
        }
        
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hashedBytes = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedBytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Erreur de hashage du mot de passe", e);
        }
    }

    /**
     * Vérifie si le mot de passe en clair correspond au hash stocké
     * @param motDePasseClair Le mot de passe en clair à vérifier
     * @return true si le mot de passe correspond
     */
    public boolean verifierMotDePasse(String motDePasseClair) {
        return this.getMotDePasse().equals(hashPassword(motDePasseClair));
    }

    // Equals et hashCode
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Utilisateur that = (Utilisateur) o;
        return id == that.id && 
               Objects.equals(email, that.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, email);
    }

    @Override
    public String toString() {
        return String.format("%s (ID: %d, Email: %s, Rôle: %s)", 
               nom, id, email, role);
    }
}