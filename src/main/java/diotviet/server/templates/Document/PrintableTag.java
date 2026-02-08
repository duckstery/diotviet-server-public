package diotviet.server.templates.Document;

import com.fasterxml.jackson.annotation.JsonIgnore;

/**
 * Printable field
 *
 * @param sequence
 * @param path
 * @param sub
 * @param isIterable
 * @param isParentIterable
 * @param parentKey
 * @param isIdentifier
 */
public record PrintableTag(
        @JsonIgnore
        int sequence,
        String key,
        String path,
        PrintableTag[] sub,
        Boolean isIterable,
        Boolean isParentIterable,
        String parentKey,
        Boolean isIdentifier
) {
}
