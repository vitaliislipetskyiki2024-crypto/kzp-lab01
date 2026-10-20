package ua.lpnu.kzp;

import static org.junit.jupiter.api.Assertions.*;

import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class ScreeningReportTest {

    private final List<Screening> testScreenings = List.of(
            new RegularScreening("Дюна", 1, 150.0, 10, 120),
            new PremiumScreening("Аватар", 2, 200.0, 15, 190, 50.0),
            new RegularScreening("Оппенгеймер", 3, 180.0, 8, 180),
            new PremiumScreening("Бетмен", 1, 220.0, 12, 175, 40.0),
            new RegularScreening("Інтерстеллар", 2, 160.0, 3, 169),
            new RegularScreening("Матриця", 3, 140.0, 15, 136)
    );

    @Test
    void testFilterBySoldThreshold() {
        List<Screening> result = ScreeningReport.filterBySoldThreshold(testScreenings, 10);
        assertEquals(3, result.size()); // Аватар (15), Бетмен (12), Матриця (15)
    }

    @Test
    void testExtractFilmTitles() {
        List<String> titles = ScreeningReport.extractFilmTitles(testScreenings);
        assertEquals(6, titles.size());
        assertTrue(titles.containsAll(List.of("Аватар", "Бетмен", "Дюна", "Інтерстеллар", "Матриця", "Оппенгеймер")));
    }

    @Test
    void testRevenueByHall() {
        Map<Integer, Double> revenues = ScreeningReport.revenueByHall(testScreenings);
        assertEquals(3, revenues.size());
        // Зал 1: Дюна (150*10 = 1500) + Бетмен (12*(220+40) = 3120) = 4620.0
        assertEquals(4620.0, revenues.get(1), 0.001);
    }

    @Test
    void testTicketPriceStatistics() {
        DoubleSummaryStatistics stats = ScreeningReport.ticketPriceStatistics(testScreenings);
        assertEquals(6, stats.getCount());
        assertEquals(140.0, stats.getMin(), 0.001);
        assertEquals(220.0, stats.getMax(), 0.001);
    }

    @Test
    void testTopBySoldAndDuration() {
        List<Screening> top = ScreeningReport.topBySoldAndDuration(testScreenings, 5);
        assertEquals(5, top.size());
        // При однаковій кількості проданих (15) першим має бути фільм із меншою тривалістю ("Матриця", 136 хв vs "Аватар", 190 хв)
        assertEquals("Матриця", top.get(0).getFilm());
        assertEquals("Аватар", top.get(1).getFilm());
    }

    @Test
    void testTopWithNegativeLimitThrowsException() {
        assertThrows(IllegalArgumentException.class, () ->
                ScreeningReport.topBySoldAndDuration(testScreenings, -1));
    }

    @Test
    void testFindByFilmAndHallFound() {
        Optional<Screening> found = ScreeningReport.findByFilmAndHall(testScreenings, "Дюна", 1);
        assertTrue(found.isPresent());
        assertEquals("Дюна", found.get().getFilm());
    }

    @Test
    void testFindByFilmAndHallNotFound() {
        Optional<Screening> found = ScreeningReport.findByFilmAndHall(testScreenings, "Титанік", 1);
        assertTrue(found.isEmpty());
    }

    @Test
    void testHandlesEmptyInput() {
        List<Screening> empty = List.of();
        assertTrue(ScreeningReport.filterBySoldThreshold(empty, 5).isEmpty());
        assertEquals(0, ScreeningReport.ticketPriceStatistics(empty).getCount());
        assertTrue(ScreeningReport.findByFilmAndHall(empty, "Дюна", 1).isEmpty());
    }
}