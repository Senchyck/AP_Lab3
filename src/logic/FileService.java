package logic;

import exceptions.ObjectIsNotArrayListException;

import java.io.*;
import java.util.ArrayList;

public class FileService {
    public static void writeText(final String pathname, final String text) throws IOException {
        try (final PrintWriter pw = new PrintWriter(pathname)) {
            final boolean ignored = new File(pathname).createNewFile();
            pw.println(text);
        }
    }

    public static void writeObject(final String pathname, final Object object) throws IOException {
        try (final FileOutputStream fos = new FileOutputStream(pathname)) {
            try (final ObjectOutputStream oos = new ObjectOutputStream(fos)) {
                oos.writeObject(object);
            }
        }
    }

    public static <T> ArrayList<T> readAsArrayList(final String pathname, final Class<T> elementType)
            throws IOException, ClassNotFoundException, ObjectIsNotArrayListException {
        final File file = new File(pathname);
        if (file.createNewFile() || file.length() == 0) {
            return new ArrayList<>();
        } else {
            try (final FileInputStream fis = new FileInputStream(pathname)) {
                try (final ObjectInputStream ois = new ObjectInputStream(fis)) {
                    Object o = ois.readObject();

                    if (o instanceof ArrayList<?> arrayList) {
                        return new ArrayList<>(arrayList.stream().map(elementType::cast).toList());
                    } else {
                        throw new ObjectIsNotArrayListException(o.getClass());
                    }
                }
            }
        }
    }
}