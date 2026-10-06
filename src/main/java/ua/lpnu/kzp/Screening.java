package ua.lpnu.kzp;

import java.util.Locale;
import java.util.Objects;

/**
 * Абстрактний базовий клас сеансу в кінотеатрі.
 */
public abstract class Screening {

    private final String film;
    private final int hall;
    private final double ticketPrice;
    private final int sold;
    private final int durationMin;
    private final ScreeningKind kind;

    /**
     * Конструктор для базових полів сеансу з повною валідацією даних.
     *
     * @param film        назва фільму
     * @param hall        номер залу
     * @param ticketPrice ціна квитка
     * @param sold        кількість проданих квитків
     * @param durationMin тривалість у хвилинах
     * @param kind        категорія сеансу (enum)
     * @throws IllegalArgumentException якщо передано некоректні значення
     */
    protected Screening(String film, int hall, double ticketPrice, int sold, int durationMin, ScreeningKind kind) {
        this.film = Objects.requireNonNull(film, "Назва фільму не може бути null");
        if (film.isBlank()) {
            throw new IllegalArgumentException("Назва фільму не може бути порожньою");
        }
        if (hall <= 0) {
            throw new IllegalArgumentException("Номер залу має бути додатним");
        }
        if (ticketPrice < 0 || !Double.isFinite(ticketPrice)) {
            throw new IllegalArgumentException("Ціна квитка має бути невід'ємною та скінченною");
        }
        if (sold < 0) {
            throw new IllegalArgumentException("Кількість проданих квитків має бути невід'ємною");
        }
        if (durationMin <= 0) {
            throw new IllegalArgumentException("Тривалість має бути додатною");
        }
        this.kind = Objects.requireNonNull(kind, "Категорія сеансу не може бути null");

        this.hall = hall;
        this.ticketPrice = ticketPrice;
        this.sold = sold;
        this.durationMin = durationMin;
    }

    /**
     * Фабричний метод для створення об'єкта підкласу Screening із CSV-рядка.
     *
     * @param line рядок у формате CSV (5 або 6 полів через крапку з комою)
     * @return екземпляр RegularScreening або PremiumScreening
     * @throws IllegalArgumentException якщо формат рядка чи дані некоректні
     */
    public static Screening fromCsv(String line) {
        Objects.requireNonNull(line, "Рядок не може бути null");
        String[] fields = line.split(";", -1);

        if (fields.length < 5 || fields.length > 6) {
            throw new IllegalArgumentException("Очікується 5 (звичайний) або 6 (преміум) полів");
        }

        try {
            String film = fields[0].trim();
            int hall = Integer.parseInt(fields[1].trim());
            double ticketPrice = Double.parseDouble(fields[2].trim());
            int sold = Integer.parseInt(fields[3].trim());
            int durationMin = Integer.parseInt(fields[4].trim());

            if (fields.length == 6) {
                double vipSurcharge = Double.parseDouble(fields[5].trim());
                return new PremiumScreening(film, hall, ticketPrice, sold, durationMin, vipSurcharge);
            }

            return new RegularScreening(film, hall, ticketPrice, sold, durationMin);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Числове поле має помилковий формат", e);
        }
    }

    public String getFilm() {
        return film;
    }

    public int getHall() {
        return hall;
    }

    public double getTicketPrice() {
        return ticketPrice;
    }

    public int getSold() {
        return sold;
    }

    public int getDurationMin() {
        return durationMin;
    }

    public ScreeningKind getKind() {
        return kind;
    }

    /**
     * Поліморфний метод обчислення виторгу сеансу.
     * Реалізується у конкретних підкласах.
     *
     * @return сума виторгу в гривнях
     */
    public abstract double calculateRevenue();

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null || getClass() != other.getClass()) {
            return false;
        }
        Screening screening = (Screening) other;
        return hall == screening.hall && film.equals(screening.film);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass(), film, hall);
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT,
                "[%s] %s (Зал %d): ціна %.2f грн, продано %d, тривалість %d хв, виторг %.2f грн",
                kind.label(), film, hall, ticketPrice, sold, durationMin, calculateRevenue());
    }
}