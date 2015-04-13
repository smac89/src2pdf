package models;

import com.itextpdf.text.DocumentException;
import interfaces.Converter;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.*;
import java.net.URL;
import java.util.Scanner;

public final class HTMLToPDF extends Converter {

    private final static String content = "<div style=\"background: #ffffff; "
            + "overflow:auto;width:auto;border:solid gray;border-width:.1em .1em "
            + ".1em .8em;padding:.2em .6em;\"><table><tr><td><pre style=\"margin: 0; "
            + "line-height: 125%\">1 2</pre></td><td><pre style=\"margin: 0; "
            + "line-height: 125%\"><span style=\"color: #008800; font-weight: bold\">"
            + "import</span> <span style=\"color: #0e84b5; font-weight: bold\">"
            + "interfaces.ProgressListener</span> <span style=\"color: #008800; "
            + "font-weight: bold\">as</span> <span style=\"color: #0e84b5; font-weight: bold\">Listener</span>"
            + "<span style=\"color: #008800; font-weight: bold\">from</span> <span "
            + "style=\"color: #0e84b5; font-weight: bold\">pygments.lexers</span> <span "
            + "style=\"color: #008800; font-weight: bold\">import</span> find_lexer_class"
            + "</pre></td></tr></table></div>";

    public static void main(String[] args) {
        String save = "./example123.pdf";
        try (OutputStream out = new BufferedOutputStream(new FileOutputStream(
                save))) {

            Converter conv = new HTMLToPDF();
            conv.convertDocumentFromContent(content, out);

        } catch (DocumentException | IOException e) {
            System.out.println("Can't open the folder");
        }
    }

    /**
     * @param content Valid HTML in a string
     * @param saveLoc A valid location to save the converted pdf
     */
    @Override
    public synchronized void convertDocumentFromContent(String content,
                                                        String saveLoc) throws IOException, DocumentException {

        try (OutputStream out = new BufferedOutputStream(new FileOutputStream(
                saveLoc))) {

            ITextRenderer render = new ITextRenderer();
            render.setDocumentFromString(content);
            render.layout();
            render.createPDF(out);
        }
    }

    /**
     * @param content Valid HTML in a string
     * @param out     A (buffered) stream to write the pdf
     */
    @Override
    public synchronized void convertDocumentFromContent(String content,
                                                        OutputStream out) throws IOException, DocumentException {

        ITextRenderer render = new ITextRenderer();
        render.setDocumentFromString(content);
        render.layout();
        render.createPDF(out);
    }

    /**
     * @param content A url specifying a network location of the HTML file to
     *                read
     * @param out     A (buffered) stream to write the pdf
     */
    @Override
    public synchronized void convertDocumentFromURL(URL content,
                                                    OutputStream out) throws IOException, DocumentException {

        try (Scanner scanner = new Scanner(content.openStream())) {

            ITextRenderer render = new ITextRenderer();
            render.setDocument(scanner.useDelimiter("\\A").next());
            render.layout();
            render.createPDF(out);
        }
    }

    /**
     * @param content A url specifying a network location of the HTML file to
     *                read
     * @param saveLoc A valid location to save the pdf
     */
    @Override
    public synchronized void convertDocumentFromURL(URL content, String saveLoc)
            throws IOException, DocumentException {

        try (OutputStream out = new BufferedOutputStream(new FileOutputStream(
                saveLoc)); Scanner scanner = new Scanner(content.openStream())) {

            ITextRenderer render = new ITextRenderer();
            render.setDocument(scanner.useDelimiter("\\A").next());
            render.layout();
            render.createPDF(out);
        }
    }

    /**
     * @param input   A (Buffered) input stream to read the file from
     * @param saveLoc A valid location to save the pdf
     */
    @Override
    public synchronized void convertDocumentFromStream(InputStream input,
                                                       String saveLoc) throws IOException, DocumentException {

        try (OutputStream out = new BufferedOutputStream(new FileOutputStream(
                saveLoc)); Scanner scanner = new Scanner(input)) {

            ITextRenderer render = new ITextRenderer();
            render.setDocumentFromString(scanner.useDelimiter("\\A").next());
            render.layout();
            render.createPDF(out);
        }
    }

    /**
     * @param input A (Buffered) input stream to read the file from
     * @param out   A (buffered) stream to write the pdf
     */
    @Override
    public synchronized void convertDocumentFromStream(InputStream input,
                                                       OutputStream out) throws IOException, DocumentException {

        try (Scanner scanner = new Scanner(input)) {

            ITextRenderer render = new ITextRenderer();
            render.setDocumentFromString(scanner.useDelimiter("\\A").next());
            render.layout();
            render.createPDF(out);
        }
    }
}
