import Person.Baby;
import Servlets.BabyPatientList;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class BabyPatientListTest {

    @Test
    void getAll_returnsTwoBabies() {
        List<Baby> babies = BabyPatientList.getAll();
        assertEquals(2, babies.size());
    }

    @Test
    void getBaby_returnsCorrectBabies() {
        Baby a = BabyPatientList.getBaby(1);
        assertEquals(1, a.getId());
        assertEquals("Person.Baby A", a.getName());

        Baby b = BabyPatientList.getBaby(2);
        assertEquals(2, b.getId());
        assertEquals("Person.Baby B", b.getName());
    }

    @Test
    void getBaby_unknownId_defaultsToFirstBaby() {
        Baby unknown = BabyPatientList.getBaby(999);
        assertEquals(1, unknown.getId()); // current behaviour
    }
}
