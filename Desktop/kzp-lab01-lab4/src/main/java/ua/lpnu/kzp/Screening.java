package ua.lpnu.kzp;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

/**
 * Абстрактний базовий клас сеансу в кінотеатрі з анотаціями для CSV-експорту.
 */
public abstract class Screening {

    @CsvColumn("тривалість")
    private final int durationMin;

    @CsvColumn("фільм")
    private final String film;

    @CsvColumn("зал")
    private final int hall;

    @CsvColumn("категорія")
    private final ScreeningKind kind;

    @CsvColumn("продано")
    private final int sold;

    @CsvColumn("ціна")
    private final double ticketPrice;

    /**
     * Конструктор для базових полів сеансу з повною валідацією даних.
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
     * Створює об'єкт з розібраного списку CSV-полів (для Round-trip імпорту).
     * Порядок полів відповідає алфавітному порядку назв полів Java:
     * [0] durationMin, [1] film, [2] hall, [3] kind, [4] sold, [5] ticketPrice
     */
    public static Screening fromFields(List<String> fields) throws DataStorageException {
        if (fields == null || fields.size() < 6) {
            throw new DataStorageException("Очікується щонайменше 6 полів CSV для сеансу");
        }

        try {
            int durationMin = Integer.parseInt(fields.get(0).trim());
            String film = fields.get(1).trim();
            int hall = Integer.parseInt(fields.get(2).trim());
            ScreeningKind.valueOf(fields.get(3).trim());
            int sold = Integer.parseInt(fields.get(4).trim());
            double ticketPrice = Double.parseDouble(fields.get(5).trim());

            if (fields.size() >= 7) {
                double vipSurcharge = Double.parseDouble(fields.get(6).trim());
                return new PremiumScreening(film, hall, ticketPrice, sold, durationMin, vipSurcharge);
            }

            return new RegularScreening(film, hall, ticketPrice, sold, durationMin);
        } catch (IllegalArgumentException e) {
            throw new DataStorageException("Некоректні дані у полях CSV-запису", e);
        }
    }

    /**
     * Фабричний метод для створення об'єкта з початкового файлу CSV (роздільник крапка з комою).
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
        return hall == screening.hall
                && Double.compare(screening.ticketPrice, ticketPrice) == 0
                && sold == screening.sold
                && durationMin == screening.durationMin
                && film.equals(screening.film)
                && kind == screening.kind;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getClass(), film, hall, ticketPrice, sold, durationMin, kind);
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT,
                "[%s] %s (Зал %d): ціна %.2f грн, продано %d, тривалість %d хв, виторг %.2f грн",
                kind.label(), film, hall, ticketPrice, sold, durationMin, calculateRevenue());
    }

    
    @Override
    protected final void finalize() {
        // Захист від Finalizer-атак для SpotBugs
    }
}