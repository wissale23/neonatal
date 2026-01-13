import org.junit.jupiter.api.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static org.mockito.Mockito.*;

public class ServletAccessTest {

    @Test
    void getAdmin_notLoggedIn_redirectsToLogin() throws Exception {
        Servlet servlet = new Servlet();
        servlet.init();

        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse resp = mock(HttpServletResponse.class);

        when(req.getServletPath()).thenReturn("/admin");
        when(req.getContextPath()).thenReturn("");
        when(req.getSession(false)).thenReturn(null);

        servlet.doGet(req, resp);

        verify(resp).sendRedirect("/login?error=login_required");
    }

    @Test
    void logout_invalidatesSession_andRedirectsToLogin() throws Exception {
        Servlet servlet = new Servlet();
        servlet.init();

        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse resp = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        when(req.getServletPath()).thenReturn("/logout");
        when(req.getContextPath()).thenReturn("");
        when(req.getSession(false)).thenReturn(session);

        servlet.doGet(req, resp);

        verify(session).invalidate();
        verify(resp).sendRedirect("/login");
    }
}
