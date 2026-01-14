import Person.Baby;
import Warning.WarningSystem;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class WarningSystemTest {

    @Test
    void detectsBelowAboveAndSafe() {
        Baby baby = mock(Baby.class);
        when(baby.getLowerRange()).thenReturn(1.0);
        when(baby.getUpperRange()).thenReturn(2.0);

        WarningSystem ws = new WarningSystem(baby);

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
        Baby baby = mock(Baby.class);
        when(baby.getLowerRange()).thenReturn(1.0);
        when(baby.getUpperRange()).thenReturn(2.0);

        WarningSystem ws = new WarningSystem(baby);

        assertTrue(ws.getWarningMessage(0.5).toLowerCase().contains("below"));
        assertTrue(ws.getWarningMessage(2.5).toLowerCase().contains("above"));
        assertTrue(ws.getWarningMessage(1.5).toLowerCase().contains("within"));
    }
}
