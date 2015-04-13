package controllers;

import com.itextpdf.text.DocumentException;
import interfaces.Controller;
import interfaces.Converter;
import interfaces.UserChoices;
import interfaces.ViewInterface;
import models.HTMLToPDF;
import org.jsoup.nodes.Document;
import org.python.core.PyObject;
import org.python.util.PythonInterpreter;
import view.InteractionPanel;
import view.TopText;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.concurrent.ExecutionException;

@SuppressWarnings("unchecked")
public class Mediator implements Controller {

    /**
     * Python interpreter for interacting with the python side
     */
    private PythonInterpreter pi;
    private UserChoices choice;

    private Mediator() {
        JProgressBar bar = new JProgressBar();
        bar.setStringPainted(true);
        JOptionPane.showOptionDialog(null, bar, "Loading...", JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE, null, new Object[]{}, null);

        SwingWorker<PythonInterpreter, Void> worker = new SwingWorker<PythonInterpreter, Void>() {
            @Override
            protected PythonInterpreter doInBackground() {
                PythonInterpreter pyInt = new PythonInterpreter();
                setProgress(10);
                pyInt.exec("from pymodules.PyInteraction import *");
                setProgress(40);
                pyInt.exec("from pymodules.HighLightSource import *");
                setProgress(40);
                return pyInt;
            }

            @Override
            protected void done() {
                try {
                    pi = get();
                    bar.getParent().setVisible(false);
                    setProgress(10);
                } catch (InterruptedException | ExecutionException e) {
                }
            }
        };

        worker.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                if (evt.getPropertyName().equals("progress")) {
                    System.out.println(evt.getNewValue());
                    bar.setValue(bar.getValue() + (Integer) evt.getNewValue());
                }
            }
        });

        worker.execute();
    }

    public static void start() {
        Mediator controller = new Mediator();
        JPanel topPanel = new TopText("Welcome to PDF converter!");
        topPanel.setPreferredSize(new Dimension(600, 100));
        JPanel interactionPanel = new InteractionPanel();
        interactionPanel.setPreferredSize(new Dimension(600, 480));

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame();
            Container c = frame.getContentPane();
            c.add(topPanel, BorderLayout.PAGE_START);
            c.add(interactionPanel, BorderLayout.CENTER);
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.pack();
            frame.setVisible(true);
        });

        SwingUtilities.invokeLater(() -> {
            ((ViewInterface) topPanel).initComponents();
            ((UserChoices) interactionPanel).setController(controller);
            ((ViewInterface) interactionPanel).initComponents();
        });

        controller.choice = (UserChoices) interactionPanel;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Converter convert = new HTMLToPDF();
        List<String> fnames = choice.getFiles();

        pi.set("lang", choice.getLanguage());
        pi.set("style", choice.getStyle());
        pi.set("fnames", fnames);
        pi.exec("highligher = HighLight()");
        pi.exec("highligher.setComponents(lexer=lang, style=style)");

        Iterator<String> iter = fnames.iterator();
        File pDir = Paths.get(choice.getRootFolder().getPath(), "PDF").toFile();

        for (PyObject obj : pi.eval("highligher.highlight_files(fnames)").asIterable()) {
            Document doc = (Document) obj.__tojava__(Document.class);
            File file = new File((iter.next() + ".pdf").replace(pDir.getParent(), pDir.getPath()));
            if (file.getParentFile().exists() || file.getParentFile().mkdirs()) {
                try (BufferedOutputStream bos =
                             new BufferedOutputStream(new FileOutputStream(file))) {
                    convert.convertDocumentFromContent(doc.outerHtml(), bos);
                } catch (IOException | DocumentException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    @Override
    public List<String> getLanguages() {
        List<String> list = (List<String>) pi.eval("get_languages()").__tojava__(List.class);
        return list;
    }

    @Override
    public List<String> getStyles() {
        List<String> list = (List<String>) pi.eval("get_styles()").__tojava__(List.class);
        return list;
    }

    @Override
    public void update(Observable o, Object arg) {
    }
}
