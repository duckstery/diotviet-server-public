package diotviet.server.traits;

import diotviet.server.views.Identifiable;
import diotviet.server.views.Lockable;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Objects;
import java.util.stream.IntStream;

public abstract class BusinessValidator<T> extends BaseValidator<T> {

    // ****************************
    // Properties
    // ****************************

    /**
     * Model mapper
     */
    @Autowired
    private ModelMapper modelMapper;

    // ****************************
    // Public API
    // ****************************

    /**
     * Map object (Record included) to object (Record excluded)
     *
     * @param object
     * @param destinationType
     * @return
     */
    public T map(Object object, Class<T> destinationType) {
        return modelMapper.map(object, destinationType);
    }

    /**
     * Direct map
     *
     * @param object
     * @param destination
     * @return
     */
    public final <S, D> D directMap(S object, D destination) {
        // Check if destination is existed
        isExist(destination);

        // Direct map object to destination object
        modelMapper.map(object, destination);

        return destination;
    }

    /**
     * Validate if code is valid, then return the valid code, else, interrupt
     *
     * @param target:          Target of validation
     * @param format:          Preserved format
     * @param lengthOfNumber:  Length of number after preserved format
     * @param provider:        Provider to provider template item for validation
     * @param defaultProvider: Provider to provide default entity
     * @return
     */
    public void checkCode(Identifiable target, String format, int lengthOfNumber, EntityProvider<Identifiable, String> provider, EntityProvider<Identifiable, String> defaultProvider) {
        // Init holder
        long id = target.getId();
        String code = target.getCode();

        if (StringUtils.isBlank(code)) {
            // If code is null, no need for validation
            code = generateCode(format, lengthOfNumber, defaultProvider);
        } else if (id == 0) {
            // Validate for "CREATE"
            if (code.startsWith(format)) {
                // Check if code format is reserved
                interrupt("reserved", getKey(), "code");
            } else if (Objects.nonNull(provider.provide(code))) {
                // Check if code is exist
                interrupt("exists_by", getKey(), "code");
            }
        } else {
            // Validate for "UPDATE"
            // Get first Customer that has matched code
            Identifiable identifiable = provider.provide(code);
            // Check if identifiable is null
            boolean identifiableIsNull = Objects.isNull(identifiable);
            if (identifiableIsNull && code.startsWith(format)) {
                // Check if Customer with code is not exist and code format is reserved
                interrupt("reserved", getKey(), "code");
            } else if (!identifiableIsNull && identifiable.getId() != id) {
                // Else, check if Customer exist and that Customer is not self
                interrupt("exists_by", getKey(), "code");
            }
        }

        target.setCode(code);
    }

    /**
     * Validate if code is valid, then return the valid code, else, interrupt
     *
     * @param target:          Target of validation
     * @param format:          Preserved format
     * @param provider:        Provider to provider template item for validation
     * @param defaultProvider: Provider to provide default entity
     * @return
     */
    public void checkCode(Identifiable target, String format, EntityProvider<Identifiable, String> provider, EntityProvider<Identifiable, String> defaultProvider) {
        checkCode(target, format, 5, provider, defaultProvider);
    }

    /**
     * Optimistic lock check
     *
     * @param lockable
     * @param olRepo
     */
    public void checkOptimisticLock(Lockable lockable, OptimisticLockRepository olRepo) {
        // Check if Lockable is newly added
        if (lockable.getId() <= 0L) {
            // Set lockable to 0
            lockable.setVersion(0L);
            return;
        }

        // Check if lockable is a valid version of Entity
        if (!olRepo.existsByIdAndVersion(lockable.getId(), lockable.getVersion())) {
            inconsistent("invalid_lock");
        }

        // Increase lock
        lockable.setVersion(lockable.getVersion() + 1);
    }

    /**
     * Mass optimistic lock check for multiple id
     *
     * @param ids
     * @param versions
     * @param olRepo
     */
    public void massCheckOptimisticLock(Long[] ids, Long[] versions, OptimisticLockRepository olRepo) {
        // Merge list of ids and list of versions to a list of tuple of (id-version)
        String[] tuples = IntStream.range(0, ids.length)
                .mapToObj(index -> String.format("%d-%d", ids[index], versions[index]))
                .toArray(String[]::new);

        // Check if all tuples are exists
        if (!olRepo.existsAllByTuplesOfIdAndVersion(tuples)) {
            inconsistent("invalid_locks");
        }
    }

    // ****************************
    // Protected API
    // ****************************

    /**
     * Generate code
     *
     * @param format
     * @param lengthOfNumber
     * @param provider
     * @return
     */
    protected String generateCode(String format, int lengthOfNumber, EntityProvider<Identifiable, String> provider) {
        // Get Product with "largest" code
        Identifiable identifiable = provider.provide(format + "%");
        // Get number part from code
        String alphanumeric = Objects.isNull(identifiable) ? "0" : identifiable.getCode().substring(format.length());

        // Generate format
        String f = "%s%0" + lengthOfNumber + "d";
        return String.format(f, format, Integer.parseInt(alphanumeric) + 1);
    }

    /**
     * Generate code
     *
     * @param format
     * @param provider
     * @return
     */
    protected String generateCode(String format, EntityProvider<Identifiable, String> provider) {
        return generateCode(format, 5, provider);
    }
}
