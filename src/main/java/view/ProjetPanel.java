package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.Date;
import java.text.SimpleDateFormat;
import model.Projet;
import model.Utilisateur;
import controller.ProjetController;

public class ProjetPanel extends JPanel {
    private ProjetController controller;
    private JTable projetTable;
    private DefaultTableModel tableModel;
    private JComboBox<Utilisateur> responsableCombo;
    private JComboBox<String> statutCombo;
    
    public ProjetPanel() {
        controller = new ProjetController();
        setLayout(new BorderLayout());
        initTable();
        initToolbar();
        initFilterPanel();
        chargerProjets();
        chargerResponsables();
    }
    
    private void initTable() {
        String[] columns = {"ID", "Nom", "Description", "Début", "Fin", "Statut", "Responsable"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        projetTable = new JTable(tableModel);
        projetTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        projetTable.setAutoCreateRowSorter(true);
        projetTable.setRowHeight(25);
        
        projetTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editerProjet();
                }
            }
        });
        
        add(new JScrollPane(projetTable), BorderLayout.CENTER);
    }
    
    private void initToolbar() {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        
        JButton addButton = new JButton("Ajouter");
        addButton.addActionListener(e -> ajouterProjet());
        
        JButton editButton = new JButton("Modifier");
        editButton.addActionListener(e -> editerProjet());
        
        JButton deleteButton = new JButton("Supprimer");
        deleteButton.addActionListener(e -> supprimerProjet());
        
        JButton refreshButton = new JButton("Actualiser");
        refreshButton.addActionListener(e -> {
            chargerResponsables();
            chargerProjets();
        });
        
        toolBar.add(addButton);
        toolBar.add(editButton);
        toolBar.add(deleteButton);
        toolBar.addSeparator();
        toolBar.add(refreshButton);
        
        add(toolBar, BorderLayout.NORTH);
    }
    
    private void initFilterPanel() {
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        
        // Filtre par statut
        statutCombo = new JComboBox<>(new String[]{"Tous", "En attente", "En cours", "Terminé", "Annulé"});
        statutCombo.addActionListener(e -> filtrerProjets());
        
        // Filtre par responsable
        responsableCombo = new JComboBox<>();
        responsableCombo.addActionListener(e -> filtrerProjets());
        
        filterPanel.add(new JLabel("Statut:"));
        filterPanel.add(statutCombo);
        filterPanel.add(new JLabel("Responsable:"));
        filterPanel.add(responsableCombo);
        
        add(filterPanel, BorderLayout.SOUTH);
    }
    
    private void chargerResponsables() {
        responsableCombo.removeAllItems();
        
        // Ajouter l'option "Tous"
        Utilisateur tous = new Utilisateur();
        tous.setId(0);
        tous.setNom("Tous");
        responsableCombo.addItem(tous);
        
        // Charger les vrais responsables
        List<Utilisateur> responsables = controller.listerUtilisateurs();
        if (responsables.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Aucun responsable disponible. Veuillez d'abord créer des utilisateurs.",
                "Avertissement", 
                JOptionPane.WARNING_MESSAGE);
        } else {
            responsables.forEach(responsableCombo::addItem);
        }
    }
    
    private void chargerProjets() {
        tableModel.setRowCount(0);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        
        List<Projet> projets = controller.listerProjets();
        projets.forEach(p -> {
            Utilisateur responsable = controller.getUtilisateurById(p.getIdUtilisateur());
            String nomResponsable = (responsable != null) ? responsable.getNom() : "Inconnu";
            
            Object[] row = {
                p.getId(),
                p.getNom(),
                p.getDescription().length() > 30 ? 
                    p.getDescription().substring(0, 30) + "..." : p.getDescription(),
                sdf.format(p.getDateDebut()),
                sdf.format(p.getDateFin()),
                p.getStatut().toString(),
                nomResponsable
            };
            tableModel.addRow(row);
        });
    }
    
    private void filtrerProjets() {
        String statut = statutCombo.getSelectedIndex() == 0 ? null : statutCombo.getSelectedItem().toString();
        Utilisateur selectedResponsable = (Utilisateur)responsableCombo.getSelectedItem();
        int idResponsable = (selectedResponsable != null) ? selectedResponsable.getId() : 0;
        
        tableModel.setRowCount(0);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        
        controller.listerProjets().stream()
            .filter(p -> statut == null || p.getStatut().toString().equals(statut))
            .filter(p -> idResponsable == 0 || p.getIdUtilisateur() == idResponsable)
            .forEach(p -> {
                Utilisateur responsable = controller.getUtilisateurById(p.getIdUtilisateur());
                String nomResponsable = (responsable != null) ? responsable.getNom() : "Inconnu";
                
                Object[] row = {
                    p.getId(),
                    p.getNom(),
                    p.getDescription().length() > 30 ? 
                        p.getDescription().substring(0, 30) + "..." : p.getDescription(),
                    sdf.format(p.getDateDebut()),
                    sdf.format(p.getDateFin()),
                    p.getStatut().toString(),
                    nomResponsable
                };
                tableModel.addRow(row);
            });
    }
    
    private void ajouterProjet() {
        JDialog dialog = new JDialog();
        dialog.setTitle("Nouveau Projet");
        dialog.setSize(500, 450);
        dialog.setModal(true);
        dialog.setLocationRelativeTo(this);
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Champs du formulaire
        JTextField nomField = new JTextField(20);
        JTextArea descriptionArea = new JTextArea(5, 20);
        JScrollPane descriptionScroll = new JScrollPane(descriptionArea);
        JDatePicker debutPicker = new JDatePicker();
        JDatePicker finPicker = new JDatePicker();
        JComboBox<String> statutCombo = new JComboBox<>(
            new String[]{"En attente", "En cours", "Terminé", "Annulé"});
        
        // Combo box des responsables
        JComboBox<Utilisateur> responsableCombo = new JComboBox<>();
        List<Utilisateur> responsables = controller.listerUtilisateurs();
        if (responsables.isEmpty()) {
            JOptionPane.showMessageDialog(dialog,
                "Aucun responsable disponible. Veuillez d'abord créer des utilisateurs.",
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
            dialog.dispose();
            return;
        }
        responsables.forEach(responsableCombo::addItem);
        
        // Ajout des composants
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Nom:"), gbc);
        gbc.gridx = 1;
        formPanel.add(nomField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1; gbc.gridheight = 2;
        formPanel.add(descriptionScroll, gbc);
        gbc.gridheight = 1;
        
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Date début:"), gbc);
        gbc.gridx = 1;
        formPanel.add(debutPicker, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Date fin:"), gbc);
        gbc.gridx = 1;
        formPanel.add(finPicker, gbc);
        
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Statut:"), gbc);
        gbc.gridx = 1;
        formPanel.add(statutCombo, gbc);
        
        gbc.gridx = 0; gbc.gridy = 6;
        formPanel.add(new JLabel("Responsable:"), gbc);
        gbc.gridx = 1;
        formPanel.add(responsableCombo, gbc);
        
        // Bouton Enregistrer
        JButton saveButton = new JButton("Enregistrer");
        saveButton.addActionListener(e -> {
            if (nomField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, 
                    "Le nom du projet est obligatoire", 
                    "Erreur", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Utilisateur selectedResponsable = (Utilisateur)responsableCombo.getSelectedItem();
            if (selectedResponsable == null) {
                JOptionPane.showMessageDialog(dialog,
                    "Veuillez sélectionner un responsable",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try {
                Projet nouveauProjet = new Projet();
                nouveauProjet.setNom(nomField.getText());
                nouveauProjet.setDescription(descriptionArea.getText());
                nouveauProjet.setDateDebut(debutPicker.getDate());
                nouveauProjet.setDateFin(finPicker.getDate());
                nouveauProjet.setStatut(Projet.StatutProjet.valueOf(
                    statutCombo.getSelectedItem().toString().toUpperCase().replace(" ", "_")));
                nouveauProjet.setIdUtilisateur(selectedResponsable.getId());
                
                if (controller.ajouterProjet(nouveauProjet)) {
                    chargerProjets();
                    dialog.dispose();
                    JOptionPane.showMessageDialog(this,
                        "Projet ajouté avec succès!",
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
    
    private void editerProjet() {
        int selectedRow = projetTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, 
                "Veuillez sélectionner un projet", 
                "Erreur", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int id = (int) tableModel.getValueAt(selectedRow, 0);
        Projet projet = controller.getProjetById(id);
        if (projet == null) {
            JOptionPane.showMessageDialog(this,
                "Projet introuvable",
                "Erreur",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        JDialog dialog = new JDialog();
        dialog.setTitle("Modifier Projet");
        dialog.setSize(500, 450);
        dialog.setModal(true);
        dialog.setLocationRelativeTo(this);
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Champs pré-remplis
        JTextField nomField = new JTextField(projet.getNom(), 20);
        JTextArea descriptionArea = new JTextArea(projet.getDescription(), 5, 20);
        JScrollPane descriptionScroll = new JScrollPane(descriptionArea);
        JDatePicker debutPicker = new JDatePicker();
        debutPicker.setDate(projet.getDateDebut());
        JDatePicker finPicker = new JDatePicker();
        finPicker.setDate(projet.getDateFin());
        JComboBox<String> statutCombo = new JComboBox<>(
            new String[]{"En attente", "En cours", "Terminé", "Annulé"});
        statutCombo.setSelectedItem(projet.getStatut().toString());
        
        // Combo box des responsables
        JComboBox<Utilisateur> responsableCombo = new JComboBox<>();
        List<Utilisateur> responsables = controller.listerUtilisateurs();
        responsables.forEach(responsableCombo::addItem);
        
        // Sélectionner le responsable actuel
        Utilisateur currentResponsable = controller.getUtilisateurById(projet.getIdUtilisateur());
        if (currentResponsable != null) {
            responsableCombo.setSelectedItem(currentResponsable);
        }
        
        // Ajout des composants qui sont identiques a ajouterProjet
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Nom:"), gbc);
        gbc.gridx = 1;
        formPanel.add(nomField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1; gbc.gridheight = 2;
        formPanel.add(descriptionScroll, gbc);
        gbc.gridheight = 1;
        
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Date début:"), gbc);
        gbc.gridx = 1;
        formPanel.add(debutPicker, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Date fin:"), gbc);
        gbc.gridx = 1;
        formPanel.add(finPicker, gbc);
        
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Statut:"), gbc);
        gbc.gridx = 1;
        formPanel.add(statutCombo, gbc);
        
        gbc.gridx = 0; gbc.gridy = 6;
        formPanel.add(new JLabel("Responsable:"), gbc);
        gbc.gridx = 1;
        formPanel.add(responsableCombo, gbc);
        
        // Bouton Enregistrer
        JButton saveButton = new JButton("Enregistrer");
        saveButton.addActionListener(e -> {
            if (nomField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, 
                    "Le nom du projet est obligatoire", 
                    "Erreur", 
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            Utilisateur selectedResponsable = (Utilisateur)responsableCombo.getSelectedItem();
            if (selectedResponsable == null) {
                JOptionPane.showMessageDialog(dialog,
                    "Veuillez sélectionner un responsable",
                    "Erreur",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try {
                projet.setNom(nomField.getText());
                projet.setDescription(descriptionArea.getText());
                projet.setDateDebut(debutPicker.getDate());
                projet.setDateFin(finPicker.getDate());
                projet.setStatut(Projet.StatutProjet.valueOf(
                    statutCombo.getSelectedItem().toString().toUpperCase().replace(" ", "_")));
                projet.setIdUtilisateur(selectedResponsable.getId());
                
                if (controller.modifierProjet(projet)) {
                    chargerProjets();
                    dialog.dispose();
                    JOptionPane.showMessageDialog(this,
                        "Projet modifié avec succès!",
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
    
    private void supprimerProjet() {
        int selectedRow = projetTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, 
                "Veuillez sélectionner un projet", 
                "Erreur", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int id = (int) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Êtes-vous sûr de vouloir supprimer ce projet?\nCette action est irréversible.", 
            "Confirmation", 
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (controller.supprimerProjet(id)) {
                chargerProjets();
                JOptionPane.showMessageDialog(this,
                    "Projet supprimé avec succès",
                    "Succès",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
    
    // Classe interne pour le date picker
    private class JDatePicker extends JPanel {
        private JTextField dateField;
        private JButton pickButton;
        
        public JDatePicker() {
            setLayout(new BorderLayout(5, 5));
            dateField = new JTextField(10);
            pickButton = new JButton("...");
            pickButton.setPreferredSize(new Dimension(25, 20));
            pickButton.addActionListener(e -> showDateChooser());
            
            add(dateField, BorderLayout.CENTER);
            add(pickButton, BorderLayout.EAST);
        }
        
        public Date getDate() {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                return sdf.parse(dateField.getText());
            } catch (Exception e) {
                return new Date(); // Retourne la date actuelle si parsing échoue
            }
        }
        
        public void setDate(Date date) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            dateField.setText(sdf.format(date));
        }
        
        private void showDateChooser() {
            JDialog dialog = new JDialog();
            dialog.setTitle("Choisir une date");
            dialog.setModal(true);
            dialog.setSize(300, 200);
            
            JSpinner spinner = new JSpinner(new SpinnerDateModel());
            JSpinner.DateEditor editor = new JSpinner.DateEditor(spinner, "dd/MM/yyyy");
            spinner.setEditor(editor);
            
            JButton okButton = new JButton("OK");
            okButton.addActionListener(e -> {
                dateField.setText(editor.getFormat().format(spinner.getValue()));
                dialog.dispose();
            });
            
            dialog.add(spinner, BorderLayout.CENTER);
            dialog.add(okButton, BorderLayout.SOUTH);
            dialog.setLocationRelativeTo(this);
            dialog.setVisible(true);
        }
    }
}