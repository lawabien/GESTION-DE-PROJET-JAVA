package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import model.Utilisateur;
import controller.ProjetController;

public class UtilisateurPanel extends JPanel {
    private ProjetController controller;
    private JTable utilisateurTable;
    private DefaultTableModel tableModel;
    
    public UtilisateurPanel() {
        controller = new ProjetController();
        setLayout(new BorderLayout());
        initTable();
        initToolbar();
        chargerUtilisateurs();
    }
    
    private void initTable() {
        String[] columns = {"ID", "Nom", "Email", "Rôle"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        utilisateurTable = new JTable(tableModel);
        utilisateurTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        utilisateurTable.setAutoCreateRowSorter(true);
        utilisateurTable.setRowHeight(25);
        
        utilisateurTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editerUtilisateur();
                }
            }
        });
        
        add(new JScrollPane(utilisateurTable), BorderLayout.CENTER);
    }
    
    private void initToolbar() {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        
        JButton addButton = new JButton("Ajouter");
        addButton.addActionListener(e -> ajouterUtilisateur());
        
        JButton editButton = new JButton("Modifier");
        editButton.addActionListener(e -> editerUtilisateur());
        
        JButton deleteButton = new JButton("Supprimer");
        deleteButton.addActionListener(e -> supprimerUtilisateur());
        
        JButton refreshButton = new JButton("Actualiser");
        refreshButton.addActionListener(e -> chargerUtilisateurs());
        
        toolBar.add(addButton);
        toolBar.add(editButton);
        toolBar.add(deleteButton);
        toolBar.addSeparator();
        toolBar.add(refreshButton);
        
        add(toolBar, BorderLayout.NORTH); // Changé de SOUTH à NORTH pour une meilleure disposition
    }
    
    private void chargerUtilisateurs() {
        tableModel.setRowCount(0);
        
        List<Utilisateur> utilisateurs = controller.listerUtilisateurs();
        for (Utilisateur u : utilisateurs) {
            Object[] row = {
                u.getId(),
                u.getNom(),
                u.getEmail(),
                u.getRole().toString()
            };
            tableModel.addRow(row);
        }
    }
    
    private void ajouterUtilisateur() {
        JDialog dialog = new JDialog();
        dialog.setTitle("Nouvel Utilisateur");
        dialog.setSize(400, 300);
        dialog.setModal(true);
        dialog.setLocationRelativeTo(this);
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Champs du formulaire
        JTextField nomField = new JTextField(20);
        JTextField emailField = new JTextField(20);
        JPasswordField passwordField = new JPasswordField(20);
        JComboBox<Utilisateur.Role> roleCombo = new JComboBox<>(Utilisateur.Role.values());
        
        // Ajout des composants
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Nom:"), gbc);
        gbc.gridx = 1;
        formPanel.add(nomField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        formPanel.add(emailField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Mot de passe:"), gbc);
        gbc.gridx = 1;
        formPanel.add(passwordField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Rôle:"), gbc);
        gbc.gridx = 1;
        formPanel.add(roleCombo, gbc);
        
        // Bouton Enregistrer
        JButton saveButton = new JButton("Enregistrer");
        saveButton.addActionListener(e -> {
            if (nomField.getText().trim().isEmpty() || 
                emailField.getText().trim().isEmpty() || 
                passwordField.getPassword().length == 0) {
                
                JOptionPane.showMessageDialog(dialog, 
                    "Tous les champs sont obligatoires", 
                    "Erreur", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try {
                Utilisateur nouvelUtilisateur = new Utilisateur();
                nouvelUtilisateur.setNom(nomField.getText());
                nouvelUtilisateur.setEmail(emailField.getText());
                nouvelUtilisateur.setMotDePasse(new String(passwordField.getPassword()));
                nouvelUtilisateur.setRole((Utilisateur.Role)roleCombo.getSelectedItem());
                
                if (controller.ajouterUtilisateur(nouvelUtilisateur)) {
                    chargerUtilisateurs();
                    dialog.dispose();
                    JOptionPane.showMessageDialog(this,
                        "Utilisateur ajouté avec succès!",
                        "Succès",
                        JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog,
                    "Erreur lors de l'ajout: " + ex.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(saveButton, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    
    private void editerUtilisateur() {
        int selectedRow = utilisateurTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, 
                "Veuillez sélectionner un utilisateur", 
                "Erreur", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int id = (int) tableModel.getValueAt(selectedRow, 0);
        Utilisateur utilisateur = controller.getUtilisateurById(id);
        if (utilisateur == null) {
            JOptionPane.showMessageDialog(this,
                "Utilisateur introuvable",
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        JDialog dialog = new JDialog();
        dialog.setTitle("Modifier Utilisateur");
        dialog.setSize(400, 300);
        dialog.setModal(true);
        dialog.setLocationRelativeTo(this);
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Champs pré-remplis
        JTextField nomField = new JTextField(utilisateur.getNom(), 20);
        JTextField emailField = new JTextField(utilisateur.getEmail(), 20);
        JPasswordField passwordField = new JPasswordField(20);
        passwordField.setText(""); // Ne pas afficher le mot de passe actuel
        JComboBox<Utilisateur.Role> roleCombo = new JComboBox<>(Utilisateur.Role.values());
        roleCombo.setSelectedItem(utilisateur.getRole());
        
        // Ajout des composants
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Nom:"), gbc);
        gbc.gridx = 1;
        formPanel.add(nomField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        formPanel.add(emailField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        formPanel.add(new JLabel("Nouveau mot de passe:"), gbc);
        gbc.gridx = 1;
        formPanel.add(passwordField, gbc);
        formPanel.add(new JLabel("(laisser vide pour ne pas changer)"), gbc);
        
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Rôle:"), gbc);
        gbc.gridx = 1;
        formPanel.add(roleCombo, gbc);
        
        // Bouton Enregistrer
        JButton saveButton = new JButton("Enregistrer");
        saveButton.addActionListener(e -> {
            if (nomField.getText().trim().isEmpty() || 
                emailField.getText().trim().isEmpty()) {
                
                JOptionPane.showMessageDialog(dialog, 
                    "Le nom et l'email sont obligatoires", 
                    "Erreur", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try {
                utilisateur.setNom(nomField.getText());
                utilisateur.setEmail(emailField.getText());
                
                // Ne changer le mot de passe que si un nouveau a été saisi
                if (passwordField.getPassword().length > 0) {
                    utilisateur.setMotDePasse(new String(passwordField.getPassword()));
                }
                
                utilisateur.setRole((Utilisateur.Role)roleCombo.getSelectedItem());
                
                if (controller.modifierUtilisateur(utilisateur)) {
                    chargerUtilisateurs();
                    dialog.dispose();
                    JOptionPane.showMessageDialog(this,
                        "Utilisateur modifié avec succès!",
                        "Succès",
                        JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog,
                    "Erreur lors de la modification: " + ex.getMessage(),
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(saveButton, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    
    private void supprimerUtilisateur() {
        int selectedRow = utilisateurTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, 
                "Veuillez sélectionner un utilisateur", 
                "Erreur", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int id = (int) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Êtes-vous sûr de vouloir supprimer cet utilisateur?\nCette action est irréversible.", 
            "Confirmation", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (controller.supprimerUtilisateur(id)) {
                chargerUtilisateurs();
                JOptionPane.showMessageDialog(this,
                    "Utilisateur supprimé avec succès",
                    "Succès",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
}