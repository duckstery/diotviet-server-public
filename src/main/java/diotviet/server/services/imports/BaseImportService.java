package diotviet.server.services.imports;

import diotviet.server.traits.EntityProvider;
import diotviet.server.utils.OtherUtils;
import diotviet.server.views.Identifiable;
import org.apache.commons.lang3.StringUtils;
import org.dhatim.fastexcel.reader.ExcelReaderException;
import org.dhatim.fastexcel.reader.Row;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public abstract class BaseImportService<E> {

    // ****************************
    // Properties
    // ****************************

    /**
     * Index of code
     */
    private int index;
    /**
     * Code prefix
     */
    private String prefix;

    // ****************************
    // Abstraction
    // ****************************

    /**
     * Prepare cache, data, ...
     *
     * @return
     */
    public abstract List<E> prep();

    /**
     * Convert legacy model (KiotViet) to Entity
     *
     * @param row
     * @return
     */
    public abstract E convert(Row row);

    /**
     * Re-attach any relationship
     *
     * @param entity
     * @return
     */
    public abstract void pull(List<E> entity);

    /**
     * Import List of entity
     *
     * @param entities
     */
    public abstract void runImport(List<E> entities);

    /**
     * Flush cache
     */
    public abstract void flush();

    // ****************************
    // Protected API
    // ****************************

    /**
     * Initialize code index
     *
     * @param prefix
     * @param provider
     */
    public void initializeCode(String prefix, EntityProvider<Identifiable, String> provider) {
        // Get Identifiable to retrieve code
        Identifiable identifiable = provider.provide(prefix + "%");
        // Save code prefix
        this.prefix = prefix;
        // Save code last index
        this.index = Integer.parseInt(Objects.isNull(identifiable) ? "1" : identifiable.getCode().substring(3));
    }

    /**
     * Generate code begin from index (index will increase after each generation)
     */
    public String generateCode() {
        return generateCode(5);
    }

    /**
     * Generate code begin from index (index will increase after each generation)
     */
    public String generateCode(int lengthOfNumber) {
        // Generate format
        String format = "%s%0" + lengthOfNumber + "d";
        return String.format(format, prefix, index++);
    }

    /**
     * Resolve string
     *
     * @param row
     * @param index
     * @return
     */
    protected String resolve(Row row, int index) {
        return row.getCell(index).getRawValue();
    }

    /**
     * Resolve value of cell with raw value as default value
     *
     * @param row
     * @param index
     * @return
     */
    protected String resolveValue(Row row, int index) {
        return Objects.isNull(row.getCell(index).getValue()) ? "" : resolve(row, index);
    }

    /**
     * Resolve phone number
     *
     * @param row
     * @param index
     * @return
     */
    protected String resolvePhoneNumber(Row row, int index) {
        // Get value
        String value = resolve(row, index);

        if (Objects.isNull(value)) {
            return null;
        }

        return value.length() > 15 ? value.substring(0, 15) : value;
    }

    /**
     * Resolve gender
     *
     * @param row
     * @param index
     * @return
     */
    protected Boolean resolveGender(Row row, int index) {
        return StringUtils.compare(resolve(row, index), "Nam") == 0;
    }

    /**
     * Resolve point
     *
     * @param row
     * @param index
     * @return
     */
    protected Long resolvePoint(Row row, int index) {
        return Long.parseLong(OtherUtils.get(resolve(row, index), "0"));
    }


    /**
     * Resolve decimal
     *
     * @param row
     * @param index
     * @return
     */
    protected Long resolveDecimal(Row row, int index) {
        // Get value
        Optional<BigDecimal> optional = row.getCellAsNumber(index);

        return Objects.isNull(optional) || optional.isEmpty() ? 0L : optional.get().toBigInteger().longValue();
    }

    /**
     * Resolve decimal
     *
     * @param row
     * @param index
     * @return
     */
    protected LocalDateTime resolveDate(Row row, int index) {
        Optional<LocalDateTime> optional;

        try {
            // Get value
            optional = row.getCellAsDate(index);
        } catch (ExcelReaderException ignored) {
            optional = Optional.empty();
        }

        return Objects.isNull(optional) || optional.isEmpty()
            ? LocalDateTime.parse(resolve(row, index), DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))
            : LocalDateTime.ofInstant(optional.get().toInstant(ZoneOffset.UTC), ZoneId.systemDefault());
    }
}
