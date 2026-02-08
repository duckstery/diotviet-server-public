package diotviet.server.data;

import com.querydsl.core.BooleanBuilder;
import diotviet.server.entities.QStaff;
import diotviet.server.templates.Staff.StaffSearchRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.Objects;

@Component
public class StaffDAO {
    /**
     * Create filter based on request
     *
     * @param request
     * @return
     */
    public BooleanBuilder createFilter(StaffSearchRequest request) {
        // Get QStaff
        QStaff staff = QStaff.staff;
        // Final expressions
        BooleanBuilder query = new BooleanBuilder();

        // Filter by min createdAt
        if (Objects.nonNull(request.createAtFrom())) {
            query.and(staff.createdAt.goe(request.createAtFrom().atStartOfDay()));
        }
        // Filter by max createdAt
        if (Objects.nonNull(request.createAtTo())) {
            query.and(staff.createdAt.loe(request.createAtTo().atTime(LocalTime.MAX)));
        }
        // Filter by min birthday
        if (Objects.nonNull(request.birthdayFrom())) {
            query.and(staff.birthday.goe(request.birthdayFrom()));
        }
        // Filter by max birthday
        if (Objects.nonNull(request.birthdayTo())) {
            query.and(staff.birthday.loe(request.birthdayTo()));
        }
        // Filter by isMale flag
        if (Objects.nonNull(request.isMale())) {
            query.and(staff.isMale.eq(request.isMale()));
        }
        // Filter by isMale flag
        if (Objects.nonNull(request.isDeactivated())) {
            query.and(staff.isDeactivated.eq(request.isDeactivated()));
        }
        // Filter by search string
        if (StringUtils.isNotBlank(request.search())) {
            query.and(staff.name.coalesce("")
                    .concat(staff.phoneNumber.coalesce(""))
                    .concat(staff.address.coalesce(""))
                    .toLowerCase()
                    .contains(request.search().toLowerCase()));
        }

        // Connect expression
        return query.and(staff.isDeleted.isFalse());
    }
}
