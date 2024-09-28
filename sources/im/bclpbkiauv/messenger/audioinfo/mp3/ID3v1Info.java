package im.bclpbkiauv.messenger.audioinfo.mp3;

import com.serenegiant.uvccamera.BuildConfig;
import im.bclpbkiauv.messenger.audioinfo.AudioInfo;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import kotlin.UByte;

public class ID3v1Info extends AudioInfo {
    public static boolean isID3v1StartPosition(InputStream input) throws IOException {
        input.mark(3);
        try {
            return input.read() == 84 && input.read() == 65 && input.read() == 71;
        } finally {
            input.reset();
        }
    }

    public ID3v1Info(InputStream input) throws IOException {
        if (isID3v1StartPosition(input)) {
            this.brand = "ID3";
            this.version = "1.0";
            byte[] bytes = readBytes(input, 128);
            this.title = extractString(bytes, 3, 30);
            this.artist = extractString(bytes, 33, 30);
            this.album = extractString(bytes, 63, 30);
            try {
                this.year = Short.parseShort(extractString(bytes, 93, 4));
            } catch (NumberFormatException e) {
                this.year = 0;
            }
            this.comment = extractString(bytes, 97, 30);
            ID3v1Genre id3v1Genre = ID3v1Genre.getGenre(bytes[127]);
            if (id3v1Genre != null) {
                this.genre = id3v1Genre.getDescription();
            }
            if (bytes[125] == 0 && bytes[126] != 0) {
                this.version = BuildConfig.VERSION_NAME;
                this.track = (short) (bytes[126] & UByte.MAX_VALUE);
            }
        }
    }

    /* access modifiers changed from: package-private */
    public byte[] readBytes(InputStream input, int len) throws IOException {
        int total = 0;
        byte[] bytes = new byte[len];
        while (total < len) {
            int current = input.read(bytes, total, len - total);
            if (current > 0) {
                total += current;
            } else {
                throw new EOFException();
            }
        }
        return bytes;
    }

    /* access modifiers changed from: package-private */
    public String extractString(byte[] bytes, int offset, int length) {
        try {
            String text = new String(bytes, offset, length, "ISO-8859-1");
            int zeroIndex = text.indexOf(0);
            return zeroIndex < 0 ? text : text.substring(0, zeroIndex);
        } catch (Exception e) {
            return "";
        }
    }
}
