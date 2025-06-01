package exception;

import java.io.File;

public class FileWriteException extends RuntimeException {
    public FileWriteException(File file, Throwable cause) {
        super("Error writing from file: " + file.getPath(), cause);
    }
}
