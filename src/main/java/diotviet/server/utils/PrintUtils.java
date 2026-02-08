package diotviet.server.utils;

import diotviet.server.annotations.PrintObject;
import diotviet.server.annotations.PrintTag;
import diotviet.server.structures.FakeJSON;
import diotviet.server.templates.Document.PrintableTag;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;

@Component
public class PrintUtils {

    // ****************************
    // Public API
    // ****************************

    /**
     * Get printable field of Entity
     *
     * @param entityClass
     * @return
     */
    public PrintableTag[] getPrintableTag(Class<?> entityClass) {
        // Trace and harvest
        return traceAndHarvestPrintTag(entityClass, new ArrayList<>(), "", "", null).toArray(new PrintableTag[0]);
    }

    /**
     * Get example of entityClass
     *
     * @param entityClass
     * @return
     */
    public FakeJSON getExample(Class<?> entityClass) {
        return traceAndHarvestPrintTagExample(entityClass, new ArrayList<>())[0];
    }

    // ****************************
    // Private API
    // ****************************

    /**
     * Trace and harvest Entity @PrintTag
     *
     * @param entityClass
     * @param harvested
     * @param prefix
     * @param parentKey
     * @param parentTag
     * @return
     */
    private ArrayList<PrintableTag> traceAndHarvestPrintTag(Class<?> entityClass, ArrayList<String> harvested, String prefix, String parentKey, PrintTag parentTag) {
        // Create output
        ArrayList<PrintableTag> printableFields = new ArrayList<>();
        // Get uncapitalize basename for identification
        String base = getBase(entityClass);

        // Cache the field's class, so it won't be harvested next time
        harvested.add(entityClass.getName());

        // Iterate through each field
        for (Method method : entityClass.getDeclaredMethods()) {
            // Get PrintTag Annotation
            PrintTag printTag = retrievePrintTag(method);
            // Check if ignored
            if (printTag.ignored()) {
                continue;
            }

            // Get target
            String target = printTag.value();
            // Get translation key
            String key = base + "_" + target;
            // Get data path
            String path = prefix + target;
            // Calculator child path prefix
            String childPathPrefix = prefix + target + "." + (printTag.isIterable() ? "{i}." : "");

            // Create subfield
            ArrayList<PrintableTag> subfields = new ArrayList<>();
            // Check if this is a nested Print Tag
            if (ArrayUtils.isNotEmpty(printTag.component())) {
                // Make sure does not re-harvest harvested field to avoid infinite references
                if (!harvested.contains(printTag.component()[0].getName())) {
                    // Harvest subfields
                    subfields = traceAndHarvestPrintTag(
                            printTag.component()[0],                        // Component (An Entity)
                            harvested,                                      // Harvested Entity
                            childPathPrefix,                                // Data path prefix
                            printTag.isIterable() ? key : parentKey,        // If self is iterable, it'll become a new parent for it child
                            printTag.isIterable() ? printTag : parentTag    // Same reason as above
                    );
                }
            }

            // Retrieve parent's isIterable flag
            boolean isParentIterable = Objects.nonNull(parentTag) && parentTag.isIterable();
            // Preprocess subfields
            PrintableTag[] preprocessedFields = preprocessSubfields(printTag, subfields, path, key);
            // Add new PrintableTag
            printableFields.add(
                    new PrintableTag(
                            printTag.sequence(),
                            key,                    // Translation key
                            path,                   // Data path
                            preprocessedFields,     // Subfields
                            printTag.isIterable(),  // Tag iterable flag
                            isParentIterable,       // Tag's parent iterable flag
                            parentKey,              // Tag's parent key
                            printTag.isIdentifier() // Tag's identifier
                    )
            );

        }

        // Sort
        printableFields.sort(Comparator.comparingInt(PrintableTag::sequence));
        return printableFields;
    }

    /**
     * Get base
     *
     * @param type
     * @return
     */
    private String getBase(Class<?> type) {
        // Check if annotation exists
        if (type.isAnnotationPresent(PrintObject.class)) {
            return StringUtils.uncapitalize(type.getAnnotation(PrintObject.class).value());
        }

        // Else, get class name as base
        return StringUtils.uncapitalize(type.getSimpleName());
    }

