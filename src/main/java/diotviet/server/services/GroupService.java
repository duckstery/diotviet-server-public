package diotviet.server.services;

import diotviet.server.constants.Type;
import diotviet.server.entities.Group;
import diotviet.server.repositories.GroupRepository;
import diotviet.server.templates.Group.GroupInteractRequest;
import diotviet.server.validators.GroupValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class GroupService {

    // ****************************
    // Properties
    // ****************************

    /**
     * Group repository
     */
    @Autowired
    private GroupRepository repository;
    /**
     * Group validator
     */
    @Autowired
    private GroupValidator validator;

    // ****************************
    // Public API
    // ****************************

    /**
     * Get all groups and order by name (ASC) and id (DESC)
     *
     * @return
     */
    public List<Group> getGroups(Type type) {
        // Query for Product's data
        return repository.findAllByType(type, Sort.by("id").ascending());
    }

    /**
     * Save item
     *
     * @param request
     */
    @Transactional
    public void store(GroupInteractRequest request) {
        // Common validate for create and update
        repository.save(validator.validateAndExtract(request));
    }

    /**
     * Delete multiple item with ids
     *
     * @param ids
     */
    @Transactional(rollbackFor = {Exception.class, Throwable.class})
    public void delete(Long id, Type type) {
        // Select the target Group
        Group group = validator.isExist(repository.findByIdAndType(id, type));
        // Delete association depend on Type
        switch (group.getType()) {
            case PRODUCT -> repository.deleteAssocProductById(group.getId());
            case TRANSACTION -> repository.deleteAssocProductById(group.getId());
            case PARTNER -> repository.deleteAssocCustomerById(group.getId());
        }
        // Delete Group, this operation won't cause any side effect, only the "groups" table
        repository.deleteById(id);
    }
}
