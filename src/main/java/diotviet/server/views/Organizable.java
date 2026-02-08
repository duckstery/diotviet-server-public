package diotviet.server.views;

import diotviet.server.entities.Group;

import java.util.Set;

public interface Organizable {
    /**
     * Get Groups
     *
     * @return
     */
    Set<Group> getGroups();

    /**
     * Set Groups
     *
     * @return
     */
    Organizable setGroups(Set<Group> groups);
}
