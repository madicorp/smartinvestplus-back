package net.madicorp.smartinvestplus.stockexchange.service;

import net.madicorp.smartinvestplus.stockexchange.domain.CloseRate;

import java.time.LocalDate;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * User: sennen
 * Date: 08/07/2016
 * Time: 14:50
 */
public class CloseRateIterator implements Iterator<CloseRate>, Iterable<CloseRate> {
    private final LocalDate endDate;
    private final Iterator<CloseRate> existingCloseRateIterator;
    private final NextDayProvider nextDayProvider;
    // TODO rename should express the fact that it gets the closest close rate in the past outside current interval
    private final PreviousCloseRateProvider previousCloseRateProvider;
    private final CloseRateProvider closeRateProvider;
    private LocalDate currentDate;
    private CloseRate currentExistingCloseRate;
    private CloseRate previousCloseRate;
    private boolean existingCloseRateYielded = true;

    CloseRateIterator(LocalDate startDate, LocalDate endDate,
                      Iterator<CloseRate> existingCloseRateIterator,
                      NextDayProvider nextDayProvider, PreviousCloseRateProvider previousCloseRateProvider,
                      CloseRateProvider closeRateProvider) {
        this.endDate = endDate;
        this.existingCloseRateIterator = existingCloseRateIterator;
        this.nextDayProvider = nextDayProvider;
        this.previousCloseRateProvider = previousCloseRateProvider;
        this.currentDate = startDate;
        this.closeRateProvider = closeRateProvider;
    }

    @Override
    public Iterator<CloseRate> iterator() {
        return this;
    }

    @Override
    public boolean hasNext() {
        return existingCloseRateIterator.hasNext() || hasNotYieldEndDateCloseRate();
    }

    @Override
    public CloseRate next() {
        if (!hasNext()) {
            throw new NoSuchElementException();
        }
        if (canRetrieveExistingCloseRate()) {
            // Retrieving existing close rate
            currentExistingCloseRate = existingCloseRateIterator.next();
        }
        if (mustYieldExistingCloseRate()) {
            // It is time for existing close rate to be yielded
            existingCloseRateYielded = true;
            previousCloseRate = merge(closeRateProvider.get(), currentExistingCloseRate);
            currentDate = nextDayProvider.apply(currentDate);
            return currentExistingCloseRate;
        }
        // Generating one because we have none for this date
        existingCloseRateYielded = false;
        return generateCloseRate();
    }

    private boolean mustYieldExistingCloseRate() {
        return currentExistingCloseRate != null && currentDate.isEqual(currentExistingCloseRate.getDate());
    }

    private boolean canRetrieveExistingCloseRate() {
        return existingCloseRateIterator.hasNext() && existingCloseRateYielded;
    }

    private CloseRate generateCloseRate() {
        PreviousCloseRateProvider provider;
        if (availablePreviousCloseRate()) {
            provider = (date) -> previousCloseRate;
        } else {
            provider = previousCloseRateProvider;
        }
        return generateCloseRate(provider);
    }

    private boolean availablePreviousCloseRate() {
        return previousCloseRate != null;
    }

    private CloseRate generateCloseRate(PreviousCloseRateProvider provider) {
        CloseRate generatedCloseRate = new CloseRate();
        generatedCloseRate.setDate(currentDate);
        generatedCloseRate.setRate(provider.apply(currentDate).getRate());
        generatedCloseRate.setGenerated(true);
        previousCloseRate = merge(closeRateProvider.get(), generatedCloseRate);
        currentDate = nextDayProvider.apply(currentDate);
        return generatedCloseRate;
    }

    private CloseRate merge(CloseRate closeRateWithSecurity, CloseRate closeRateWithDateAndRate) {
        CloseRate closeRate = new CloseRate();
        closeRate.setSecurity(closeRateWithSecurity.getSecurity());
        closeRate.setDate(closeRateWithDateAndRate.getDate());
        closeRate.setRate(closeRateWithDateAndRate.getRate());
        return closeRate;
    }

    private boolean hasNotYieldEndDateCloseRate() {
        return currentDate.isBefore(endDate) || currentDate.isEqual(endDate);
    }
}
