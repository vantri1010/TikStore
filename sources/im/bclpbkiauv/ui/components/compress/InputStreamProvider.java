package im.bclpbkiauv.ui.components.compress;

import java.io.IOException;
import java.io.InputStream;

public interface InputStreamProvider {
    void close();

    String getPath();

    InputStream open() throws IOException;
}
