package ua.lpnu.kzp;

import java.util.Locale;
import java.util.Objects;

/**
 * Описує незмінний запис про кіносеанс.
 */
public final class Screening {

    private final String film;
    private final int hall;
    private final double ticketPrice;
    private final int sold;
    private final int durationMin;

    /**
     * Конструктор, що створює об'єкт сеансу та перевіряє його інваріанти.
     *
     * @param film        назва фільму (не порожня)
     * @param hall        номер залу (> 0)
     * @param ticketPrice ціна квитка (>= 0, скінченне число)
     * @param sold        кількість проданих квитків (>= 0)
     * @param durationMin тривалість у хвилинах (> 0)
     * @throws IllegalArgumentException якщо параметри порушують інваріанти
     */
    public Screening(String film, int hall, double ticketPrice, int sold, int durationMin) {
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

        this.hall = hall;
        this.ticketPrice = ticketPrice;
        this.sold = sold;
        this.durationMin = durationMin;
    }

    /**
     * Статичний фабричний метод для створення сутності з CSV-рядка.
     *
     * @param line рядок у форматі "film;hall;ticketPrice;sold;durationMin"
     * @return об'єкт Screening
     * @throws IllegalArgumentException якщо формат або числа некоректні
     */
    public static Screening fromCsv(String line) {
        Objects.requireNonNull(line, "Рядок не може бути null");
        String[] fields = line.split(";", -1);

        if (fields.length != 5) {
            throw new IllegalArgumentException("Очікується рівно 5 полів");
        }

        try {
            String film = fields[0].trim();
            int hall = Integer.parseInt(fields[1].trim());
            double ticketPrice = Double.parseDouble(fields[2].trim());
            int sold = Integer.parseInt(fields[3].trim());
            int durationMin = Integer.parseInt(fields[4].trim());

            return new Screening(film, hall, ticketPrice, sold, durationMin);
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

    /**
     * Обчислює дохід від даного сеансу.
     *
     * @return валова выручка з квитків
     */
    public double calculateRevenue() {
        return ticketPrice * sold;
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT,
                "%s (Зал %d): ціна %.2f грн, продано %d, тривалість %d хв",
                film, hall, ticketPrice, sold, durationMin);
    }
}