package diotviet.server.templates.Document;

import diotviet.server.views.Document.DocumentInitView;

import java.util.List;
import java.util.Map;

public record DocumentSelectResponse(
        List<DocumentInitView> documents,
        PrintableTag[] tags,
        Map<String, Object> example
) {
}
