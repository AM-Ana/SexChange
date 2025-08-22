import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Pattern;

import javax.swing.*;


public class SexChangeForm extends JFrame {
    private static final String NOTIF_ITEM = "Notification de changement de prénoms à l’état-civil";

    private final JCheckBox changeNamesCheckBox;
    private final JTextField civilFirstNamesField;
    private final JTextField chosenFirstNamesField;
    private final JTextField lastNameField;
    private final JTextField birthDateField;
    private final JTextField birthPlaceField;
    private final JComboBox<String> civilSexCombo;
    private final JTextField addressField;
    private final JTextArea tribunalArea;
    private final JTextArea narrativeArea;
    private final JTextField currentCityField;
    private final JTextField nationalityField;
    private final JTextField professionField;
    private final JTextField maritalStatusField;
    private final JTextField childrenInfoField;
    private final JCheckBox pacsCheckBox;
    private final JButton attachmentsButton;

    private List<String> attachmentsCurrent;
    private List<String> attachmentsDefaultSexOnly;
    private List<String> attachmentsDefaultWithNames;
    private boolean lastModeChangeNames;


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            SexChangeForm form = new SexChangeForm();
            form.setVisible(true);
        });
    }

    public SexChangeForm() {
        super("Requête de changement de sexe à l'état civil");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(820, 840);
        setLocationRelativeTo(null);

        changeNamesCheckBox = new JCheckBox("Cela concerne aussi un changement de prénoms");
        civilFirstNamesField = new JTextField(20);
        chosenFirstNamesField = new JTextField(20);
        lastNameField = new JTextField(20);
        birthDateField = new JTextField(10);
        birthPlaceField = new JTextField(20);
        civilSexCombo = new JComboBox<>(new String[]{"Masculin", "Féminin"});
        addressField = new JTextField(30);
        tribunalArea = new JTextArea(4, 30);
        tribunalArea.setLineWrap(true);
        tribunalArea.setWrapStyleWord(true);
        narrativeArea = new JTextArea(8, 30);
        narrativeArea.setLineWrap(true);
        narrativeArea.setWrapStyleWord(true);
        currentCityField = new JTextField(20);
        nationalityField = new JTextField(20);
        professionField = new JTextField(30);
        maritalStatusField = new JTextField(20);
        childrenInfoField = new JTextField(20);
        pacsCheckBox = new JCheckBox("A contracté un Pacte civil de solidarité (PACS)");
        attachmentsButton = new JButton("Pièces justificatives");

        initAttachmentDefaults();
        attachmentsCurrent = new ArrayList<>(attachmentsDefaultSexOnly);
        lastModeChangeNames = changeNamesCheckBox.isSelected();

        initLayout();
        initListeners();
    }

    private void initAttachmentDefaults() {
        attachmentsDefaultWithNames = Arrays.asList(
                "Copie intégrale de l’acte de naissance",
                "Copie de la pièce d’identité (carte d’identité ou passeport)",
                "Justificatif de domicile",
                "Attestation de l’employeur confirmant l’usage des prénoms d’usage dans l’environnement professionnel",
                "Carte étudiante faisant état de l'usage des prénoms d'usage à l'université",
                "Attestations de proches confirmant la reconnaissance sociale dans le genre revendiqué",
                "Ordonnance de traitement hormono-substitutif (facultatif)",
                "Photographies",
                "Photocopies de courrier reçu au prénom d’usage"
        );
        attachmentsDefaultSexOnly = Arrays.asList(
                "Copie intégrale de l’acte de naissance",
                "Copie de la pièce d’identité (carte d’identité ou passeport)",
                "Justificatif de domicile",
                "Attestation de l’employeur confirmant l’usage du sexe d’usage dans l’environnement professionnel",
                "Carte étudiante faisant état de l'usage du sexe d'usage à l'université",
                "Attestations de proches confirmant la reconnaissance sociale dans le genre revendiqué",
                "Ordonnance de traitement hormono-substitutif (facultatif)",
                "Photographies",
                "Photocopies de courrier reçu au sexe d’usage",
                NOTIF_ITEM
        );
    }

    private boolean listsEqual(List<String> a, List<String> b) {
        if (a == b) return true;
        if (a == null || b == null) return false;
        if (a.size() != b.size()) return false;
        for (int i = 0; i < a.size(); i++) if (!Objects.equals(a.get(i), b.get(i))) return false;
        return true;
    }

    private List<String> defaultAttachmentsForMode(boolean changeNames) {
        return changeNames ? attachmentsDefaultWithNames : attachmentsDefaultSexOnly;
    }

    private void sanitizeAttachmentsForMode(boolean changeNames) {
        if (changeNames) {
            List<String> filtered = new ArrayList<>();
            for (String s : attachmentsCurrent) {
                String low = s == null ? "" : s.toLowerCase(Locale.ROOT);
                if (low.startsWith("notification de changement de prénoms")) continue;
                filtered.add(s);
            }
            attachmentsCurrent = filtered;
        }
    }

    private void maybeSwitchAttachmentsOnModeChange(boolean newMode) {
        List<String> prevDefault = defaultAttachmentsForMode(lastModeChangeNames);
        if (listsEqual(attachmentsCurrent, prevDefault)) {
            attachmentsCurrent = new ArrayList<>(defaultAttachmentsForMode(newMode));
        }
        sanitizeAttachmentsForMode(newMode);
        lastModeChangeNames = newMode;
    }

    private void initLayout() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 6, 4, 6);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        int y = 0;

        gbc.gridx = 0; gbc.gridy = y; gbc.gridwidth = 2;
        panel.add(changeNamesCheckBox, gbc);
        y++;

        gbc.gridwidth = 1; gbc.gridy = y; gbc.gridx = 0;
        panel.add(new JLabel("Prénoms à l'état civil :"), gbc);
        gbc.gridx = 1; panel.add(civilFirstNamesField, gbc);
        y++;

        gbc.gridx = 0; gbc.gridy = y;
        panel.add(new JLabel("Prénoms choisis :"), gbc);
        gbc.gridx = 1; panel.add(chosenFirstNamesField, gbc);
        chosenFirstNamesField.setEnabled(false);
        y++;

        gbc.gridx = 0; gbc.gridy = y;
        panel.add(new JLabel("Nom de famille :"), gbc);
        gbc.gridx = 1; panel.add(lastNameField, gbc);
        y++;

        gbc.gridx = 0; gbc.gridy = y;
        panel.add(new JLabel("Date de naissance (DD/MM/YYYY) :"), gbc);
        gbc.gridx = 1; panel.add(birthDateField, gbc);
        y++;

        gbc.gridx = 0; gbc.gridy = y;
        panel.add(new JLabel("Lieu de naissance :"), gbc);
        gbc.gridx = 1; panel.add(birthPlaceField, gbc);
        y++;

        gbc.gridx = 0; gbc.gridy = y;
        panel.add(new JLabel("Sexe à l'état civil :"), gbc);
        gbc.gridx = 1; panel.add(civilSexCombo, gbc);
        y++;

        gbc.gridx = 0; gbc.gridy = y;
        panel.add(new JLabel("Nationalité :"), gbc);
        gbc.gridx = 1; panel.add(nationalityField, gbc);
        y++;

        gbc.gridx = 0; gbc.gridy = y;
        panel.add(new JLabel("Adresse :"), gbc);
        gbc.gridx = 1; panel.add(addressField, gbc);
        y++;

        gbc.gridx = 0; gbc.gridy = y;
        panel.add(new JLabel("Profession/Employeur :"), gbc);
        gbc.gridx = 1; panel.add(professionField, gbc);
        y++;

        gbc.gridx = 0; gbc.gridy = y;
        panel.add(new JLabel("Statut matrimonial :"), gbc);
        gbc.gridx = 1; panel.add(maritalStatusField, gbc);
        y++;

        gbc.gridx = 0; gbc.gridy = y;
        panel.add(new JLabel("Situation parentale :"), gbc);
        gbc.gridx = 1; panel.add(childrenInfoField, gbc);
        y++;

        gbc.gridx = 0; gbc.gridy = y; gbc.gridwidth = 2;
        panel.add(pacsCheckBox, gbc);
        y++;

        gbc.gridwidth = 1; gbc.gridx = 0; gbc.gridy = y;
        panel.add(new JLabel("Ville actuelle (Fait à) :"), gbc);
        gbc.gridx = 1; panel.add(currentCityField, gbc);
        y++;

        gbc.gridx = 0; gbc.gridy = y;
        panel.add(new JLabel("Tribunal judiciaire compétent :"), gbc);
        gbc.gridx = 1; gbc.fill = GridBagConstraints.BOTH;
        JScrollPane tribunalScroll = new JScrollPane(tribunalArea);
        panel.add(tribunalScroll, gbc);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        y++;

        gbc.gridx = 0; gbc.gridy = y; gbc.gridwidth = 2;
        panel.add(new JLabel("Récit de vie :"), gbc);
        y++;

        gbc.gridy = y; gbc.gridwidth = 2;
        JScrollPane scroll = new JScrollPane(narrativeArea);
        panel.add(scroll, gbc);
        y++;

        gbc.gridy = y; gbc.gridwidth = 1; gbc.gridx = 0;
        panel.add(attachmentsButton, gbc);

        gbc.gridx = 1;
        gbc.anchor = GridBagConstraints.EAST;
        JButton generateButton = new JButton("Générer le document");
        panel.add(generateButton, gbc);
        generateButton.addActionListener(this::generateDocument);

        getContentPane().add(panel, BorderLayout.CENTER);
    }

    private void initListeners() {
        changeNamesCheckBox.addActionListener(e -> {
            boolean selected = changeNamesCheckBox.isSelected();
            chosenFirstNamesField.setEnabled(selected);
            maybeSwitchAttachmentsOnModeChange(selected);
        });
        attachmentsButton.addActionListener(this::openAttachmentsDialog);
    }

    private void openAttachmentsDialog(ActionEvent e) {
        sanitizeAttachmentsForMode(changeNamesCheckBox.isSelected());
        JustificationsDialog dlg = new JustificationsDialog(this, new ArrayList<>(attachmentsCurrent));
        dlg.setVisible(true);
        if (dlg.isSaved()) {
            attachmentsCurrent = dlg.getItems();
            sanitizeAttachmentsForMode(changeNamesCheckBox.isSelected());
        }
    }

    private boolean validateBirthDate(String text) {
        if (!Pattern.matches("\\d{2}/\\d{2}/\\d{4}", text)) return false;
        try {
            java.time.LocalDate.parse(text, java.time.format.DateTimeFormatter.ofPattern("dd/MM/uuuu"));
            return true;
        } catch (Exception ex) {
            return false;
        }
    }

    private void generateDocument(ActionEvent event) {
        boolean change = changeNamesCheckBox.isSelected();
        String civilFirst = civilFirstNamesField.getText().trim();
        String chosenFirst = chosenFirstNamesField.getText().trim();
        String lastName = lastNameField.getText().trim();
        String birthDate = birthDateField.getText().trim();
        String birthPlace = birthPlaceField.getText().trim();
        String civilSex = (String) civilSexCombo.getSelectedItem();
        String address = addressField.getText().trim();
        String currentCity = currentCityField.getText().trim();
        String tribunal = tribunalArea.getText().trim();
        String narrative = narrativeArea.getText().trim();
        String nationality = nationalityField.getText().trim();
        String profession = professionField.getText().trim();
        String maritalStatus = maritalStatusField.getText().trim();
        String childrenInfo = childrenInfoField.getText().trim();
        boolean pacs = pacsCheckBox.isSelected();

        if (civilFirst.isEmpty() || lastName.isEmpty() || birthDate.isEmpty() || birthPlace.isEmpty() || address.isEmpty() || tribunal.isEmpty() || currentCity.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Merci de remplir tous les champs obligatoires.", "Champs manquants", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (!validateBirthDate(birthDate)) {
            JOptionPane.showMessageDialog(this, "La date de naissance est invalide. Format attendu : DD/MM/YYYY.", "Date invalide", JOptionPane.WARNING_MESSAGE);
            return;
        }
        if (change && chosenFirst.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Veuillez indiquer les prénoms choisis.", "Champs manquants", JOptionPane.WARNING_MESSAGE);
            return;
        }

        sanitizeAttachmentsForMode(change);
        List<String> items = new ArrayList<>(attachmentsCurrent);

        FormData data = new FormData(
                change,
                civilFirst,
                chosenFirst,
                lastName,
                birthDate,
                birthPlace,
                civilSex,
                address,
                tribunal,
                narrative,
                currentCity,
                items,
                nationality,
                profession,
                maritalStatus,
                childrenInfo,
                pacs
        );

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Enregistrer le document");
        chooser.setSelectedFile(new File("requete_changement_sexe.docx"));
        int result = chooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = chooser.getSelectedFile();
            if (!selectedFile.getName().toLowerCase(Locale.ROOT).endsWith(".docx")) {
                selectedFile = new File(selectedFile.getParentFile(), selectedFile.getName() + ".docx");
            }
            DocumentGenerator generator = new DocumentGenerator();
            try {
                generator.generate(data, selectedFile);
                JOptionPane.showMessageDialog(this, "Le document a été généré avec succès.", "Succès", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Erreur lors de la génération du document : " + ex.getMessage(), "Erreur", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}

class JustificationsDialog extends JDialog {
    private final DefaultListModel<String> model;
    private final JList<String> list;
    private boolean saved = false;

    public JustificationsDialog(JFrame owner, List<String> initialItems) {
        super(owner, "Pièces justificatives", true);
        model = new DefaultListModel<>();
        if (initialItems != null) initialItems.forEach(model::addElement);
        list = new JList<>(model);
        buildUI();
        setSize(600, 400);
        setLocationRelativeTo(owner);
    }

    private void buildUI() {
        setLayout(new BorderLayout());
        add(new JScrollPane(list), BorderLayout.CENTER);

        JPanel buttons = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);

        JButton addBtn = new JButton("Ajouter");
        JButton editBtn = new JButton("Modifier");
        JButton delBtn = new JButton("Supprimer");
        JButton saveBtn = new JButton("Enregistrer");
        JButton cancelBtn = new JButton("Annuler");

        gbc.gridx = 0; gbc.gridy = 0; buttons.add(addBtn, gbc);
        gbc.gridx = 1; buttons.add(editBtn, gbc);
        gbc.gridx = 2; buttons.add(delBtn, gbc);
        gbc.gridx = 3; buttons.add(saveBtn, gbc);
        gbc.gridx = 4; buttons.add(cancelBtn, gbc);
        add(buttons, BorderLayout.SOUTH);

        addBtn.addActionListener(e -> {
            String s = JOptionPane.showInputDialog(this, "Nouvelle pièce :", "");
            if (s != null) {
                s = s.trim();
                if (!s.isEmpty()) model.addElement(s);
            }
        });

        editBtn.addActionListener(e -> {
            int idx = list.getSelectedIndex();
            if (idx >= 0) {
                String current = model.get(idx);
                String s = JOptionPane.showInputDialog(this, "Modifier la pièce :", current);
                if (s != null) {
                    s = s.trim();
                    if (!s.isEmpty()) model.set(idx, s);
                }
            }
        });

        delBtn.addActionListener(e -> {
            int idx = list.getSelectedIndex();
            if (idx >= 0) model.remove(idx);
        });

        saveBtn.addActionListener(e -> {
            saved = true;
            dispose();
        });

        cancelBtn.addActionListener(e -> {
            saved = false;
            dispose();
        });
    }

    public boolean isSaved() {
        return saved;
    }

    public List<String> getItems() {
        List<String> out = new ArrayList<>();
        for (int i = 0; i < model.getSize(); i++) out.add(model.get(i));
        return out;
    }
}