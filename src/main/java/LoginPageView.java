import java.util.List;

public class LoginPageView {

    public String render(String contextPath, String messageHtml) {
        String msg = (messageHtml == null) ? "" : messageHtml;

        return "<!DOCTYPE html>" +
                "<html><head><meta charset='utf-8'>" +
                "<meta name='viewport' content='width=device-width, initial-scale=1'>" +
                "<title>Login</title>" +
                "<style>" +
                "body{font-family:Arial,sans-serif;max-width:560px;margin:40px auto;padding:0 16px;}" +
                ".card{border:1px solid #ddd;border-radius:12px;padding:16px;}" +
                ".row{margin:10px 0;}label{display:block;margin-bottom:6px;}" +
                "input{width:100%;padding:10px;border:1px solid #ccc;border-radius:8px;}" +
                "button{margin-top:10px;padding:10px 14px;border:0;border-radius:8px;cursor:pointer;}" +
                ".err{color:#b00020;margin:10px 0;}" +
                ".ok{color:#0a7a2f;margin:10px 0;}" +
                "</style></head><body>" +
                "<h1>Login</h1>" +
                "<div class='card'>" + msg +
                "<form method='POST' action='" + contextPath + "/login'>" +
                "<div class='row'><label>Username</label><input name='username' required></div>" +
                "<div class='row'><label>Password</label><input name='password' type='password' required></div>" +
                "<button type='submit'>Sign in</button>" +
                "</form></div></body></html>";
    }

    public String renderAdminPage(String contextPath, String messageHtml, List<String> usernames, List<Baby> babies) {
        String msg = (messageHtml == null) ? "" : messageHtml;

        StringBuilder userOptions = new StringBuilder();
        if (usernames != null) {
            for (String u : usernames) {
                if (u == null) continue;
                String uu = u.trim();
                if (uu.isEmpty()) continue;
                userOptions.append("<option value='").append(escape(uu)).append("'>")
                        .append(escape(uu)).append("</option>");
            }
        }

        StringBuilder babyChecks = new StringBuilder();
        if (babies != null) {
            for (Baby b : babies) {
                babyChecks.append("<label style='display:flex;align-items:center;gap:8px;margin:6px 0;'>")
                        .append("<input type='checkbox' name='babyIds' value='").append(b.getId()).append("'>")
                        .append("Baby ").append(b.getId()).append(" — ").append(escape(b.getName()))
                        .append("</label>");
            }
        }

        StringBuilder babyOptions = new StringBuilder();
        if (babies != null) {
            for (Baby b : babies) {
                babyOptions.append("<option value='").append(b.getId()).append("'>")
                        .append("Baby ").append(b.getId()).append(" — ").append(escape(b.getName()))
                        .append("</option>");
            }
        }

        return "<!DOCTYPE html>" +
                "<html><head><meta charset='utf-8'>" +
                "<meta name='viewport' content='width=device-width, initial-scale=1'>" +
                "<title>Admin</title>" +
                "<style>" +
                "body{font-family:Arial,sans-serif;max-width:820px;margin:40px auto;padding:0 16px;}" +
                ".card{border:1px solid #ddd;border-radius:12px;padding:16px;margin-top:12px;}" +
                ".row{margin:10px 0;}label{display:block;margin-bottom:6px;}" +
                "input,select{width:100%;padding:10px;border:1px solid #ccc;border-radius:8px;}" +
                "button{margin-top:10px;padding:10px 14px;border:0;border-radius:8px;cursor:pointer;}" +
                ".err{color:#b00020;margin:10px 0;}" +
                ".ok{color:#0a7a2f;margin:10px 0;}" +
                "a{display:inline-block;margin-top:12px;}" +
                "</style></head><body>" +
                "<h1>Admin Panel</h1>" +
                "<div class='card'>" +
                "<h2>Create an account</h2>" +
                msg +
                "<form method='POST' action='" + contextPath + "/admin'>" +
                "<input type='hidden' name='action' value='create'>" +
                "<div class='row'><label>Username</label><input name='newUsername' required></div>" +
                "<div class='row'><label>Password</label><input name='newPassword' type='password' required></div>" +
                "<div class='row'><label>Role</label>" +
                "<select name='newRole' required>" +
                "<option value='nurse'>Nurse</option>" +
                "<option value='consultant'>Consultant</option>" +
                "<option value='researcher'>Researcher</option>" +
                "<option value='admin'>Admin</option>" +
                "</select></div>" +
                "<div class='row'><label>Assign babies</label>" +
                babyChecks +
                "</div>" +
                "<button type='submit'>Create account</button>" +
                "</form>" +
                "</div>" +
                "<div class='card'>" +
                "<h2>Assign baby to existing user</h2>" +
                "<form method='POST' action='" + contextPath + "/admin'>" +
                "<input type='hidden' name='action' value='assign'>" +
                "<div class='row'><label>User</label><select name='targetUser' required>" + userOptions + "</select></div>" +
                "<div class='row'><label>Baby</label><select name='babyId' required>" + babyOptions + "</select></div>" +
                "<button type='submit'>Assign</button>" +
                "</form>" +
                "</div>" +
                "<div class='card'>" +
                "<h2>Remove baby from user</h2>" +
                "<form method='POST' action='" + contextPath + "/admin'>" +
                "<input type='hidden' name='action' value='unassign'>" +
                "<div class='row'><label>User</label><select name='targetUser' required>" + userOptions + "</select></div>" +
                "<div class='row'><label>Baby</label><select name='babyId' required>" + babyOptions + "</select></div>" +
                "<button type='submit'>Remove</button>" +
                "</form>" +
                "</div>" +
                "<div class='card'>" +
                "<h2>Delete user</h2>" +
                "<form method='POST' action='" + contextPath + "/admin'>" +
                "<input type='hidden' name='action' value='delete'>" +
                "<div class='row'><label>User</label><select name='targetUser' required>" + userOptions + "</select></div>" +
                "<button type='submit'>Delete</button>" +
                "</form>" +
                "</div>" +
                "<p><a href='" + contextPath + "/logout'>Logout</a></p>" +
                "</body></html>";
    }

    public static String errorBox(String text) {
        if (text == null || text.trim().isEmpty()) return "";
        return "<div class='err'>" + escape(text) + "</div>";
    }

    public static String okBox(String text) {
        if (text == null || text.trim().isEmpty()) return "";
        return "<div class='ok'>" + escape(text) + "</div>";
    }

    private static String escape(String s) {
        return s.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;")
                .replace("\"","&quot;").replace("'","&#39;");
    }
}
