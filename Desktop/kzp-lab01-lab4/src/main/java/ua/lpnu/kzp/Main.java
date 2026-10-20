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


public class Main {

    public static void main(String[] args) {
        Path inputPath = Path.of("data", "input.csv");

        if (args.length >= 2 && args[0].equals("--input")) {
            inputPath = Path.of(args[1]);
        }

        try {
            // 1. Ініціалізація параметризованого сховища Repository<T>
            Repository<Screening> repository = new Repository<>();

            // Зчитування вхідних даних та заповнення Repository
            try (var lines = Files.lines(inputPath, StandardCharsets.UTF_8)) {
                lines.filter(line -> !line.isBlank())
                     .map(line -> {
                         try {
                             return Screening.fromCsv(line);
                         } catch (IllegalArgumentException e) {
                             System.out.println("Пропущено некоректний запис: " + line);
                             return null;
                         }
                     })
                     .filter(s -> s != null)
                     .forEach(repository::add);
            }

            List<Screening> screenings = repository.all();

            // 2. Розрахунок статистики та результатів
            double totalRevenue = screenings.stream()
                    .mapToDouble(Screening::calculateRevenue)
                    .sum();

            DoubleSummaryStatistics priceStats = ScreeningReport.ticketPriceStatistics(screenings);
            int validRecords = (int) priceStats.getCount();
            double averageTicketPrice = priceStats.getAverage();

            int maxSold = screenings.stream()
                    .mapToInt(Screening::getSold)
                    .max()
                    .orElse(0);

            TicketSales summary = new TicketSales(validRecords, totalRevenue, averageTicketPrice, maxSold);

            System.out.println("===== ОСНОВНІ РЕЗУЛЬТАТИ =====");
            System.out.println("Записів у Repository: " + repository.all().size());
            System.out.printf(Locale.ROOT, "Загальний дохід: %.2f грн%n", summary.totalRevenue());
            System.out.printf(Locale.ROOT, "Середня ціна квитка: %.2f грн%n", summary.averageTicketPrice());
            System.out.println("Максимально продано квитків: " + summary.maxSold());

            // 3. Демонстрація Stream API запитів
            System.out.println("\n===== STREAM API ЗАПИТИ (ВАРІАНТ 19) =====");

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

            // 4. Пошук через Optional
            System.out.println("\n6. Пошук (Optional):");
            Optional<Screening> notFound = ScreeningReport.findByFilmAndHall(screenings, "НеіснуючийФільм", 99);
            System.out.println("  Перевірка відсутнього сеансу: " + (notFound.isPresent() ? "Знайдено" : "Не знайдено (Optional.empty)"));

            // 5. ЛАБОРАТОРНА 5: РЕФЛЕКСИВНИЙ ЕКСПОРТ ТА ROUND-TRIP ПЕРЕВІРКА
            System.out.println("\n===== ЛАБОРАТОРНА 5: CSV ЕКСПОРТ ТА ROUND-TRIP =====");
            Path exportPath = Path.of("target", "screenings-export.csv");

            try {
                // А. Експорт даних репозиторію через CsvExporter
                CsvExporter.write(exportPath, Screening.class, repository.all());
                System.out.println("  1. Експорт у CSV успішно виконано -> " + exportPath.toAbsolutePath());

                // Б. Читання файла назад
                String csvText = Files.readString(exportPath, StandardCharsets.UTF_8);
                List<List<String>> parsedRows = CsvParser.parseDocument(csvText);

                System.out.println("  2. Заголовок CSV: " + String.join(", ", parsedRows.get(0)));

                // В. Відновлення об'єктів з CSV (пропускаючи заголовок)
                List<Screening> restored = parsedRows.stream()
                        .skip(1)
                        .map(fields -> {
                            try {
                                return Screening.fromFields(fields);
                            } catch (DataStorageException e) {
                                throw new IllegalStateException("Помилка відновлення об'єкта", e);
                            }
                        })
                        .toList();

                System.out.println("  3. Відновлено об'єктів: " + restored.size());

                // Г. Перевірка Round-Trip (порівняння вхідних даних та відновлених)
                boolean roundTripSuccess = repository.all().equals(restored);
                System.out.println("  4. Результат Round-trip (початкові == відновлені): " + roundTripSuccess);

            } catch (DataStorageException e) {
                System.err.println("  Помилка роботи зі сховищем CSV: " + e.getMessage());
                if (e.getCause() != null) {
                    System.err.println("  Першопричина: " + e.getCause().getMessage());
                }
            }

            // Збереження підсумкового текстового звіту
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