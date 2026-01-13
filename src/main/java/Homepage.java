import javax.servlet.http.HttpServletRequest;

public class Homepage {
    // Returns the full HTML of the homepage

    public static String generatePage(HttpServletRequest req) {
        String contextPath = req.getContextPath();
        return "<!DOCTYPE html><html><head>"
                + "<meta name='viewport' content='width=device-width, initial-scale=1'>"
                + "<link rel=\"stylesheet\" href=\"https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.7.0/css/font-awesome.min.css\">"
                + "<style>"
                + "body { font-family: 'Lato', sans-serif; }"
                + ".sidebar { height:100%; width:0; position:fixed; z-index:1; top:0; left:0; background-color:#111; overflow-x:hidden; transition:0.5s; padding-top:60px; }"
                + ".sidebar a { padding:8px 8px 8px 32px; text-decoration:none; font-size:25px; color:#818181; display:block; transition:0.3s; }"
                + ".sidebar a:hover { color:#f1f1f1; }"
                + ".sidebar .closebtn { position:absolute; top:0; right:25px; font-size:36px; margin-left:50px; }"
                + ".openbtn { font-size:20px; cursor:pointer; background-color:#111; color:white; padding:10px 15px; border:none; }"
                + ".openbtn:hover { background-color:#444; }"
                + "#main { transition: margin-left .5s; padding:16px; }"
                + "@media screen and (max-height:450px){.sidebar{padding-top:15px;}.sidebar a{font-size:18px;}}"
                + "</style>"
                + sectionsCSS           // contacts CSS
                + "</head><body>"
                + generateSidebar()           // sidebar HTML
                + generateSlideshow(contextPath)         // slideshow content
                + generateAboutUs()         // about us HTML
                + "<div class='info-row'>"
                + generateContacts()            // contacts HTML
                + generateLocation()            // location HTML
                + "</div>"
                + "<script>"
                + "function openSidebar(){ document.getElementById('mySidebar').style.width='250px';"
                + "document.getElementById('main').style.marginLeft='250px'; }"
                + "function closeSidebar(){ document.getElementById('mySidebar').style.width='0';"
                + "document.getElementById('main').style.marginLeft='0'; }"
                + "</script>"
                + "</body></html>";
    }

    public static String generateSidebar() {
        // Reference 2 - Collapsable Sidebar taken from https://www.w3schools.com/howto/howto_js_collapse_sidebar.asp
        return "<div id='mySidebar' class='sidebar'>"
                + "  <a href='javascript:void(0)' class='closebtn' onclick='closeSidebar()'>&times;</a>"
                + "  <a href='/home'><i class=\"fa fa-fw fa-home\"></i>Home</a>"
                + "  <a href='/login'><i class=\"fa fa-fw fa-user\"></i>Login</a>"
                + "</div>"
                + "<div id='main'>"
                + "  <button class='openbtn' onclick='openSidebar()'>&#9776; Options</button>";
    }

    public static String generateSlideshow(String contextPath) {
    // Reference 3 - Slideshow taken from https://www.geeksforgeeks.org/web-templates/programming-a-slideshow-with-html-and-css/
        return ""
                + "<style>"
                + "#slideshow {"
                + "  overflow:hidden;"
                + "  height:420px;"
                + "  width:100%;"
                + "  max-width:900px;"
                + "  margin:40px auto;"
                + "}"

                + ".slide-wrapper {"
                + "  width:400%;"
                + "  animation: slide 16s ease infinite;"
                + "}"

                + ".slide {"
                + "  float:left;"
                + "  width:25%;"
                + "  height:420px;"
                + "}"

                + ".slide img {"
                + "  width:100%;"
                + "  height:100%;"
                + "  object-fit:cover;"
                + "}"

                + "@keyframes slide {"
                + "  0% {margin-left:0%;}"
                + "  20% {margin-left:0%;}"
                + "  25% {margin-left:-100%;}"
                + "  45% {margin-left:-100%;}"
                + "  50% {margin-left:-200%;}"
                + "  70% {margin-left:-200%;}"
                + "  75% {margin-left:-300%;}"
                + "  95% {margin-left:-300%;}"
                + "  100% {margin-left:0%;}"
                + "}"
                + "</style>"

                + "<div id='slideshow'>"
                + "  <div class='slide-wrapper'>"

                + "    <div class='slide'>"
                + "      <img src='" + contextPath + "/images/1.jpg' alt='Slide 1'>"
                + "    </div>"

                + "    <div class='slide'>"
                + "      <img src='" + contextPath + "/images/2.jpg' alt='Slide 2'>"
                + "    </div>"

                + "    <div class='slide'>"
                + "      <img src='" + contextPath + "/images/3.jpg' alt='Slide 3'>"
                + "    </div>"

                + "    <div class='slide'>"
                + "      <img src='" + contextPath + "/images/4.jpg' alt='Slide 4'>"
                + "    </div>"

                + "  </div>"
                + "</div>";
    }

