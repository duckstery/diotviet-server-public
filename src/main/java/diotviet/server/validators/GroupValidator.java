package diotviet.server.validators;

import diotviet.server.constants.Type;
import diotviet.server.entities.Group;
import diotviet.server.exceptions.ServiceValidationException;
import diotviet.server.repositories.GroupRepository;
import diotviet.server.templates.Group.GroupInteractRequest;
import diotviet.server.traits.BusinessValidator;
import diotviet.server.views.Organizable;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.stream.Streams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
public class GroupValidator extends BusinessValidator<Group> {

    // ****************************
    // Properties
    // ****************************

    /**
     * Group repository
     */
    @Autowired
    private GroupRepository groupRepository;

    // ****************************
    // Public API
    // ****************************

    /**
     * Validate for Group (single) existence and return it
     */
    public Group isExistById(Long id) {
        if (Objects.isNull(id)) {
            return null;
        }

        // Check if category is exists
        Group group = groupRepository.findById(id).orElse(null);
        if (Objects.isNull(group)) {
            throw new ServiceValidationException("invalid_group", "", "group");
        }

        return group;
    }

    /**
     * Validate for Groups existence and return it
     *
     * @param ids
     * @return
     */
    public Set<Group> isExistByIds(Long[] ids) {
        if (Objects.isNull(ids)) {
            return null;
        }

        // Check if groups are exists
        Set<Group> group = groupRepository.findAllByIdIn(ids);
        if (group.size() != ids.length) {
            throw new ServiceValidationException("invalid_groups", "", "groups");
        }

        return group;
    }

    /**
     * Validate request and extract Entity
     *
     * @param request
     * @return
     */
    public Group validateAndExtract(GroupInteractRequest request) {
        // Primary validation
        assertStringRequired(request, "name" , 20);
        assertObject(request, "type", true);

        // Convert request to Group
        Group group = new Group();
        if (Objects.nonNull(request.id())) {
            group.setId(request.id());
        }
        group.setName(request.name());
        group.setType(Type.fromCode(request.type()));

        return group;
    }

    /**
     * Assign Groups by id for Organizable
     *
     * @param organizable
     * @param assignedIds
     */
    public void assignGroups(Organizable organizable, Long[] assignedIds) {
        // Validate assignedIds to make sure all Groups with these ids are exist
        Set<Group> groups = isExistByIds(assignedIds);
        // Convert Organization's current Groups to Set of Group's id
        Set<Long> currentGroup = Streams.of(organizable.getGroups()).map(Group::getId).collect(Collectors.toUnmodifiableSet());

        // Check if Groups before and after assignment is not the same
        if (!(groups.size() == CollectionUtils.size(organizable.getGroups()) && Set.of(assignedIds).containsAll(currentGroup))) {
            // If so, assign new Set of Groups for Organizable
            organizable.setGroups(groups);
        }
    }
}
