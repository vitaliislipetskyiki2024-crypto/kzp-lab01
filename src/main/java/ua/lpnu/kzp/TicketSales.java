package ua.lpnu.kzp;

import java.util.Locale;

/**
 * 
 *
 * @param validRecords       кількість правильних записів
 * @param totalRevenue       загальний дохід
 * @param averageTicketPrice середня ціна квитка
 * @param maxSold            максимальна кількість проданих квитків
 */
public record TicketSales(
        int validRecords,
        double totalRevenue,
        double averageTicketPrice,
        int maxSold
) {
    /**
     * 
     */
    public TicketSales {
        if (validRecords < 0 || maxSold < 0) {
            throw new IllegalArgumentException("Кількісні показники не можуть бути від'ємними");
        }
        if (totalRevenue < 0 || averageTicketPrice < 0
                || !Double.isFinite(totalRevenue)
                || !Double.isFinite(averageTicketPrice)) {
            throw new IllegalArgumentException("Фінансові показники мають бути скінченними та невід'ємними");
        }
    }

    @Override
    public String toString() {
        return String.format(Locale.ROOT,
                "TicketSales[validRecords=%d, totalRevenue=%.2f, averageTicketPrice=%.2f, maxSold=%d]",
                validRecords, totalRevenue, averageTicketPrice, maxSold);
    }
}