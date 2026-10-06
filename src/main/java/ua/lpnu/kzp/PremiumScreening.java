package ua.lpnu.kzp;

/**
 * Преміум-сеанс у кінотеатрі з VIP-надбавкою.
 */
public final class PremiumScreening extends Screening {

    private final double vipSurcharge;

    public PremiumScreening(String film, int hall, double ticketPrice, int sold, int durationMin, double vipSurcharge) {
        super(film, hall, ticketPrice, sold, durationMin, ScreeningKind.PREMIUM);
        if (vipSurcharge < 0 || !Double.isFinite(vipSurcharge)) {
            throw new IllegalArgumentException("VIP-надбавка має бути невід'ємною та скінченною");
        }
        this.vipSurcharge = vipSurcharge;
    }

    public double getVipSurcharge() {
        return vipSurcharge;
    }

    /**
     * Виторг = кількість квитків * (ціна квитка + VIP-надбавка)
     */
    @Override
    public double calculateRevenue() {
        return getSold() * (getTicketPrice() + vipSurcharge);
    }
}