package diotviet.server.views.Staff;

import diotviet.server.views.Lockable;
import diotviet.server.views.Visualizer;

public interface StaffDetailView extends StaffSearchView, Lockable, Visualizer {
    /**
     * Facebook account
     *
     * @return
     */
    String getFacebook();

    /**
     * Description
     *
     * @return
     */
    String getDescription();
}