    public static final String sectionsCSS =
            "<style>" +

                    // About Us section CSS
                    ".about-us {"
                    + "max-width: 1000px;"
                    + "margin: 60px auto;"
                    + "padding: 40px 40px;"
                    + "border-radius: 3px;"
                    + "background-color: #385f73;"
                    + "color: white;"
                    + "text-align: left;"
                    + "font-family: Lato, sans-serif;"
                    + "}"

                    + ".about-us h2 {"
                    + "color: white;"
                    + "font-size: 36px;"
                    + "margin-bottom: 15px;"
                    + "text-align: center;}"

                    + ".about-us h2::after {"
                    + "content: '';"
                    + "display: block;"
                    + "width: 80%;"
                    + "max-width: 900px;"
                    + "height: 3px;"
                    + "background-color: white;"
                    + "margin: 15px auto 30px auto;}"

                    + ".about-us p {"
                    + "font-size: 16px;"
                    + "line-height: 1.5;}"

                    // Contacts and Location boxes CSS
                    + ".info-row {"
                    + "display: flex;"
                    + "gap: 100px;"
                    + "max-width: 1000px;"
                    + "margin: 40px auto;}"

                    + ".info-box {"
                    + "flex: 1;"
                    + "padding: 30px 30px;"
                    + "border-radius: 5px;"
                    + "background-color: #7eabc2;"
                    + "font-family: Lato, sans-serif;"
                    + "text-align: left;}"

                    + ".info-box h3 {"
                    + "font-size: 22px;"
                    + "color: #2c4f61;"
                    + "margin-top: 0;"
                    + "margin-bottom: 15px;"
                    + "text-align: center;}"

                    + ".info-box h3::after {"
                    + "content: '';"
                    + "display: block;"
                    + "width: 90%;"
                    + "height: 3px;"
                    + "background-color: #2c4f61;"
                    + "margin: 10px auto 15px auto;}"

                    + ".info-box p {"
                    + "margin: 10px 30px;"
                    + "font-size: 16px;"
                    + "color: #2c4f61;"
                    + "text-align: left;}"

                    + ".info-box a, .info-box a:visited {"
                    + "font-size: 16px;"
                    + "color: #2c4f61;"
                    + "text-decoration: none;"
                    + "-webkit-text-fill-color: #2c4f61;}"

                    + ".info-box a:hover {"
                    + "color: #031426;"
                    + "text-decoration: underline;}"

                    + ".contact-label {"
                    + "display: inline-block;"
                    + "width: 300px;"
                    + "font-weight: bold;}"

                    + "</style>";

    public static String generateAboutUs() {
        return "<section class='about-us'>"
                + "<h2>About Us</h2>"
                + "<p>"
                + "Queen Charlotte's & Chelsea Hospital (QCCH) is a (level 3) neonatal intensive care unit (NICU), " +
                "which provides neonatal care for infants born <27 weeks of gestation and/or of birthweight <800 grams " +
                "as well as multiple pregnancies <28 weeks of gestation, as well as IC for more mature infants. " +
                "The service treat new born babies with complex medical needs, including cardiac and neurological problems, " +
                "as well as newborn babies with hypoxic ischaemic encephalopathy and retinopathy of prematurity. " +
                "The service maintain close links with fetal medicine unit, clinical genetics and specialist paediatric services " +
                "at Imperial College Healthcare NHS Trust (ICHT) (ID, paed surgery, neurology)."
                + "</p>"
                + "<p>The unit has two main areas and each has a number of small rooms:</p>"
                + "<ul class='area-list'>"
                + "<li>Intensive Care(IC)</li>"
                + "<li>High Dependency Care (HD) and Special Care (SC)</li>"
                + "</section>";
    }

    public static String generateContacts() {
        return "<div class='info-box contacts-box'>"
                + "<h3><i class='fa fa-phone'></i> Contact Information</h3>"
                + "<p><span class='contact-label'>Reception</span>"
                + "<a href='tel: 02033135158'>02033135158</a></p>"
                + "<p><span class='contact-label'>Intensive Care</span>"
                + "<a href='tel: 02033133174'>02033133174</a></p>"
                + "<p><span class='contact-label'>High Dependency and Special Care</span>"
                + "<a href='tel: 02033133908'>02033133908</a></p>"
                + "</div>";
    }

    public static String generateLocation() {
        return "<div class='info-box location-box'>"
                + "<h3><i class='fa fa-map-marker'></i> Find us at</h3>"
                + "<p>4th Floor</p>"
                + "<p>Queen Charlotte's & Chelsea Hospital</p>"
                + "<p>Du Cane Road,</p>"
                + "<p>Hammersmith W12 0HS</p>"
                + "</div>";
    }


}
