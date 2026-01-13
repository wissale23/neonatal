public class LogoutOption {

    public static String generateLogoutSidebar(){

        return "<div id='mySidebar' class='sidebar'>"
                + "  <a href='javascript:void(0)' class='closebtn' onclick='closeSidebar()'>&times;</a>"
                + "  <a href='/'><i class=\"fa fa-fw fa-home\"></i>Home</a>"
                + "  <a href='/login'><i class='fa fa-fw fa-lock'></i>Logout</a>"
                + "</div>"
                + "<div id='main'>"
                + "  <button class='openbtn' onclick='openSidebar()'>&#9776; Options</button>";
    }

}
