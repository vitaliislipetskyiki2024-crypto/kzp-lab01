package ua.lpnu.kzp;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        Path inputPath = Path.of("data", "input.csv");

        // Якщо передано --input, використовуємо вказаний файл
        if (args.length >= 2 && args[0].equals("--input")) {
            inputPath = Path.of(args[1]);
        }

        try {
            List<String> lines = Files.readAllLines(
                    inputPath,
                    StandardCharsets.UTF_8
            );

            int validRecords = 0;
            double totalRevenue = 0.0;
            double totalTicketPrice = 0.0;
            int maxSold = 0;

            for (String line : lines) {

                // Пропускаємо порожні рядки
                if (line.isBlank()) {
                    continue;
                }

                String[] fields = line.split(";", -1);

                // Має бути рівно 5 полів
                if (fields.length != 5) {
                    System.out.println("Пропущено неправильний запис: " + line);
                    continue;
                }

                String film = fields[0].trim();

                try {
                    int hall = Integer.parseInt(fields[1].trim());
                    double ticketPrice = Double.parseDouble(fields[2].trim());
                    int sold = Integer.parseInt(fields[3].trim());
                    int durationMin = Integer.parseInt(fields[4].trim());

                    // Перевірка правильності даних
                    if (film.isEmpty()
                            || hall <= 0
                            || ticketPrice < 0
                            || sold < 0
                            || durationMin <= 0) {

                        System.out.println("Пропущено неправильний запис: " + line);
                        continue;
                    }

                    // Запис правильний
                    validRecords++;

                    // Загальний дохід
                    totalRevenue += ticketPrice * sold;

                    // Для середньої ціни квитка
                    totalTicketPrice += ticketPrice;

                    // Максимальна кількість проданих квитків
                    if (sold > maxSold) {
                        maxSold = sold;
                    }

                } catch (NumberFormatException e) {
                    System.out.println("Пропущено неправильний запис: " + line);
                }
            }

            double averageTicketPrice = 0.0;

            if (validRecords > 0) {
                averageTicketPrice = totalTicketPrice / validRecords;
            }
// Формат запису: film;hall;ticketPrice;sold;durationMin
            System.out.println();
            System.out.println("===== РЕЗУЛЬТАТИ =====");
            System.out.println("Кiлькiсть правильних записiв: " + validRecords);
            System.out.printf("Загальний дохiд: %.2f грн%n", totalRevenue);
            System.out.printf("Середня цiна квитка: %.2f грн%n", averageTicketPrice);
            System.out.println("Максимальна кiлькiсть проданих квиткiв: " + maxSold);

        } catch (IOException e) {
            System.out.println("Помилка читання файлу: " + inputPath);
            System.out.println(e.getMessage());
        }
    }
}

