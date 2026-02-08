package diotviet.server.services;

import diotviet.server.entities.Document;
import diotviet.server.repositories.DocumentRepository;
import diotviet.server.templates.Document.DocumentInteractRequest;
import diotviet.server.validators.DocumentValidator;
import diotviet.server.views.Document.DocumentInitView;
import diotviet.server.views.Document.DocumentMetaView;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DocumentService {

    // ****************************
    // Properties
    // ****************************

    /**
     * Document repository
     */
    @Autowired
    private DocumentRepository repository;
    /**
     * Document validator
     */
    @Autowired
    private DocumentValidator validator;

    // ****************************
    // Public API
    // ****************************

    /**
     * Init Document by group id
     *
     * @param groupId
     * @return
     */
    public List<DocumentInitView> init(Long groupId) {
        // Find all documents by groupId
        List<DocumentInitView> documents = repository.findAllByGroupIdOrderById(groupId);
        // Then, re-fetch the first Document (in Documents) with data
        documents.set(0, repository.findById(documents.get(0).getId(), DocumentInitView.class));

        return documents;
    }

    /**
     * Find by id
     *
     * @param id
     * @return
     */
    public DocumentInitView findById(Long id) {
        return validator.isExist(repository.findById(id, DocumentInitView.class));
    }

    /**
     * Store Document
     *
     * @param request
     * @return
     */
    public DocumentMetaView store(DocumentInteractRequest request) {
        // Common validate for create and update
        Document document = validator.validateAndExtract(request);
        document.setIsActive(true);
        // Save and flush
        document = repository.save(document);

        return repository.findById(document.getId(), DocumentMetaView.class);
    }

    /**
     * Delete Document of Group
     *
     * @param groupId
     * @param id
     */
    public void delete(Long groupId, Long id) {
        // Validate if except for "id", Group has at least 1 Document
        validator.mustHasAtLeastOneExceptFor(groupId, id);
        // Delete
        repository.deleteById(id);
    }

    /**
     * Find active document by group name
     *
     * @param groupName
     * @return
     */
    public Document getActiveDocumentOfGroup(String groupName) {
        return repository.findByIsActiveTrueAndGroupName(groupName);
    }
}
