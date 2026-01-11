import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class WarningSystemTest {

    @Test
    void detectsBelowAboveAndSafe() {
        WarningSystem ws = new WarningSystem(1.0, 2.0);

        assertTrue(ws.isBelowRange(0.5));
        assertFalse(ws.isBelowRange(1.5));

        assertTrue(ws.isAboveRange(2.5));
        assertFalse(ws.isAboveRange(1.5));

        assertTrue(ws.isUnsafe(0.5));
        assertTrue(ws.isUnsafe(2.5));
        assertFalse(ws.isUnsafe(1.5));
    }

    @Test
    void warningMessageMatchesState() {
        WarningSystem ws = new WarningSystem(1.0, 2.0);

        assertTrue(ws.getWarningMessage(0.5).toLowerCase().contains("below"));
        assertTrue(ws.getWarningMessage(2.5).toLowerCase().contains("above"));
        assertTrue(ws.getWarningMessage(1.5).toLowerCase().contains("within"));
    }
}
