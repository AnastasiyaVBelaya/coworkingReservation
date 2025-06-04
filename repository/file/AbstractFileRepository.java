package repository.file;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractFileRepository<T> {
    protected final File file;
    protected final List<T> items;

    private static final String READ_WARNING =
            "Warning! Unable to read data from file '%s'. " +
                    "The file might be corrupted or incompatible. " +
                    "It is recommended to delete or rename this file to restore application functionality.";

    private static final String WRITE_WARNING =
            "Warning! Unable to write data to file '%s'. Possible causes:\n" +
                    "- File or directory is read-only or access is denied.\n" +
                    "- File is in use by another program.\n" +
                    "- Insufficient disk space.\n" +
                    "- File system errors or hardware issues.\n" +
                    "Please check file permissions, close other programs using the file, " +
                    "free up disk space if needed, or restart the application.";

    protected AbstractFileRepository(String filePath) {
        this.file = new File(filePath);
        this.items = readFromFile();
    }

    protected List<T> readFromFile() {
        if (!file.exists()) {
            return new ArrayList<>();
        }
        try (ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(file))) {
            return (List<T>) inputStream.readObject();
        } catch (Exception e) {
            System.out.printf((READ_WARNING) + "%n", file.getName());
            return new ArrayList<>();
        }
    }

    protected void writeToFile() {
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(file))) {
            outputStream.writeObject(items);
        } catch (Exception e) {
            System.out.printf((WRITE_WARNING) + "%n", file.getName());
        }
    }
}
