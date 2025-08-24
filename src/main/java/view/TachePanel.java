package view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.Date;
import model.Tache;
import model.Projet;
import model.Utilisateur;
import controller.ProjetController;
import java.text.SimpleDateFormat;

public class TachePanel extends JPanel {
    private ProjetController controller;
    private JTable tacheTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> projetFilterCombo;
    private JComboBox<String> statutFilterCombo;
    
    public TachePanel() {
        controller = new ProjetController();
        setLayout(new BorderLayout());
        initFilterPanel();
        initTable();
        initToolbar();
        chargerTaches();
    }
    
    private void initFilterPanel() {
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        
        // Filtre par projet
        projetFilterCombo = new JComboBox<>();
        projetFilterCombo.addItem("Tous les projets");
        controller.listerProjets().forEach(p -> 
            projetFilterCombo.addItem(p.getNom())
        );
        projetFilterCombo.addActionListener(e -> filtrerTaches());
        
        // Filtre par statut
        statutFilterCombo = new JComboBox<>();
        statutFilterCombo.addItem("Tous les statuts");
        for (Tache.StatutTache statut : Tache.StatutTache.values()) {
            statutFilterCombo.addItem(statut.toString());
        }
        statutFilterCombo.addActionListener(e -> filtrerTaches());
        
        filterPanel.add(new JLabel("Projet:"));
        filterPanel.add(projetFilterCombo);
        filterPanel.add(new JLabel("Statut:"));
        filterPanel.add(statutFilterCombo);
        
        add(filterPanel, BorderLayout.NORTH);
    }
    
