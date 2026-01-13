import javax.servlet.http.HttpServletRequest;

public class UsefulLinksPage {
    public static String generatePage(HttpServletRequest req) {
        String contextPath = req.getContextPath();
        return "<!DOCTYPE html><html><head>"
                + "<meta name='viewport' content='width=device-width, initial-scale=1'>"
                + "<link rel=\"stylesheet\" href=\"https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css\">"
                + "<style>"
                + "body { font-family: 'Lato', sans-serif; }"

                // Sidebar CSS
                + ".sidebar { height:100%; width:0; position:fixed; z-index:1; top:0; left:0; background-color:#111; overflow-x:hidden; transition:0.5s; padding-top:60px; }"
                + ".sidebar a { padding:8px 8px 8px 32px; text-decoration:none; font-size:25px; color:#818181; display:block; transition:0.3s; }"
                + ".sidebar a:hover { color:#f1f1f1; }"
                + ".sidebar .closebtn { position:absolute; top:0; right:25px; font-size:36px; margin-left:50px; }"
                + ".openbtn { font-size:20px; cursor:pointer; background-color:#111; color:white; padding:10px 15px; border:none; }"
                + ".openbtn:hover { background-color:#444; }"
                + "#main { transition: margin-left .5s; padding:16px; }"
                + "@media screen and (max-height:450px){.sidebar{padding-top:15px;}.sidebar a{font-size:18px;}}"

                // Header CSS
                + ".header { background-color: #003087; color: white; text-align: center; padding: 20px; font-size: 28px; font-weight: bold; }"

                // Links CSS
                + ".links {color: black;text-align: left;}"
                + ".links .map-link {"
                + "margin-top: 12px;}"

                + "</style>"

                + "</head><body>"
                + "<div class='header'>Useful Links</div>" // Header HTML
                + Homepage.generateSidebar()           // sidebar HTML
                + generateLinks()

                + "</div>"
                + "<script>"
                + "function openSidebar(){ document.getElementById('mySidebar').style.width='250px';"
                + "document.getElementById('main').style.marginLeft='250px'; }"
                + "function closeSidebar(){ document.getElementById('mySidebar').style.width='0';"
                + "document.getElementById('main').style.marginLeft='0'; }"
                + "</script>"
                + "</body></html>";
    }

    public static String generateLinks(){
        return "<div class='links'>"
                + "<p class='map-link'>"
                + "<a href=\"https://www.google.com/maps/place/Queen+Charlotte's+and+Chelsea+Hospital/@51.5163664,-0.2374661,17z/data=!3m1!4b1!4m6!3m5!1s0x487611d51e61f219:0x868f7e814c833d2a!8m2!3d51.5163664!4d-0.2374661!16s%2Fm%2F027hryh?entry=ttu&g_ep=EgoyMDI2MDEwNy4wIKXMDSoASAFQAw%3D%3D\""
                + "target='_blank'>"
                + "<i class='fa fa-external-link'></i> Location Guide</a></p>"
                + "<p class='map-link'>"
                + "<a href=\"https://www.england.nhs.uk/nhsidentity/identity-guidelines/colours/\""
                + "target='_blank'>"
                + "<i class='fa fa-external-link'></i> Parent Guide</a></p>"
                + "</div>";
    }
}
