package ua.lpnu.kzp;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ScreeningTest {

    @Test
    void fromCsvCreatesValidObject() {
        Screening s = Screening.fromCsv("Дюна; 1; 150.0; 80; 165");
        assertEquals("Дюна", s.getFilm());
        assertEquals(1, s.getHall());
        assertEquals(150.0, s.getTicketPrice(), 0.0001);
        assertEquals(80, s.getSold());
        assertEquals(165, s.getDurationMin());
        assertEquals(12000.0, s.calculateRevenue(), 0.0001);
    }

    @Test
    void constructorRejectsEmptyFilm() {
        assertThrows(IllegalArgumentException.class,
                () -> new RegularScreening("   ", 1, 100.0, 10, 120));
    }

    @Test
    void constructorRejectsNegativeValues() {
        assertThrows(IllegalArgumentException.class,
                () -> new RegularScreening("Фільм", 0, 100.0, 10, 120));
        assertThrows(IllegalArgumentException.class,
                () -> new RegularScreening("Фільм", 1, -10.0, 10, 120));
        assertThrows(IllegalArgumentException.class,
                () -> new RegularScreening("Фільм", 1, 100.0, -1, 120));
    }

    @Test
    void fromCsvRejectsInvalidFieldCount() {
        assertThrows(IllegalArgumentException.class,
                () -> Screening.fromCsv("Дюна; 1; 150.0; 80"));
    }

    @Test
    void fromCsvRejectsInvalidNumberFormat() {
        assertThrows(IllegalArgumentException.class,
                () -> Screening.fromCsv("Дюна; abc; 150.0; 80; 165"));
    }

    @Test
    void recordStoresCorrectValues() {
        TicketSales sales = new TicketSales(5, 5000.0, 100.0, 50);
        assertEquals(5, sales.validRecords());
        assertEquals(5000.0, sales.totalRevenue(), 0.0001);
        assertEquals(100.0, sales.averageTicketPrice(), 0.0001);
        assertEquals(50, sales.maxSold());
    }

    @Test
    void recordRejectsNegativeValues() {
        assertThrows(IllegalArgumentException.class,
                () -> new TicketSales(-1, 5000.0, 100.0, 50));
    }
}