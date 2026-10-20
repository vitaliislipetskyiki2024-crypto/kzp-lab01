package ua.lpnu.kzp;

import java.util.Comparator;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public final class ScreeningReport {

    private ScreeningReport() {
        
    }

    /**
     * 1. Фільтр: відбір сеансів, де продано квитків понад вказаний поріг.
     */
    public static List<Screening> filterBySoldThreshold(List<Screening> screenings, int threshold) {
        return streamOf(screenings)
                .filter(s -> s.getSold() > threshold)
                .toList();
    }

    /**
     * 2. Перетворення: отримання списку унікальних відсортованих назв фільмів.
     */
    public static List<String> extractFilmTitles(List<Screening> screenings) {
        return streamOf(screenings)
                .map(Screening::getFilm)
                .distinct()
                .sorted()
                .toList();
    }

    /**
     * 3. Групування: обчислення сумарного виторгу за залами.
     */
    public static Map<Integer, Double> revenueByHall(List<Screening> screenings) {
        return streamOf(screenings)
                .collect(Collectors.groupingBy(
                        Screening::getHall,
                        Collectors.summingDouble(Screening::calculateRevenue)
                ));
    }

    /**
     * 4. Зведена статистика за ціною квитка.
     */
    public static DoubleSummaryStatistics ticketPriceStatistics(List<Screening> screenings) {
        return streamOf(screenings)
                .collect(Collectors.summarizingDouble(Screening::getTicketPrice));
    }

    /**
     * 5. Top-N: відбір кращих N сеансів за кількістю проданих квитків (за спаданням),
     * а при однаковій кількості — за тривалістю (за зростанням).
     */
    public static List<Screening> topBySoldAndDuration(List<Screening> screenings, int limit) {
        if (limit < 0) {
            throw new IllegalArgumentException("Ліміт не може бути від'ємним");
        }

        Comparator<Screening> comparator = Comparator
                .comparingInt(Screening::getSold)
                .reversed()
                .thenComparingInt(Screening::getDurationMin);

        return streamOf(screenings)
                .sorted(comparator)
                .limit(limit)
                .toList();
    }

    /**
     * Пошук (Optional): перший сеанс за назвою фільму та номером залу.
     */
    public static Optional<Screening> findByFilmAndHall(List<Screening> screenings, String film, int hall) {
        Objects.requireNonNull(film, "Назва фільму не може бути null");
        return streamOf(screenings)
                .filter(s -> s.getFilm().equalsIgnoreCase(film) && s.getHall() == hall)
                .findFirst();
    }

    private static Stream<Screening> streamOf(List<Screening> screenings) {
        return Objects.requireNonNull(screenings, "Список сеансів не може бути null").stream();
    }
}