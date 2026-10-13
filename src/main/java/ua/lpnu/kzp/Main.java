package ua.lpnu.kzp;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        Path inputPath = Path.of("data", "input.csv");

        if (args.length >= 2 && args[0].equals("--input")) {
            inputPath = Path.of(args[1]);
        }

        try {
            // Зчитування та збірка списку за допомогою Stream API
            List<Screening> screenings = Files.lines(inputPath, StandardCharsets.UTF_8)
                    .filter(line -> !line.isBlank())
                    .map(line -> {
                        try {
                            return Screening.fromCsv(line);
                        } catch (IllegalArgumentException e) {
                            System.out.println("Пропущено некоректний запис: " + line);
                            return null;
                        }
                    })
                    .filter(s -> s != null)
                    .toList();

            // 1. Поліморфний розрахунок загального виторгу
            double totalRevenue = screenings.stream()
                    .mapToDouble(Screening::calculateRevenue)
                    .sum();

            // 2. Зведена статистика цін (Stream API)
            DoubleSummaryStatistics priceStats = ScreeningReport.ticketPriceStatistics(screenings);
            int validRecords = (int) priceStats.getCount();
            double averageTicketPrice = priceStats.getAverage();

            int maxSold = screenings.stream()
                    .mapToInt(Screening::getSold)
                    .max()
                    .orElse(0);

            TicketSales summary = new TicketSales(validRecords, totalRevenue, averageTicketPrice, maxSold);

            System.out.println("===== ОСНОВНІ РЕЗУЛЬТАТИ =====");
            System.out.println("Кількість правильних записів: " + summary.validRecords());
            System.out.printf(Locale.ROOT, "Загальний дохід: %.2f грн%n", summary.totalRevenue());
            System.out.printf(Locale.ROOT, "Середня ціна квитка: %.2f грн%n", summary.averageTicketPrice());
            System.out.println("Максимальна кількість проданих квитків: " + summary.maxSold());

            // Демонстрація 5 запитів Stream API 
            System.out.println("\n===== STREAM API ЗАПИТИ =====");

            System.out.println("\n1. Сеанси, де продано понад 5 квитків:");
            ScreeningReport.filterBySoldThreshold(screenings, 5)
                    .forEach(s -> System.out.println("  - " + s.getFilm() + " (Зал " + s.getHall() + "): " + s.getSold() + " шт."));

            System.out.println("\n2. Унікальні назви фільмів:");
            System.out.println("  " + ScreeningReport.extractFilmTitles(screenings));

            System.out.println("\n3. Виторг за залами:");
            Map<Integer, Double> hallRevenues = ScreeningReport.revenueByHall(screenings);
            hallRevenues.forEach((hall, rev) ->
                    System.out.printf(Locale.ROOT, "  Зал %d: %.2f грн%n", hall, rev));

            System.out.println("\n4. Зведена статистика ціни квитка:");
            System.out.printf(Locale.ROOT, "  Min: %.2f, Max: %.2f, Avg: %.2f, Sum: %.2f%n",
                    priceStats.getMin(), priceStats.getMax(), priceStats.getAverage(), priceStats.getSum());

            System.out.println("\n5. Топ-5 сеансів за проданими квитками:");
            ScreeningReport.topBySoldAndDuration(screenings, 5)
                    .forEach(s -> System.out.println("  - " + s.getFilm() + " | Продано: " + s.getSold() + " | Хв: " + s.getDurationMin()));

            // 6. Інтерактивний та автоматичний пошук через Optional
            System.out.println("\n6. Пошук (Optional):");
            
            // Статична перевірка для відсутнього елемента (поверне Optional.empty)
            Optional<Screening> notFound = ScreeningReport.findByFilmAndHall(screenings, "ФільмЯкогоНемає", 99);
            System.out.println("  Перевірка неіснуючого фільму: " + (notFound.isPresent() ? "Знайдено" : "Не знайдено (Optional.empty)"));

            // Інтерактивний пошук з клавіатури
            Scanner scanner = new Scanner(System.in, java.nio.charset.Charset.defaultCharset());
            System.out.println("\n  --- Інтерактивний пошук ---");
            System.out.print("  Введіть назву фільму для пошуку: ");
            String userFilm = scanner.nextLine().trim();

            System.out.print("  Введіть номер залу: ");
            int userHall = 1;
            try {
                userHall = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("  Некоректний номер залу, використано зал 1 за замовчуванням.");
            }

            Optional<Screening> found = ScreeningReport.findByFilmAndHall(screenings, userFilm, userHall);
            if (found.isPresent()) {
                System.out.println("  Результат -> Знайдено: " + found.get());
            } else {
                System.out.println("  Результат -> На жаль, сеанс '" + userFilm + "' у залі " + userHall + " не знайдено (Optional.empty).");
            }

            // Збереження звіту
            try {
                FileReport.save(
                        Path.of("report.txt"),
                        summary.validRecords(),
                        summary.totalRevenue(),
                        summary.averageTicketPrice(),
                        summary.maxSold()
                );
                System.out.println("\nЗвіт збережено у файл: report.txt");
            } catch (IOException e) {
                System.out.println("Помилка запису звіту: " + e.getMessage());
            }

        } catch (IOException e) {
            System.out.println("Помилка читання файлу: " + inputPath);
            System.out.println(e.getMessage());
        }
    }
}