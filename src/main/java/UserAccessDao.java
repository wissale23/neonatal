import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class UserAccessDao {

    public static final class UserRow {
        public final String username;
        public final String password;
        public final String role;

        public UserRow(String username, String password, String role) {
            this.username = username;
            this.password = password;
            this.role = role;
        }
    }

    public void ensureSchema() throws SQLException {
        try (Connection c = Db.connect(); Statement st = c.createStatement()) {
            st.execute(
                    "CREATE TABLE IF NOT EXISTS app_users (" +
                            "username TEXT PRIMARY KEY," +
                            "password TEXT NOT NULL," +
                            "role TEXT NOT NULL CHECK (role IN ('admin','nurse','consultant','researcher'))" +
                            ")"
            );

            st.execute(
                    "CREATE TABLE IF NOT EXISTS user_babies (" +
                            "username TEXT NOT NULL REFERENCES app_users(username) ON DELETE CASCADE," +
                            "baby_id INT NOT NULL," +
                            "PRIMARY KEY (username, baby_id)" +
                            ")"
            );
        }
    }

    public void ensureBootstrapAdmin(String username, String password) throws SQLException {
        try (Connection c = Db.connect();
             PreparedStatement ps = c.prepareStatement(
                     "INSERT INTO app_users(username, password, role) VALUES (?,?, 'admin') " +
                             "ON CONFLICT (username) DO NOTHING"
             )) {
            ps.setString(1, username);
            ps.setString(2, password);
            ps.executeUpdate();
        }
    }

    public void ensureDemoUsers() throws SQLException {
        try (Connection c = Db.connect();
             PreparedStatement ps = c.prepareStatement(
                     "INSERT INTO app_users(username, password, role) VALUES (?,?,?) ON CONFLICT (username) DO NOTHING"
             )) {

            ps.setString(1, "nurse1");
            ps.setString(2, "nursepass");
            ps.setString(3, "nurse");
            ps.executeUpdate();

            ps.setString(1, "consult1");
            ps.setString(2, "consultpass");
            ps.setString(3, "consultant");
            ps.executeUpdate();

            ps.setString(1, "research1");
            ps.setString(2, "researchpass");
            ps.setString(3, "researcher");
            ps.executeUpdate();
        }
    }

    public UserRow findUser(String username) throws SQLException {
        try (Connection c = Db.connect();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT username, password, role FROM app_users WHERE username=?"
             )) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) return null;
                return new UserRow(rs.getString(1), rs.getString(2), rs.getString(3));
            }
        }
    }

    public boolean insertUser(String username, String password, String role) throws SQLException {
        try (Connection c = Db.connect();
             PreparedStatement ps = c.prepareStatement(
                     "INSERT INTO app_users(username, password, role) VALUES (?,?,?)"
             )) {
            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, role);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            if ("23505".equals(e.getSQLState())) return false;
            throw e;
        }
    }

    public boolean deleteUser(String username) throws SQLException {
        try (Connection c = Db.connect();
             PreparedStatement ps = c.prepareStatement(
                     "DELETE FROM app_users WHERE username=?"
             )) {
            ps.setString(1, username);
            return ps.executeUpdate() == 1;
        }
    }

    public boolean assignBaby(String username, int babyId) throws SQLException {
        try (Connection c = Db.connect();
             PreparedStatement ps = c.prepareStatement(
                     "INSERT INTO user_babies(username, baby_id) VALUES (?,?)"
             )) {
            ps.setString(1, username);
            ps.setInt(2, babyId);
            return ps.executeUpdate() == 1;
        } catch (SQLException e) {
            if ("23505".equals(e.getSQLState())) return false;
            throw e;
        }
    }

    public boolean unassignBaby(String username, int babyId) throws SQLException {
        try (Connection c = Db.connect();
             PreparedStatement ps = c.prepareStatement(
                     "DELETE FROM user_babies WHERE username=? AND baby_id=?"
             )) {
            ps.setString(1, username);
            ps.setInt(2, babyId);
            return ps.executeUpdate() == 1;
        }
    }

    public List<Integer> getAssignedBabies(String username) throws SQLException {
        try (Connection c = Db.connect();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT baby_id FROM user_babies WHERE username=? ORDER BY baby_id"
             )) {
            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                List<Integer> ids = new ArrayList<>();
                while (rs.next()) ids.add(rs.getInt(1));
                return ids;
            }
        }
    }

    public List<String> listUsers() throws SQLException {
        try (Connection c = Db.connect();
             PreparedStatement ps = c.prepareStatement(
                     "SELECT username FROM app_users ORDER BY username"
             );
             ResultSet rs = ps.executeQuery()) {
            List<String> out = new ArrayList<>();
            while (rs.next()) out.add(rs.getString(1));
            return out;
        }
    }
}

