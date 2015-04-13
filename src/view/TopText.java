package view;

import com.google.common.base.Joiner;
import interfaces.ViewInterface;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TopText extends JPanel implements ComponentListener, ViewInterface {

    private final List<String> tokens;
    private boolean resized = true;
    private String[] textWords;

    /**
     * Constructor for the welcome panel at the top of the gui
     *
     * @param text the text to display
     */
    public TopText(String text) {
        setBorder(BorderFactory.createRaisedBevelBorder());
        setDoubleBuffered(true);
        setBackground(new Color(Integer.parseInt("303B5E", 16)));
        textWords = text.split("\\s+");
        tokens = Arrays.asList(textWords);
        addComponentListener(this);
    }

    private void setWordsPerLine(Graphics2D graphics) {

        // Calculate the length of the full string
        FontMetrics fmet = graphics.getFontMetrics(graphics.getFont());
        List<String> rows = new ArrayList<String>();

        int textlen = (int) Math
                .min(fmet.stringWidth(Joiner.on(' ').join(tokens)),
                        getWidth() / 1.5);

        // Get the words that should fit on each line
        for (int i = 0; i < tokens.size(); ) {
            List<String> words = new ArrayList<String>();
            for (int genWidth = 0; i < tokens.size() && genWidth < textlen; i++) {
                genWidth += fmet.stringWidth(tokens.get(i));
                ;
                words.add(tokens.get(i));
                if (genWidth >= textlen || (i + 1 == tokens.size())) {
                    rows.add(Joiner.on(' ').join(words));
                }
            }
        }
        textWords = rows.toArray(new String[rows.size()]);
    }

    @Override
    protected void paintComponent(Graphics graphic) {
        super.paintComponent(graphic);

        Graphics2D graphics = (Graphics2D) graphic;
        if (resized) {
            graphics.setFont(graphics.getFont().deriveFont(22.0f));
            setWordsPerLine(graphics);
            resized = false;
        }
        drawStringCentered(graphics, true);
    }

    private void drawStringCentered(Graphics2D graphics, boolean wrapLong) {
        FontMetrics fmet = graphics.getFontMetrics(graphics.getFont());
        graphics.setPaint(new Color(Integer.parseInt("09BD90", 16)));
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        if (wrapLong) {
            int textHeight = fmet.getHeight() * textWords.length;
            int heightStart = getHeight() / 2 - textHeight / 2
                    + fmet.getHeight() / 2;

            for (String line : textWords) {
                int width = fmet.stringWidth(line);
                graphics.drawString(line, getWidth() / 2 - width / 2,
                        heightStart);
                heightStart += fmet.getHeight();
            }
        } else {
            String line = Joiner.on(' ').join(textWords);
            int width = fmet.stringWidth(line);
            graphics.drawString(line, getWidth() / 2 - width / 2, getHeight()
                    / 2 - fmet.getHeight() / 2);
        }
    }

    @Override
    public void componentHidden(ComponentEvent arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void componentMoved(ComponentEvent arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void componentResized(ComponentEvent arg0) {
        resized = true;
    }

    @Override
    public void componentShown(ComponentEvent arg0) {
        // TODO Auto-generated method stub
    }

    @Override
    public void initComponents() {
        // TODO Auto-generated method stub
    }

    @Override
    public void showMessage(String msg, int msgType) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }
}