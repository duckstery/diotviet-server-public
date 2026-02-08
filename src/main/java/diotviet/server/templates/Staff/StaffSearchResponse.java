package diotviet.server.templates.Staff;

import diotviet.server.views.Staff.StaffSearchView;
import org.springframework.data.domain.Page;

public record StaffSearchResponse(Page<StaffSearchView> items) {
}
