package diotviet.server.structures;

import lombok.NoArgsConstructor;
import org.apache.commons.compress.utils.IOUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

@NoArgsConstructor
public class URLStreamFile implements MultipartFile {

    // ****************************
    // Properties
    // ****************************

    /**
     * URL
     */
    private URL url;

    // ****************************
    // Public API
    // ****************************

    /**
     * Create an URLStreamFile with URL string
     *
     * @param url
     * @return
     * @throws IOException
     */
    public static URLStreamFile of(String url) throws MalformedURLException {
        return of(new URL(url));
    }

    /**
     * Create an URLStreamFile with URL
     *
     * @param url
     * @return
     * @throws IOException
     */
    public static URLStreamFile of (URL url) {
        // Create new instance
        URLStreamFile file = new URLStreamFile();
        // Save URL
        file.url = url;

        return file;
    }

    // ****************************
    // Overridden API
    // ****************************

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getOriginalFilename() {
        return null;
    }

    @Override
    public String getContentType() {
        return null;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public long getSize() {
        return 0;
    }

    @Override
    public byte[] getBytes() throws IOException {
        return IOUtils.toByteArray(url.openStream());
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return url.openStream();
    }

    @Override
    public void transferTo(File dest) throws IOException, IllegalStateException {

    }
}
