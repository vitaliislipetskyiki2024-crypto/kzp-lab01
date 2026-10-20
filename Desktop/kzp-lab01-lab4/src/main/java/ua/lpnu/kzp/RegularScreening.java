package ua.lpnu.kzp;

/**
 * Звичайний сеанс у кінотеатрі.
 */
public final class RegularScreening extends Screening {

    public RegularScreening(String film, int hall, double ticketPrice, int sold, int durationMin) {
        super(film, hall, ticketPrice, sold, durationMin, ScreeningKind.REGULAR);
    }

    /**
     * Виторг = кількість квитків * ціна квитка
     */
    @Override
    public double calculateRevenue() {
        return getSold() * getTicketPrice();
    }
}