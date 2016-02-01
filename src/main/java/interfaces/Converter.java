package interfaces;

import com.itextpdf.text.DocumentException;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

public abstract class Converter {
    public abstract void convertDocumentFromContent(String content, String saveLoc)
            throws IOException, DocumentException;

    public abstract void convertDocumentFromContent(String content, OutputStream out)
            throws IOException, DocumentException;

    public abstract void convertDocumentFromURL(URL content, OutputStream out)
            throws IOException, DocumentException;

    public abstract void convertDocumentFromURL(URL content, String saveLoc)
            throws IOException, DocumentException;

    public abstract void convertDocumentFromStream(InputStream input, String saveLoc)
            throws IOException, DocumentException;

    public abstract void convertDocumentFromStream(InputStream input, OutputStream out)
            throws IOException, DocumentException;
}