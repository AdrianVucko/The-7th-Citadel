package com.tvz.avuckovic.the7thcitadel.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FileUtils {
    public static List<String[]> readRowAttributesForFile(String filePath, boolean skipFirstLine) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))){
            List<String[]> attributesPerRow = new ArrayList<>();
            String line;
            if(skipFirstLine) {
                reader.readLine();
            }

            while ((line = reader.readLine()) != null) {
                //Empty string if there is no entry for column
                line = line.replaceAll(";(?=;|$)", "; ");
                String[] attributes = line.split(";");
                attributesPerRow.add(attributes);
            }

            return attributesPerRow;
        } catch (IOException e) {
            throw new RuntimeException("An error occurred while reading objects!", e);
        }
    }

    public static <T extends Serializable> List<T>  loadObjectsFromFile(String fileName) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName))) {
            return (List<T>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("An error occurred while loading objects!", e);
        }
    }

    public static <T extends Serializable> void writeObjects(String filePath, List<T> objects) {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(filePath))) {
            out.writeObject(objects);
        } catch (IOException e) {
            throw new RuntimeException("An error occurred while saving objects!", e);
        }
    }

    public static boolean fileExists(String pathName) {
        File file = new File(pathName);
        return file.exists();
    }
}
