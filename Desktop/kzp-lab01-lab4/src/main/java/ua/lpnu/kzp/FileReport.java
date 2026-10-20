package ua.lpnu.kzp;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileReport {

    public static void save(
            Path outputPath,
            int validRecords,
            double totalRevenue,
            double averageTicketPrice,
            int maxSold
    ) throws IOException {

        String report = String.format(
                "===== РЕЗУЛЬТАТИ =====%n"
                        + "Кількість правильних записів: %d%n"
                        + "Загальний дохід: %.2f грн%n"
                        + "Середня ціна квитка: %.2f грн%n"
                        + "Максимальна кількість проданих квитків: %d%n",
                validRecords,
                totalRevenue,
                averageTicketPrice,
                maxSold
        );

        Files.writeString(
                outputPath,
                report,
                StandardCharsets.UTF_8
        );
    }
}