package diotviet.server.services.imports;

import diotviet.server.entities.Staff;
import diotviet.server.repositories.StaffRepository;
import org.dhatim.fastexcel.reader.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class StaffImportService extends BaseImportService<Staff> {

    // ****************************
    // Properties
    // ****************************

    /**
     * Staff repository
     */
    @Autowired
    private StaffRepository staffRepository;

    // ****************************
    // Public API
    // ****************************

    /**
     * Prepare to import Staff
     *
     * @return
     */
    @Override
    public List<Staff> prep() {
        return null;
    }

    /**
     * Convert legacy to Staff
     *
     * @param row
     * @return
     */
    @Override
    public Staff convert(Row row) {
        return null;
    }

    /**
     * Re-attach any relationship
     *
     * @param staffs
     * @return
     */
    @Override
    public void pull(List<Staff> staffs) {

    }

    /**
     * Import Staff
     *
     * @param staffs
     */
    @Override
    @Transactional
    public void runImport(List<Staff> staffs) {
        // Bulk insert
        staffRepository.saveAll(staffs);

    }

    /**
     * Flush cache
     */
    @Override
    public void flush() {

    }
}
