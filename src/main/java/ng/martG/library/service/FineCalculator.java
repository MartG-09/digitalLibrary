package ng.martG.library.service;

import ng.martG.library.config.LibraryProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Fine = (days late) x (rate per day).
 * Returning ON the due date is not late; one day after is 1 day late.
 * The bean is named "fineCalculator" so templates can call it.
 */
@Component("fineCalculator")
public class FineCalculator {

    private final BigDecimal ratePerDay;

    @Autowired
    public FineCalculator(LibraryProperties properties) {
        this(properties.getFinePerDay());
    }

    /** Handy for unit tests. */
    public FineCalculator(BigDecimal ratePerDay) {
        this.ratePerDay = ratePerDay;
    }

    public BigDecimal calculate(LocalDate dueDate, LocalDate returnedOn) {
        long daysLate = ChronoUnit.DAYS.between(dueDate, returnedOn);
        if (daysLate <= 0) {
            return BigDecimal.ZERO.setScale(2);
        }
        return ratePerDay.multiply(BigDecimal.valueOf(daysLate)).setScale(2);
    }
}
