package repository.file;

import exception.FileReadException;
import exception.FileWriteException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractFileRepository<T> {
    protected final File file;
    protected final List<T> items;

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
            throw new FileReadException(file, e);
        }
    }

    protected void writeToFile() {
        try (ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(file))) {
            outputStream.writeObject(items);
        } catch (Exception e) {
            throw new FileWriteException(file, e);
        }
    }
}
