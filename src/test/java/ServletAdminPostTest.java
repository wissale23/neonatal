import org.junit.jupiter.api.Test;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import static org.mockito.Mockito.*;

public class ServletAdminPostTest {

    @Test
    void postAdmin_notLoggedIn_redirectsToLoginRequired() throws Exception {
        Servlet servlet = new Servlet();
        servlet.init();

        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse resp = mock(HttpServletResponse.class);

        when(req.getServletPath()).thenReturn("/admin");
        when(req.getContextPath()).thenReturn("");
        when(req.getSession(false)).thenReturn(null);

        servlet.doPost(req, resp);

        verify(resp).sendRedirect("/login?error=login_required");
    }

    @Test
    void postAdmin_nonAdmin_redirectsToTheirHome() throws Exception {
        Servlet servlet = new Servlet();
        servlet.init();

        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse resp = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        when(req.getServletPath()).thenReturn("/admin");
        when(req.getContextPath()).thenReturn("");
        when(req.getSession(false)).thenReturn(session);
        when(session.getAttribute("role")).thenReturn("nurse");

        servlet.doPost(req, resp);

        verify(resp).sendRedirect("/nurses");
    }

    @Test
    void postAdmin_success_createsAccount_andRedirectsCreated() throws Exception {
        Servlet servlet = new Servlet();
        servlet.init();

        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse resp = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        when(req.getServletPath()).thenReturn("/admin");
        when(req.getContextPath()).thenReturn("");
        when(req.getSession(false)).thenReturn(session);
        when(session.getAttribute("role")).thenReturn("admin");

        when(req.getParameter("newUsername")).thenReturn("newNurse99");
        when(req.getParameter("newPassword")).thenReturn("pw123");
        when(req.getParameter("newRole")).thenReturn("nurse");

        servlet.doPost(req, resp);

        verify(resp).sendRedirect("/admin?status=created");
    }

    @Test
    void postAdmin_usernameTaken_redirectsWithUsernameTaken() throws Exception {
        Servlet servlet = new Servlet();
        servlet.init();

        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse resp = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        when(req.getServletPath()).thenReturn("/admin");
        when(req.getContextPath()).thenReturn("");
        when(req.getSession(false)).thenReturn(session);
        when(session.getAttribute("role")).thenReturn("admin");

        // nurse1 already exists
        when(req.getParameter("newUsername")).thenReturn("nurse1");
        when(req.getParameter("newPassword")).thenReturn("pw123");
        when(req.getParameter("newRole")).thenReturn("nurse");

        servlet.doPost(req, resp);

        verify(resp).sendRedirect("/admin?error=username_taken");
    }

    @Test
    void postAdmin_invalidRole_redirectsWithInvalidRole() throws Exception {
        Servlet servlet = new Servlet();
        servlet.init();

        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpServletResponse resp = mock(HttpServletResponse.class);
        HttpSession session = mock(HttpSession.class);

        when(req.getServletPath()).thenReturn("/admin");
        when(req.getContextPath()).thenReturn("");
        when(req.getSession(false)).thenReturn(session);
        when(session.getAttribute("role")).thenReturn("admin");

        when(req.getParameter("newUsername")).thenReturn("userX");
        when(req.getParameter("newPassword")).thenReturn("pw123");
        // role not allowed by AuthManager.createUser
        when(req.getParameter("newRole")).thenReturn("parent");

        servlet.doPost(req, resp);

        verify(resp).sendRedirect("/admin?error=invalid_role");
    }
}
