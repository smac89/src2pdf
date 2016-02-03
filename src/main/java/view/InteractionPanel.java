package view;

import interfaces.Controller;
import interfaces.UserChoices;
import interfaces.ViewInterface;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.Document;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;

public class InteractionPanel extends JPanel implements ViewInterface, UserChoices {

    private final Map<String, String> srcFiles;
    /**
     * A simple file visitor that enables one to walk a directory tree
     */
    private final FileVisitor<Path> walkDirectoryTree = new SimpleFileVisitor<Path>() {

        // TODO User might want to choose files based on some attribute of the directory
        // So it might help to let directory names be included in the output.
        //
        @Override
        public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
            srcFiles.put(file.getFileName().toString(), file.getParent().toString());
            return FileVisitResult.CONTINUE;
        }
    };
    private String lang, style, content;
    private String prompt = "Type a regex, ex [cpp$] [,java$] ...";
    private boolean manualInsert = false;
    private Controller control;
    private JTextArea dirContent;
    private File dir;

    public InteractionPanel() {
        super(new MigLayout("fill", "[]5::5[]", "[]5::5[]5::5[]"));
        setBorder(BorderFactory.createRaisedSoftBevelBorder());
        setBackground(new Color(Integer.parseInt("5E303B", 16)));
        srcFiles = new TreeMap<>();
    }

    @Override
    public List<String> getFiles() {
        List<String> selectedFiles = Arrays.asList(dirContent.getText().split("\n"));
        for (int t = 0; t < selectedFiles.size(); t++) {
            selectedFiles.set(t, Paths
                    .get(srcFiles.get(selectedFiles.get(t)), selectedFiles.get(t)).toString());
        }
        return selectedFiles;
    }

    @Override
    public String getLanguage() {
        return lang;
    }

    @Override
    public String getStyle() {
        return style;
    }

    @Override
    public void initComponents() {
        JButton button = new JButton("Convert!");
        button.addActionListener(control);
        add(button, "south, tag ok, spanx, center, height 30::");

        addProgressReport();
        addInteraction();
    }

    @Override
    public void showMessage(String msg, int msgType) {
        JOptionPane.showMessageDialog(this, msg, "Message", msgType);
    }

    @Override
    public File getRootFolder() {
        return dir;
    }

    @Override
    public void setController(Controller listener) {
        control = listener;
    }

    /* ------------------Private methods begin-------------------- */
    private void addProgressReport() {

        JTextArea textArea = new JTextArea(10, 10);
        textArea.setBorder(BorderFactory.createLoweredSoftBevelBorder());
        textArea.setBackground(new Color(Integer.parseInt("D3DBBD", 16)));
        textArea.setVisible(true);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setEditable(false);
        ((DefaultCaret) textArea.getCaret()).setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Progress"));

        add(scrollPane, "width 40%::, spany, growy, pushy");
    }

    /**
     * Used to select the correct language from the list of possible languages
     *
     * @return The combo box used for the selection
     */
    private JComboBox<String> getLangSelect() {
        JComboBox<String> langSelect =
                new JComboBox<>(control.getLanguages().toArray(new String[0]));
        langSelect.setBorder(BorderFactory.createTitledBorder("Language"));
        langSelect.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED)
                lang = (String) e.getItem();
        });
        return langSelect;
    }

    /**
     * Used to select the correct style from the list of possible styles
     *
     * @return The combo box used for the selection
     */
    private JComboBox<String> getStyleSelect() {
        JComboBox<String> styleSelect =
                new JComboBox<>(control.getStyles().toArray(new String[0]));
        styleSelect.setBorder(BorderFactory.createTitledBorder("Format Style"));
        styleSelect.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED)
                style = (String) e.getItem();
        });
        return styleSelect;
    }

    /**
     * @return a button for opening a folder in which the files to be read are contained
     */
    private JButton getFolderButton() {
        JButton fSelect = new JButton("Select Folder");
        fSelect.addActionListener(new ActionListener() {
            private final JFileChooser fileChooser = new JFileChooser(".");

            @Override
            public void actionPerformed(ActionEvent e) {
                fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                int action = fileChooser.showOpenDialog(InteractionPanel.this);
                if (action == JFileChooser.APPROVE_OPTION) {
                    dir = fileChooser.getSelectedFile();
                    fileChooser.setCurrentDirectory(fileChooser.getCurrentDirectory());
                    srcFiles.clear();
                    try {
                        Files.walkFileTree(Paths.get(dir.toURI()), walkDirectoryTree);
                        content = String.join("\n", srcFiles.keySet());
                        dirContent.setText(content);
                    } catch (IOException ignored) {
                    }
                }
            }
        });
        return fSelect;
    }

    /**
     * @return textfield that enables users to filter only files that user wants
     */
    private JTextField getFilesFilter() {
        JTextField textField = new JTextField(prompt);
        textField.setBorder(BorderFactory.createTitledBorder("Filter File extensions"));
        textField.setBackground(new Color(Integer.parseInt("4764EF", 16)));
        textField.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent arg) {
                JTextField textField = (JTextField) arg.getSource();
                if (textField.getText().equals(prompt)) {
                    manualInsert = true;
                    textField.setText("");
                }
            }

            @Override
            public void focusLost(FocusEvent arg) {
                JTextField textField = (JTextField) arg.getSource();
                if (textField.getText().isEmpty()) {
                    manualInsert = true;
                    textField.setText(prompt);
                }
            }
        });

        textField.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void changedUpdate(DocumentEvent arg) {
            }

            @Override
            public void insertUpdate(DocumentEvent arg) {
                filter(arg);
            }

            @Override
            public void removeUpdate(DocumentEvent arg) {
                filter(arg);
            }
        });
        return textField;
    }

    private void addInteraction() {
        JComboBox<String> langSelect = getLangSelect();
        lang = langSelect.getItemAt(0);

        JComboBox<String> styleSelect = getStyleSelect();
        style = styleSelect.getItemAt(0);

        JButton folderButton = getFolderButton();

        JTextArea textArea = new JTextArea(10, 10);
        textArea.setBackground(new Color(Integer.parseInt("5F2D23", 16)));
        textArea.setForeground(new Color(Integer.parseInt("12C4B5", 16)));
        textArea.setBorder(BorderFactory.createLoweredSoftBevelBorder());
        textArea.setVisible(true);
        textArea.setEditable(false);
        dirContent = textArea;

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Content"));

        JTextField filesFilter = getFilesFilter();

        add(langSelect, "width 29%::, height pref + 5::, top, split2");
        add(styleSelect, "width 29%::, height pref + 5::, top, wrap");
        add(folderButton, "top, width 29%::, center, height 30::, wrap");
        add(scrollPane, "spanx, grow, push, center");
        add(filesFilter, "growx, center, height pref + 5::");
    }

    /**
     * Compiles a pattern entered by the user to ensure it is a valid regex
     *
     * @param regex the pattern entered by the user
     * @return A pattern object to be used for filtering files
     */
    private Pattern compileRegex(String regex) {
        try {
            Pattern p = Pattern.compile(regex);
            return p;
        } catch (PatternSyntaxException ex) {
        }
        return null;
    }

    /**
     * Filters the user's input and shows only files that match the regex entered by the user
     *
     * @param evt the document event which signals that the user wrote something
     * @throws BadLocationException
     */
    private void filter(DocumentEvent evt) {
        // Only check for input from user
        if (!manualInsert && dirContent.getDocument().getLength() > 0) {
            try {
                Document document = evt.getDocument();
                String text = document.getText(0, document.getLength()).trim();
                Pattern p = compileRegex(text);
                if (p == null)
                    return;

                final Matcher matcher = p.matcher("");
                String matchedFiles = String.join("\n", srcFiles.keySet().stream()
                        .filter(input -> matcher.reset(input).find())
                        .collect(Collectors.toList()));

                if (!matchedFiles.isEmpty()) {
                    dirContent.setText(matchedFiles);
                } else
                    dirContent.setText(content);
            } catch (BadLocationException ignored) {
            }
        } else
            manualInsert = false;
    }
}