    /**
     * Get size of example
     *
     * @param type
     * @return
     */
    private int getSizeOfExample(Class<?> type) {
        // Else, get class name as base
        return type.isAnnotationPresent(PrintObject.class) ? type.getAnnotation(PrintObject.class).sizeOfExample() : 1;
    }

    /**
     * Retrieve Print Tag
     *
     * @param method
     * @return
     */
    private PrintTag retrievePrintTag(Method method) {
        // Return null (represent default value)
        return new PrintTag() {
            private PrintTag printTag = method.getAnnotation(PrintTag.class);
            private String methodName = method.getName();

            @Override
            public Class<? extends Annotation> annotationType() {
                return PrintTag.class;
            }

            @Override
            public String value() {
                return Objects.nonNull(printTag) && StringUtils.isNotBlank(printTag.value())
                        ? printTag.value()
                        : StringUtils.uncapitalize(methodName.substring(3));
            }

            @Override
            public int sequence() {
                return Objects.nonNull(printTag) ? printTag.sequence() : 0;
            }

            @Override
            public String[] example() {
                return Objects.nonNull(printTag) ? printTag.example() : new String[0];
            }

            @Override
            public Class<?>[] component() {
                return Objects.nonNull(printTag) ? printTag.component() : new Class[0];
            }

            @Override
            public boolean isIterable() {
                return Objects.nonNull(printTag) && printTag.isIterable();
            }

            @Override
            public boolean isIdentifier() {
                return Objects.nonNull(printTag) && printTag.isIdentifier();
            }

            @Override
            public boolean ignored() {
                return Objects.nonNull(printTag) && printTag.ignored();
            }
        };
    }

    /**
     * Preprocess subfields
     *
     * @param printTag
     * @param subfields
     * @param path
     * @param key
     * @return
     */
    private PrintableTag[] preprocessSubfields(PrintTag printTag, ArrayList<PrintableTag> subfields, String path, String key) {
        // Create output holder
        ArrayList<PrintableTag> tags = new ArrayList<>(subfields);

        // Depends on the characteristic of PrintTag, subfields can be overwritten
        if (printTag.isIdentifier()) {
            // Clear all
            tags.clear();
            // Add plain value Tag
            tags.add(new PrintableTag(0, "id_raw", path, new PrintableTag[0], false, false, key, true));
            // Add Barcode Tag
            tags.add(new PrintableTag(1, "id_bc", path, new PrintableTag[0], false, false, key, true));
            // Add QR Code Tag
            tags.add(new PrintableTag(2, "id_qr", path, new PrintableTag[0], false, false, key, true));
        }

        return tags.toArray(new PrintableTag[0]);
    }

    /**
     * Trace and harvest Entity @PrintTag's example
     *
     * @param entityClass
     * @param harvested
     * @return
     */
    private FakeJSON[] traceAndHarvestPrintTagExample(Class<?> entityClass, ArrayList<String> harvested) {
        // Create output
        FakeJSON[] objects = new FakeJSON[getSizeOfExample(entityClass)];
        // Initialize array
        for (int i = 0; i < objects.length; i++) objects[i] = new FakeJSON();

        // Cache the field's class, so it won't be harvested next time
        harvested.add(entityClass.getName());

        // Iterate through each field
        for (Method method : entityClass.getDeclaredMethods()) {
            // Get PrintTag Annotation
            PrintTag printTag = retrievePrintTag(method);

            // Get key
            String key = printTag.value();
            // Get example data
            String[] examples = printTag.example();
            // Create FakeJson [size] times to demonstrate example
            for (int i = 0; i < objects.length; i++) {
                // Json data
                Object data = null;
                // Check if PrintTag is a PrintObject
                if (ArrayUtils.isNotEmpty(printTag.component())) {
                    // Trace and harvest
                    FakeJSON[] sub = traceAndHarvestPrintTagExample(printTag.component()[0], harvested);
                    // Get first
                    data = sub.length == 1 ? sub[0] : sub;
                }

                // Put data
                objects[i].put(key, Objects.isNull(data) ? examples[i] : data);
            }
        }

        return objects;
    }
}
