package ua.lpnu.kzp;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MainTest {

    @Test
    void testCorrectRecord() {
        String line = "Мавка;1;150.00;90;99";

        String[] fields = line.split(";", -1);

        assertEquals(5, fields.length);
        assertEquals("Мавка", fields[0]);
        assertEquals(1, Integer.parseInt(fields[1]));
        assertEquals(150.00, Double.parseDouble(fields[2]));
        assertEquals(90, Integer.parseInt(fields[3]));
        assertEquals(99, Integer.parseInt(fields[4]));
    }

    @Test
    void testInvalidNumber() {
        String line = "Король Лев;1;170.00;abc;118";

        String[] fields = line.split(";", -1);

        boolean invalid = false;

        try {
            Integer.parseInt(fields[3]);
        } catch (NumberFormatException e) {
            invalid = true;
        }

        assertEquals(true, invalid);
    }

    @Test
    void testNegativeTicketPrice() {
        String line = "Аватар;2;-200.00;100;192";

        String[] fields = line.split(";", -1);

        double ticketPrice = Double.parseDouble(fields[2]);

        assertEquals(true, ticketPrice < 0);
    }

    @Test
    void testWrongFieldCount() {
        String line = "Мавка;1;150.00;90";

        String[] fields = line.split(";", -1);

        assertEquals(4, fields.length);
    }

    @Test
    void testUkrainianText() {
        String line = "Мавка;1;150.00;90;99";

        String[] fields = line.split(";", -1);

        assertEquals("Мавка", fields[0]);
    }
}