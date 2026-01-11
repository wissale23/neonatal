import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.mockito.Mockito;
import static org.mockito.Mockito.*;

public class AuthManagerTest {

    @Test
    void authenticate_missingCredentials() {
        AuthManager auth = new AuthManager();
        auth.addUser("nurse1", "nursepass", "nurse");

        AuthManager.AuthResult r1 = auth.authenticate("", "x");
        assertFalse(r1.isOk());
        assertEquals(AuthManager.Error.MISSING_CREDENTIALS, r1.getError());

        AuthManager.AuthResult r2 = auth.authenticate("nurse1", "");
        assertFalse(r2.isOk());
        assertEquals(AuthManager.Error.MISSING_CREDENTIALS, r2.getError());
    }

    @Test
    void authenticate_userNotFound() {
        AuthManager auth = new AuthManager();
        AuthManager.AuthResult r = auth.authenticate("ghost", "pw");
        assertFalse(r.isOk());
        assertEquals(AuthManager.Error.USER_NOT_FOUND, r.getError());
    }

    @Test
    void authenticate_wrongPassword() {
        AuthManager auth = new AuthManager();
        auth.addUser("nurse1", "nursepass", "nurse");

        AuthManager.AuthResult r = auth.authenticate("nurse1", "wrong");
        assertFalse(r.isOk());
        assertEquals(AuthManager.Error.WRONG_PASSWORD, r.getError());
    }

    @Test
    void authenticate_successReturnsRole() {
        AuthManager auth = new AuthManager();
        auth.addUser("nurse1", "nursepass", "NURSE");

        AuthManager.AuthResult r = auth.authenticate("nurse1", "nursepass");
        assertTrue(r.isOk());
        assertEquals("nurse", r.getRole()); // normalised
    }

    @Test
    void createUser_usernameTaken() {
        AuthManager auth = new AuthManager();
        auth.addUser("nurse1", "nursepass", "nurse");

        AuthManager.CreateResult cr = auth.createUser("nurse1", "x", "nurse");
        assertFalse(cr.isOk());
        assertEquals(AuthManager.CreateError.USERNAME_TAKEN, cr.getError());
    }

    @Test
    void createUser_invalidRole() {
        AuthManager auth = new AuthManager();
        AuthManager.CreateResult cr = auth.createUser("newuser", "pw", "parent");
        assertFalse(cr.isOk());
        assertEquals(AuthManager.CreateError.INVALID_ROLE, cr.getError());
    }

    @Test
    void redirectIfNotAllowed_blocksAdminPageForNurse() {
        AuthManager auth = new AuthManager();

        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);

        when(req.getSession(false)).thenReturn(session);
        when(session.getAttribute("role")).thenReturn("nurse");

        String redirect = auth.redirectIfNotAllowed("/admin", req);
        assertEquals("/nurses", redirect);
    }
}
