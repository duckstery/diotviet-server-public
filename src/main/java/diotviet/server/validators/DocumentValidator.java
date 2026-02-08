package diotviet.server.validators;

import diotviet.server.entities.Document;
import diotviet.server.repositories.DocumentRepository;
import diotviet.server.templates.Document.DocumentInteractRequest;
import diotviet.server.traits.BusinessValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class DocumentValidator extends BusinessValidator<Document> {

    // ****************************
    // Properties
    // ****************************

    /**
     * Document repository
     */
    @Autowired
    private DocumentRepository repository;
    /**
     * Group validator
     */
    @Autowired
    private GroupValidator groupValidator;

    // ****************************
    // Public API
    // ****************************

    /**
     * Validate request and extract Entity
     *
     * @param request
     * @return
     */
    public Document validateAndExtract(DocumentInteractRequest request) {
        // Primary validation
        validate(request);
        // Convert request to Customer
        Document document = map(request, Document.class);
        // Optimistic lock check
        checkOptimisticLock(document, repository);

        // Check if request's groupId is not empty
        if (Objects.nonNull(request.groupId())) {
            // Check and get valid Group
            document.setGroup(groupValidator.isExistById(request.groupId()));
        }

        return document;
    }

    /**
     * Check if Group has at least 1 Document except for target
     *
     * @param groupId
     * @param id
     */
    public void mustHasAtLeastOneExceptFor(Long groupId, Long id) {
        // Check count
        if (repository.countByGroupIdAndIdNot(groupId, id) == 0) {
            interrupt("least_document", "document");
        }
    }


    // ****************************
    // Private API
    // ****************************

    /**
     * Primary validation
     *
     * @param request
     */
    private void validate(DocumentInteractRequest request) {
        assertStringRequired(request, "name", 20);
    }
}
