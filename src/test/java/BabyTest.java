import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BabyTest {

    private Baby freshBaby() {
        // Use real resource files that exist in src/main/resources
        return new Baby("Test Baby", 99,
                "/t_glu1.txt", "/glu_uM_unsmoothed1.txt", "/glu_uM_smoothed1.txt");
    }

    @Test
    void addAndRemoveSample_updatesLists() {
        Baby baby = freshBaby();

        baby.addSample(12.5, 4.2);
        assertEquals(1, baby.getSampleTimes().size());
        assertEquals(1, baby.getSampleValues().size());
        assertEquals(12.5, baby.getSampleTimes().get(0));
        assertEquals(4.2, baby.getSampleValues().get(0));

        baby.addSample(13.0, 5.0);
        assertEquals(2, baby.getSampleTimes().size());

        baby.removeLastSample();
        assertEquals(1, baby.getSampleTimes().size());
        assertEquals(12.5, baby.getSampleTimes().get(0));
    }

    @Test
    void addAndRemoveFeeding_updatesLists() {
        Baby baby = freshBaby();

        baby.addFeeding(8.25, 0.5, "Milk");
        assertEquals(1, baby.getFeedStarts().size());
        assertEquals(1, baby.getFeedDurations().size());
        assertEquals(1, baby.getFeedTypes().size());

        baby.removeLastFeeding();
        assertEquals(0, baby.getFeedStarts().size());
        assertEquals(0, baby.getFeedDurations().size());
        assertEquals(0, baby.getFeedTypes().size());
    }

    @Test
    void setRanges_updatesLowerAndUpper() {
        Baby baby = freshBaby();
        baby.setRanges(3.0, 9.0);
        assertEquals(3.0, baby.getLowerRange());
        assertEquals(9.0, baby.getUpperRange());
    }

    @Test
    void addComment_addsTextAndUsername_andIgnoresEmpty() {
        Baby baby = freshBaby();

        int before = baby.getComments().size();
        baby.addComment("nurse1", "");
        assertEquals(before, baby.getComments().size());

        baby.addComment("nurse1", "Patient stable");
        assertEquals(before + 1, baby.getComments().size());

        String last = baby.getComments().get(baby.getComments().size() - 1);
        assertTrue(last.contains("nurse1"));
        assertTrue(last.contains("Patient stable"));
    }
}