    private void initTable() {
        String[] columns = {"ID", "Titre", "Description", "Statut", "Priorité", "Échéance", "Projet", "Assigné à"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        tacheTable = new JTable(tableModel);
        tacheTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tacheTable.setAutoCreateRowSorter(true);
        tacheTable.setRowHeight(25);
        
        tacheTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editerTache();
                }
            }
        });
        
        add(new JScrollPane(tacheTable), BorderLayout.CENTER);
    }
    
    private void initToolbar() {
        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);
        
        JButton addButton = new JButton("Ajouter");
        addButton.addActionListener(e -> ajouterTache());
        
        JButton editButton = new JButton("Modifier");
        editButton.addActionListener(e -> editerTache());
        
        JButton deleteButton = new JButton("Supprimer");
        deleteButton.addActionListener(e -> supprimerTache());
        
        JButton refreshButton = new JButton("Actualiser");
        refreshButton.addActionListener(e -> {
            chargerTaches();
            actualiserFiltres();
        });
        
        toolBar.add(addButton);
        toolBar.add(editButton);
        toolBar.add(deleteButton);
        toolBar.addSeparator();
        toolBar.add(refreshButton);
        
        add(toolBar, BorderLayout.SOUTH);
    }
    
    private void actualiserFiltres() {
        projetFilterCombo.removeAllItems();
        projetFilterCombo.addItem("Tous les projets");
        controller.listerProjets().forEach(p -> 
            projetFilterCombo.addItem(p.getNom())
        );
    }
    
    private void chargerTaches() {
        tableModel.setRowCount(0);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        
        List<Tache> taches = controller.listerTaches();
        for (Tache t : taches) {
            Projet projet = controller.getProjetById(t.getIdProjet());
            Utilisateur utilisateur = controller.getUtilisateurById(t.getIdUtilisateur());
            
            Object[] row = {
                t.getId(),
                t.getTitre(),
                t.getDescription().length() > 30 ? 
                    t.getDescription().substring(0, 30) + "..." : t.getDescription(),
                t.getStatut().toString(),
                t.getPriorite().toString(),
                t.getDateEcheance() != null ? sdf.format(t.getDateEcheance()) : "",
                projet != null ? projet.getNom() : "Non assigné",
                utilisateur != null ? utilisateur.getNom() : "Non assigné"
            };
            tableModel.addRow(row);
        }
    }
    
    private void filtrerTaches() {
        String projetNom = (String) projetFilterCombo.getSelectedItem();
        String statut = (String) statutFilterCombo.getSelectedItem();
        
        List<Tache> tachesFiltrees = controller.listerTaches().stream()
            .filter(t -> projetFilterCombo.getSelectedIndex() == 0 || 
                  controller.getProjetById(t.getIdProjet()).getNom().equals(projetNom))
            .filter(t -> statutFilterCombo.getSelectedIndex() == 0 || 
                  t.getStatut().toString().equals(statut))
            .toList();
            
        tableModel.setRowCount(0);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        
        for (Tache t : tachesFiltrees) {
            Projet projet = controller.getProjetById(t.getIdProjet());
            Utilisateur utilisateur = controller.getUtilisateurById(t.getIdUtilisateur());
            
            Object[] row = {
                t.getId(),
                t.getTitre(),
                t.getDescription().length() > 30 ? 
                    t.getDescription().substring(0, 30) + "..." : t.getDescription(),
                t.getStatut().toString(),
                t.getPriorite().toString(),
                t.getDateEcheance() != null ? sdf.format(t.getDateEcheance()) : "",
                projet != null ? projet.getNom() : "Non assigné",
                utilisateur != null ? utilisateur.getNom() : "Non assigné"
            };
            tableModel.addRow(row);
        }
    }
    
    private void ajouterTache() {
        JDialog dialog = new JDialog();
        dialog.setTitle("Nouvelle Tâche");
        dialog.setSize(500, 400);
        dialog.setModal(true);
        dialog.setLocationRelativeTo(this);
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Composants du formulaire
        JTextField titreField = new JTextField(20);
        JTextArea descriptionArea = new JTextArea(5, 20);
        JScrollPane descriptionScroll = new JScrollPane(descriptionArea);
        JComboBox<Tache.StatutTache> statutCombo = new JComboBox<>(Tache.StatutTache.values());
        JComboBox<Tache.Priorite> prioriteCombo = new JComboBox<>(Tache.Priorite.values());
        JDatePicker echeancePicker = new JDatePicker();
        JComboBox<Projet> projetCombo = new JComboBox<>();
        JComboBox<Utilisateur> utilisateurCombo = new JComboBox<>();
        
        // Charger les projets et utilisateurs
        controller.listerProjets().forEach(projetCombo::addItem);
        controller.listerUtilisateurs().forEach(utilisateurCombo::addItem);
        
        // Ajout des composants
        gbc.gridx = 0; gbc.gridy = 0;
        formPanel.add(new JLabel("Titre:"), gbc);
        gbc.gridx = 1;
        formPanel.add(titreField, gbc);
        
        gbc.gridx = 0; gbc.gridy = 1;
        formPanel.add(new JLabel("Description:"), gbc);
        gbc.gridx = 1; gbc.gridheight = 2;
        formPanel.add(descriptionScroll, gbc);
        gbc.gridheight = 1;
        
        gbc.gridx = 0; gbc.gridy = 3;
        formPanel.add(new JLabel("Statut:"), gbc);
        gbc.gridx = 1;
        formPanel.add(statutCombo, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4;
        formPanel.add(new JLabel("Priorité:"), gbc);
        gbc.gridx = 1;
        formPanel.add(prioriteCombo, gbc);
        
        gbc.gridx = 0; gbc.gridy = 5;
        formPanel.add(new JLabel("Échéance:"), gbc);
        gbc.gridx = 1;
        formPanel.add(echeancePicker, gbc);
        
        gbc.gridx = 0; gbc.gridy = 6;
        formPanel.add(new JLabel("Projet:"), gbc);
        gbc.gridx = 1;
        formPanel.add(projetCombo, gbc);
        
        gbc.gridx = 0; gbc.gridy = 7;
        formPanel.add(new JLabel("Assigné à:"), gbc);
        gbc.gridx = 1;
        formPanel.add(utilisateurCombo, gbc);
        
        // Bouton Enregistrer
        JButton saveButton = new JButton("Enregistrer");
        saveButton.addActionListener(e -> {
            if (titreField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, 
                    "Le titre est obligatoire", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try {
                Tache nouvelleTache = new Tache();
                nouvelleTache.setTitre(titreField.getText());
                nouvelleTache.setDescription(descriptionArea.getText());
                nouvelleTache.setStatut((Tache.StatutTache) statutCombo.getSelectedItem());
                nouvelleTache.setPriorite((Tache.Priorite) prioriteCombo.getSelectedItem());
                nouvelleTache.setDateEcheance(echeancePicker.getDate());
                
                Projet projetSelectionne = (Projet) projetCombo.getSelectedItem();
                Utilisateur utilisateurSelectionne = (Utilisateur) utilisateurCombo.getSelectedItem();
                
                if (projetSelectionne != null) {
                    nouvelleTache.setIdProjet(projetSelectionne.getId());
                }
                
                if (utilisateurSelectionne != null) {
                    nouvelleTache.setIdUtilisateur(utilisateurSelectionne.getId());
                }
                
                if (controller.ajouterTache(nouvelleTache)) {
                    chargerTaches();
                    dialog.dispose();
                    JOptionPane.showMessageDialog(this, 
                        "Tâche ajoutée avec succès", "Succès", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, 
                    "Erreur: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(saveButton, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    
    private void editerTache() {
        int selectedRow = tacheTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, 
                "Veuillez sélectionner une tâche", "Erreur", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int id = (int) tableModel.getValueAt(selectedRow, 0);
        Tache tache = controller.getTacheById(id);
        
        JDialog dialog = new JDialog();
        dialog.setTitle("Modifier Tâche");
        dialog.setSize(500, 400);
        dialog.setModal(true);
        dialog.setLocationRelativeTo(this);
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Composants du formulaire pré-remplis
        JTextField titreField = new JTextField(tache.getTitre(), 20);
        JTextArea descriptionArea = new JTextArea(tache.getDescription(), 5, 20);
        JScrollPane descriptionScroll = new JScrollPane(descriptionArea);
        JComboBox<Tache.StatutTache> statutCombo = new JComboBox<>(Tache.StatutTache.values());
        statutCombo.setSelectedItem(tache.getStatut());
        JComboBox<Tache.Priorite> prioriteCombo = new JComboBox<>(Tache.Priorite.values());
        prioriteCombo.setSelectedItem(tache.getPriorite());
        JDatePicker echeancePicker = new JDatePicker();
        if (tache.getDateEcheance() != null) {
            echeancePicker.setDate(tache.getDateEcheance());
        }
        JComboBox<Projet> projetCombo = new JComboBox<>();
        JComboBox<Utilisateur> utilisateurCombo = new JComboBox<>();
        
        // Charger les projets et utilisateurs
        controller.listerProjets().forEach(projetCombo::addItem);
        controller.listerUtilisateurs().forEach(utilisateurCombo::addItem);
        
        // Sélectionner le projet et utilisateur actuel
        Projet projetActuel = controller.getProjetById(tache.getIdProjet());
        Utilisateur utilisateurActuel = controller.getUtilisateurById(tache.getIdUtilisateur());
        if (projetActuel != null) projetCombo.setSelectedItem(projetActuel);
        if (utilisateurActuel != null) utilisateurCombo.setSelectedItem(utilisateurActuel);
        
        
        // Ajout des composants identique à ajouterTache()
gbc.gridx = 0; gbc.gridy = 0;
formPanel.add(new JLabel("Titre:"), gbc);
gbc.gridx = 1;
formPanel.add(titreField, gbc);

gbc.gridx = 0; gbc.gridy = 1;
formPanel.add(new JLabel("Description:"), gbc);
gbc.gridx = 1; gbc.gridheight = 2;
formPanel.add(descriptionScroll, gbc);
gbc.gridheight = 1;

gbc.gridx = 0; gbc.gridy = 3;
formPanel.add(new JLabel("Statut:"), gbc);
gbc.gridx = 1;
formPanel.add(statutCombo, gbc);

gbc.gridx = 0; gbc.gridy = 4;
formPanel.add(new JLabel("Priorité:"), gbc);
gbc.gridx = 1;
formPanel.add(prioriteCombo, gbc);

gbc.gridx = 0; gbc.gridy = 5;
formPanel.add(new JLabel("Échéance:"), gbc);
gbc.gridx = 1;
formPanel.add(echeancePicker, gbc);

gbc.gridx = 0; gbc.gridy = 6;
formPanel.add(new JLabel("Projet:"), gbc);
gbc.gridx = 1;
formPanel.add(projetCombo, gbc);

gbc.gridx = 0; gbc.gridy = 7;
formPanel.add(new JLabel("Assigné à:"), gbc);
gbc.gridx = 1;
formPanel.add(utilisateurCombo, gbc);
        
        // Bouton Enregistrer
        JButton saveButton = new JButton("Enregistrer");
        saveButton.addActionListener(e -> {
            if (titreField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(dialog, 
                    "Le titre est obligatoire", "Erreur", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try {
                tache.setTitre(titreField.getText());
                tache.setDescription(descriptionArea.getText());
                tache.setStatut((Tache.StatutTache) statutCombo.getSelectedItem());
                tache.setPriorite((Tache.Priorite) prioriteCombo.getSelectedItem());
                tache.setDateEcheance(echeancePicker.getDate());
                
                Projet projetSelectionne = (Projet) projetCombo.getSelectedItem();
                Utilisateur utilisateurSelectionne = (Utilisateur) utilisateurCombo.getSelectedItem();
                
                if (projetSelectionne != null) {
                    tache.setIdProjet(projetSelectionne.getId());
                }
                
                if (utilisateurSelectionne != null) {
                    tache.setIdUtilisateur(utilisateurSelectionne.getId());
                }
                
                if (controller.modifierTache(tache)) {
                    chargerTaches();
                    dialog.dispose();
                    JOptionPane.showMessageDialog(this, 
                        "Tâche modifiée avec succès", "Succès", JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, 
                    "Erreur: " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(saveButton, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    
    private void supprimerTache() {
        int selectedRow = tacheTable.getSelectedRow();
        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(this, 
                "Veuillez sélectionner une tâche", "Erreur", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        int id = (int) tableModel.getValueAt(selectedRow, 0);
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Êtes-vous sûr de vouloir supprimer cette tâche?", 
            "Confirmation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        
        if (confirm == JOptionPane.YES_OPTION) {
            if (controller.supprimerTache(id)) {
                chargerTaches();
                JOptionPane.showMessageDialog(this, 
                    "Tâche supprimée avec succès", "Succès", JOptionPane.INFORMATION_MESSAGE);
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
                return null;
            }
        }
        
        public void setDate(Date date) {
            if (date != null) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
                dateField.setText(sdf.format(date));
            }
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