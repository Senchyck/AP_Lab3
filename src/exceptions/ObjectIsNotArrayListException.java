package exceptions;

public class ObjectIsNotArrayListException extends Exception {
    public ObjectIsNotArrayListException(Class<?> clazz) {
        super("The object is not ArrayList, but %s".formatted(clazz.getName()));
    }
}
