package Servlet;

import Person.Baby;

import java.util.*;

public class BabyPatientList {

    private static final List<Baby> babies = new ArrayList<>();

    // Add babies on the patient list to the List babies with their raw data paths
    static {
        babies.add(new Baby("Person.Baby A", 1,
                "/t_glu1.txt","/glu_uM_unsmoothed1.txt","/glu_uM_smoothed1.txt"));

        babies.add(new Baby("Person.Baby B", 2,
                "/t_glu2.txt","/glu_uM_unsmoothed2.txt","/glu_uM_smoothed2.txt"));
    }

    public static Baby getBaby(int id) {
        for (Baby b : babies) if (b.getId() == id) return b;
        return babies.get(0);
    }

    public static List<Baby> getAll() {
        return babies;
    }
}