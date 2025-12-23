import java.util.ArrayList;

public abstract class Adult extends Person implements Pageable{
    private ArrayList<Baby> patients = new ArrayList<>();
    private final String endpoint;

    public Adult(String name, int id, String endpoint) {
        super(name, id);
        this.endpoint = endpoint;

    }

    public ArrayList<Baby> getPatients() {
        return patients;
    }

    public void addPatient(Baby baby){
        patients.add(baby);
    }

    public String getEndpoint() {
        return endpoint;
    }
}
