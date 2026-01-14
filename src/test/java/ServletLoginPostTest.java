import org.junit.jupiter.api.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static org.mockito.Mockito.*;

public class ServletLoginPostTest {

    @Test
    void postLogin_success_startsSession_andRedirectsToRoleHome() throws Exception {
        Servlet servlet = new Servlet();
        servlet.init();

        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse resp = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        when(req.getServletPath()).thenReturn("/login");
        when(req.getContextPath()).thenReturn("");
        when(req.getParameter("username")).thenReturn("nurse1");
        when(req.getParameter("password")).thenReturn("nursepass");

        when(req.getSession(true)).thenReturn(session);

        servlet.doPost(req, resp);

        verify(session).setAttribute("username", "nurse1");
        verify(session).setAttribute("role", "nurse");
        verify(resp).sendRedirect("/nurses");
    }

    @Test
    void postLogin_wrongPassword_redirectsWithError_andDoesNotCreateSession() throws Exception {
        Servlet servlet = new Servlet();
        servlet.init();

        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse resp = mock(HttpServletResponse.class);

        when(req.getServletPath()).thenReturn("/login");
        when(req.getContextPath()).thenReturn("");
        when(req.getParameter("username")).thenReturn("nurse1");
        when(req.getParameter("password")).thenReturn("WRONG");

        servlet.doPost(req, resp);

        verify(resp).sendRedirect("/login?error=wrong_password");
        verify(req, never()).getSession(true);
    }
}
