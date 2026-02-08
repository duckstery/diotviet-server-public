package diotviet.server.validators;

import diotviet.server.entities.Staff;
import diotviet.server.entities.User;
import diotviet.server.repositories.StaffRepository;
import diotviet.server.services.UserService;
import diotviet.server.templates.Staff.StaffInteractRequest;
import diotviet.server.traits.BusinessValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
public class StaffValidator extends BusinessValidator<Staff> {

    // ****************************
    // Properties
    // ****************************

    /**
     * Staff repository
     */
    @Autowired
    private StaffRepository repository;

    // ****************************
    // Public API
    // ****************************

    /**
     * Validate request and extract Entity
     *
     * @param request
     * @return
     */
    public Staff validateAndExtract(StaffInteractRequest request) {
        // Primary validation
        validate(request);

        // Only check for edit operation
        if (Objects.nonNull(request.id())) {
            // Check if User has higher privilege than target
            hasHigherPrivilegeThan(List.of(request.id()));
        }

        // Check if User is trying to create a Staff that exceed user's role
        checkAssignedRole(request);
        // Convert request to Staff
        Staff staff = map(request, Staff.class);
        // Check phone number
        checkPhoneNumber(staff);
        // Optimistic lock check
        checkOptimisticLock(staff, repository);
        // Check and get the valid code
        checkCode(staff, "NV", repository::findFirstByCodeAndIsDeletedFalse, repository::findFirstByCodeLikeOrderByCodeDesc);
        // Preserve LocalDate type data
        checkDateData(staff);

        return staff;
    }

    /**
     * Check if current Staff (or User) has higher privilege than specific Staffs (or Users)
     *
     * @param ids
     */
    public void hasHigherPrivilegeThan(List<Long> ids) {
        // Check if the length of [ids] is equals the count of User with Role less than current User that has id in [ids]
        if (ids.size() != repository.countByIdInAndUserRoleGreaterThan(ids, UserService.getRole())) {
            interrupt("can_not_reset", "");
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
    private void validate(StaffInteractRequest request) {
        assertStringRequired(request, "name", 50);
        assertStringRequired(request, "phoneNumber", 15);
        assertNumb(request, "role", true, 0, 4);
        assertStringNonRequired(request, "code", 0, 10);
    }

    /**
     * Check if user is trying to create a Staff that exceed user's role
     *
     * @param request
     */
    private void checkAssignedRole(StaffInteractRequest request) {
        // Get Staff's role code
        int staffRole = request.role();
        // Get user's role code
        int userRole = UserService.getRole().getCode();

        if (staffRole < userRole) {
            interrupt("role_escalated", "staff", "role");
        }
    }

    /**
     * Check if phone number is used
     *
     * @param staff
     */
    private void checkPhoneNumber(Staff staff) {
        if (repository.existsByPhoneNumberAndIdNotAndIsDeletedIsFalse(staff.getPhoneNumber(), staff.getId())) {
            interrupt("exists_by", "staff", "phoneNumber");
        }
    }

    /**
     * Check and preserve LocalDate data
     *
     * @param staff
     */
    private void checkDateData(Staff staff) {
        // Check if Staff is not exist, so nothing need to be preserved
        Optional<Staff> readonly = repository.findById(staff.getId());
        if (readonly.isEmpty()) {
            return;
        }

        // Get original staff to preserve LocalDate data
        Staff original = readonly.get();

        // Set data for modified staff
        staff.setCreatedAt(original.getCreatedAt());
    }
}
