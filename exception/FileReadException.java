package exception;

import java.io.File;

public class FileReadException extends RuntimeException {
    public FileReadException(File file, Throwable cause) {
        super("Error reading from file: " + file.getPath(), cause);
    }
}
