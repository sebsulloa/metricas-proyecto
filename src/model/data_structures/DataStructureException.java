package model.data_structures;

public class DataStructureException extends Exception {
    public enum ExceptionType {
        YA_EXISTE, VACIO, POS, NULL
    }
    
    private final ExceptionType type;

    public DataStructureException(ExceptionType type, String causa) {
        super(causa);
        this.type = type;
    }

    public ExceptionType getType() {
        return type;
    }

    @Override
    public String getMessage() {
        return String.format("[%s] %s", type.name(), super.getMessage());
    }
}