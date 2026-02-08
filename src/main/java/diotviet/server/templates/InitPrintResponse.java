package diotviet.server.templates;

import diotviet.server.templates.Document.PrintableTag;

public record InitPrintResponse(
        String template,
        PrintableTag[] tags
) {
}
