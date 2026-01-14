package Person;


import Servlets.Pageable;

public abstract class Adult extends Person implements Pageable {
    private final String endpoint;

    public Adult(String name, int id, String endpoint) {
        super(name, id);
        this.endpoint = endpoint;

    }

    public String getEndpoint() {
        return endpoint;
    }
}
