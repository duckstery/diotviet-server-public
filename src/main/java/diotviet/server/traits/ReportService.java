package diotviet.server.traits;

import diotviet.server.utils.OtherUtils;
import diotviet.server.views.Report.ReportView;

import java.lang.reflect.Method;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public abstract class ReportService<R extends ReportView> {

    // ****************************
    // Protected API
    // ****************************

    /**
     * Group IncomeReportView by month YearMonth of LocalDate
     *
     * @param report
     * @return
     */
    protected <I extends R> List<R> groupReportByMonth(List<R> report, Class<I> implementation) {
        // Create holder
        List<R> reportByMonth = new ArrayList<>();
        // Current month
        YearMonth current = null;
        // Current IncomeReportByMonth
        I entryByMonth = null;

        // Get list of [R] getters
        List<Method> getters = getGetters();
        // Get list of [I] adders
        List<Method> adders = getAdders(getters, implementation);

        try {
            // Iterate through each
            for (ReportView entry : report) {
                // Get unit as LocalDate
                YearMonth time = YearMonth.parse(entry.getTime(), DateTimeFormatter.ISO_DATE);
                // Check if current month is null or not equals
                if (Objects.isNull(current) || !current.equals(time)) {
                    // Assign current month
                    current = time;
                    // Create new entry and add to list
                    reportByMonth.add(entryByMonth = implementation.getDeclaredConstructor(YearMonth.class).newInstance(current));
                }

                // Loop through each getter and adder
                for (int i = 0; i < getters.size(); i++) {
                    // Get value from getter
                    Object value = getters.get(i).invoke(entry);
                    // Add value
                    if (Objects.nonNull(adders.get(i))) {
                        adders.get(i).invoke(entryByMonth, value);
                    } else {
                        System.out.println(String.format("Implementation does not have adder for %s", getters.get(i).getName()));
                    }
                }
            }
        } catch (Exception e) {
            // Too much, just ignored it
            e.printStackTrace();
        }

        return reportByMonth;
    }

    // ****************************
    // Private API
    // ****************************

    /**
     * Get all R getters
     *
     * @return
     */
    private List<Method> getGetters() {
        // Get list of getters (start with "get" but not "setTime")
        return Arrays
                // Target will be the Parameterized Type [R]
                .stream(OtherUtils.getTypeArguments(getClass())[0].getMethods())
                .filter(method -> method.getName().startsWith("get") && !method.getName().equals("getTime"))
                .toList();
    }

    /**
     * Get all adders base on setters
     *
     * @param getter
     * @return
     */
    private <I extends R> List<Method> getAdders(List<Method> getters, Class<I> implementation) {
        // Create output list
        List<Method> adders = new ArrayList<>();

        // Get list of adders
        for (Method getter : getters) {
            try {
                // Get adder name
                String adderName = getter.getName().replace("get", "add");
                // Get adder and add to output list
                adders.add(implementation.getMethod(adderName, Long.class));
            } catch (Exception e) {
                System.out.println(e.getMessage());
                adders.add(null);
            }
        }

        return adders;
    }
}
