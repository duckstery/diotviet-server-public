package diotviet.server.constants;

import lombok.Getter;

@Getter
public enum Status {
    PENDING(0),
    PROCESSING(1),
    RESOLVED(2),
    ABORTED(3);

    private final int code;

    Status(int code) {
        this.code = code;
    }

    /**
     * Generate Type from code
     *
     * @param code
     * @return
     */
    public static Status fromCode(int code) {
        return switch (code) {
            case 0 -> Status.PENDING;
            case 1 -> Status.PROCESSING;
            case 2 -> Status.RESOLVED;
            case 3 -> Status.ABORTED;
            default -> null;
        };
    }

    /**
     * Generate Type from code
     *
     * @param code
     * @return
     */
    public static Status fromCode(long code) {
        return fromCode((int) code);
    }
}
