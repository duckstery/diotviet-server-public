package diotviet.server.views.Report.impl;

import diotviet.server.views.Report.IncomeReportView;
import lombok.Data;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

@Data
public class IncomeReportByMonth implements IncomeReportView {

    // ****************************
    // Properties
    // ****************************

    /**
     * Time
     */
    private String time;
    /**
     * Expected income;
     */
    private Long expectedIncome;
    /**
     * Real income inside
     */
    private Long realIncomeInside;
    /**
     * Real income outside
     */
    private Long realIncomeOutside;
    /**
     * Usage
     */
    private Long usage;

    // ****************************
    // Private API
    // ****************************

    /**
     * Constructor
     *
     * @param yearMonth
     */
    public IncomeReportByMonth(YearMonth yearMonth) {
        this.time = yearMonth.format(DateTimeFormatter.ofPattern("yyyy-MM"));
        this.expectedIncome = 0L;
        this.realIncomeInside = 0L;
        this.realIncomeOutside = 0L;
        this.usage = 0L;
    }

    /**
     * Sum expectedIncome
     *
     * @param value
     */
    public void addExpectedIncome(Long value) {
        this.expectedIncome += value;
    }

    /**
     * Sum expectedIncome
     *
     * @param value
     */
    public void addRealIncomeInside(Long value) {
        this.realIncomeInside += value;
    }

    /**
     * Sum expectedIncome
     *
     * @param value
     */
    public void addRealIncomeOutside(Long value) {
        this.realIncomeOutside += value;
    }

    /**
     * Sum expectedIncome
     *
     * @param value
     */
    public void addUsage(Long value) {
        this.usage += value;
    }
}
