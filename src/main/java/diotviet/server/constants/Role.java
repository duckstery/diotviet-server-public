package diotviet.server.constants;

import lombok.Getter;

public enum Role {
    ADMIN(0),
    OWNER(1),
    SUPER(2),
    STAFF(3),
    GUEST(4);

    @Getter
    private final int code;

    Role(int code) {
        this.code = code;
    }

    /**
     * Generate Role from code
     *
     * @param code
     * @return
     */
    public static Role fromCode(int code) {
        return switch (code) {
            case 0 -> Role.ADMIN;
            case 1 -> Role.OWNER;
            case 2 -> Role.SUPER;
            case 3 -> Role.STAFF;
            case 4 -> Role.GUEST;
            default -> null;
        };
    }
}