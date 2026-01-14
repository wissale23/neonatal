import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class Db {
    private Db() {}

    public static boolean isConfigured() {
        return env("PGHOST") != null
                && env("PGPORT") != null
                && env("PGDATABASE") != null
                && env("PGUSER") != null
                && env("PGPASSWORD") != null;
    }

    public static Connection connect() throws SQLException {
        String url = "jdbc:postgresql://" + env("PGHOST") + ":" + env("PGPORT") + "/" + env("PGDATABASE");
        return DriverManager.getConnection(url, env("PGUSER"), env("PGPASSWORD"));
    }

    private static String env(String key) {
        String v = System.getenv(key);
        if (v == null) return null;
        v = v.trim();
        return v.isEmpty() ? null : v;
    }
}
