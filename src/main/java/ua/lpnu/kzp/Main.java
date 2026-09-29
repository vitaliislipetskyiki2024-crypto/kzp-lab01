package ua.lpnu.kzp;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        Path inputPath = Path.of("data", "input.csv");

        if (args.length >= 2 && args[0].equals("--input")) {
            inputPath = Path.of(args[1]);
        }

        try {
            List<String> lines = Files.readAllLines(inputPath, StandardCharsets.UTF_8);
            List<Screening> screenings = new ArrayList<>();

            for (String line : lines) {
                if (line.isBlank()) {
                    continue;
                }
                try {
                    Screening screening = Screening.fromCsv(line);
                    screenings.add(screening);
                } catch (IllegalArgumentException e) {
                    System.out.println("Пропущено неправильний запис: " + line);
                }
            }

            int validRecords = screenings.size();
            double totalRevenue = 0.0;
            double totalTicketPrice = 0.0;
            int maxSold = 0;

            for (Screening s : screenings) {
                totalRevenue += s.calculateRevenue();
                totalTicketPrice += s.getTicketPrice();
                if (s.getSold() > maxSold) {
                    maxSold = s.getSold();
                }
            }

            double averageTicketPrice = validRecords > 0 ? totalTicketPrice / validRecords : 0.0;

            TicketSales summary = new TicketSales(validRecords, totalRevenue, averageTicketPrice, maxSold);

            System.out.println();
            System.out.println("===== РЕЗУЛЬТАТИ =====");
            System.out.println("Кiлькiсть правильних записiв: " + summary.validRecords());
            System.out.printf("Загальний дохiд: %.2f грн%n", summary.totalRevenue());
            System.out.printf("Середня цiна квитка: %.2f грн%n", summary.averageTicketPrice());
            System.out.println("Максимальна кiлькiсть проданих квиткiв: " + summary.maxSold());

            try {
                FileReport.save(
                        Path.of("report.txt"),
                        summary.validRecords(),
                        summary.totalRevenue(),
                        summary.averageTicketPrice(),
                        summary.maxSold()
                );
                System.out.println("Звiт збережено у файл: report.txt");
            } catch (IOException e) {
                System.out.println("Помилка запису звiту: " + e.getMessage());
            }

        } catch (IOException e) {
            System.out.println("Помилка читання файлу: " + inputPath);
            System.out.println(e.getMessage());
        }
    }
}