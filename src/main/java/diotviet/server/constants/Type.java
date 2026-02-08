package diotviet.server.constants;

public enum Type {
    PRODUCT(0),
    TRANSACTION(1),
    PARTNER(2),
    PRINT(3);

    private final int code;

    Type(int code) {
        this.code = code;
    }

    /**
     * Generate Type from code
     *
     * @param code
     * @return
     */
    public static Type fromCode(int code) {
        return switch (code) {
            case 0 -> Type.PRODUCT;
            case 1 -> Type.TRANSACTION;
            case 2 -> Type.PARTNER;
            case 3 -> Type.PRINT;
            default -> null;
        };
    }
}
