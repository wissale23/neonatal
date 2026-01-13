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

                + "</style>"
                + sectionsCSS  // sections CSS

                + "</head><body>"
                + "<div class='header'>Queen Charlotte's and Chelsea Hospital's Neonatal Unit</div>" // Header HTML
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
                + "<script>"
                + "function toggleAbout() {"
                + "var content = document.getElementById('about-content');"
                + "var icon = document.getElementById('about-icon');"
                + "content.classList.toggle('collapsed');"
                + "if (content.classList.contains('collapsed')) {icon.className='fa fa-plus';}"
                + "else {icon.className='fa fa-minus';}"
                + "}"
                + "</script>"
                + "</body></html>";
    }

    public static String generateSidebar() {
        // Reference 2 - Collapsable Sidebar taken from https://www.w3schools.com/howto/howto_js_collapse_sidebar.asp
        return "<div id='mySidebar' class='sidebar'>"
                + "  <a href='javascript:void(0)' class='closebtn' onclick='closeSidebar()'>&times;</a>"
                + "  <a href='/'><i class=\"fa fa-fw fa-home\"></i>Home</a>"
                + "  <a href='/login'><i class=\"fa fa-fw fa-user\"></i>Login</a>"
                + "  <a href='/useful-links'><i class=\"fa fa-fw fa-link\"></i>Useful Links</a>"
                + "</div>"
                + "<div id='main'>"
                + "  <button class='openbtn' onclick='openSidebar()'>&#9776; Options</button>";
    }

    // Autoplay Slideshow with Manual Arrows and Gallery
    public static String generateSlideshow(String contextPath) {
    // Reference 3 - Slideshow taken from https://www.w3schools.com/howto/howto_js_slideshow.asp
        // and https://stackoverflow.com/questions/53178768/automatic-slideshow-with-arrows-and-buttons
        return ""
                + "<style>"
                + "*{box-sizing:border-box;}"
                + ".slideshow-container{max-width:800px;position:relative;margin:40px auto;}"
                + ".mySlides{display:none;}"
                + ".mySlides img{width:100%;height:200px;object-fit:contain;}"

                // Next and Previous Arrows
                + ".prev,.next{cursor:pointer;position:absolute;top:50%;padding:16px;"
                + "color:#494B55;font-weight:bold;font-size:18px;"
                + "transition:0.6s;user-select:none;transform:translateY(-50%);}"

                + ".next{right:0;border-radius:3px 0 0 3px;}"
                + ".prev:hover,.next:hover{background-color:rgba(0,0,0,0.8);color:white;}"

                // Number text
                + ".numbertext{color:#f2f2f2;font-size:12px;padding:8px 12px;position:absolute;top:0;left:8px;}"

                // Dots for Carousel
                + ".dot{cursor:pointer;height:13px;width:13px;margin:0 2px;"
                + "background-color:#bbb;border-radius:50%;display:inline-block;"
                + "transition:background-color 0.6s ease;}"

                + ".active,.dot:hover{background-color:#494B55;}"

                // Fading animation
                + ".fade{animation-name:fade;animation-duration:2s;}"
                + "@keyframes fade{from{opacity:.4}to{opacity:1}}"
                + "</style>"

                // HTML class
                + "<div class='slideshow-container'>"

                // Images with number
                + "  <div class='mySlides fade'>"
                + "    <div class='numbertext'>1/4</div>"
                + "    <img src='" + contextPath + "/images/image1.png'>"
                + "  </div>"

                + "  <div class='mySlides fade'>"
                + "    <div class='numbertext'>2/4</div>"
                + "    <img src='" + contextPath + "/images/2.jpg'>"
                + "  </div>"

                + "  <div class='mySlides fade'>"
                + "    <div class='numbertext'>3/4</div>"
                + "    <img src='" + contextPath + "/images/3.jpg'>"
                + "  </div>"

                + "  <div class='mySlides fade'>"
                + "    <div class='numbertext'>4/4</div>"
                + "    <img src='" + contextPath + "/images/4.jpg'>"
                + "  </div>"

                // Next and previous buttons
                + "  <a class='prev' onclick='plusSlides(-1)'>&#10094;</a>"
                + "  <a class='next' onclick='plusSlides(1)'>&#10095;</a>"

                + "</div>"

                // The centre dots
                + "<div style='text-align:center'>"
                + "  <span class='dot' onclick='currentSlide(1)'></span>"
                + "  <span class='dot' onclick='currentSlide(2)'></span>"
                + "  <span class='dot' onclick='currentSlide(3)'></span>"
                + "  <span class='dot' onclick='currentSlide(4)'></span>"
                + "</div>"

                + "<script>"

                // Add timer variable
                + "var slideIndex=0;"
                + "var slides,dots,timer;"
                + "showSlides();"

                // Autoplay the slides
                + "function showSlides(){"
                + "  var i;"
                + "  slides=document.getElementsByClassName('mySlides');"
                + "  dots=document.getElementsByClassName('dot');"
                + "  for(i=0;i<slides.length;i++){slides[i].style.display='none';}"
                + "  slideIndex++;"
                + "  if(slideIndex>slides.length){slideIndex=1;}"
                + "  for(i=0;i<dots.length;i++){dots[i].className=dots[i].className.replace(' active','');}"
                + "  slides[slideIndex-1].style.display='block';"
                + "  dots[slideIndex-1].className+=' active';"
                + "  timer=setTimeout(showSlides,4000);" // Change slide every 8 seconds
                + "}"

                + "function plusSlides(n){"
                + "  clearTimeout(timer);"
                + "  slideIndex+=n;"
                + "  if(slideIndex>slides.length){slideIndex=1;}"
                + "  if(slideIndex<1){slideIndex=slides.length;}"
                + "  updateSlides();"
                + "}"

                + "function currentSlide(n){"
                + "  clearTimeout(timer);"
                + "  slideIndex=n;"
                + "  updateSlides();"
                + "}"

                + "function updateSlides(){"
                + "  var i;"
                + "  for(i=0;i<slides.length;i++){slides[i].style.display='none';}"
                + "  for(i=0;i<dots.length;i++){dots[i].className=dots[i].className.replace(' active','');}"
                + "  slides[slideIndex-1].style.display='block';"
                + "  dots[slideIndex-1].className+=' active';"
                + "  timer=setTimeout(showSlides,4000);"
                + "}"
                + "</script>";
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
                    + "font-family: Lato, sans-serif;}"

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

                    + ".about-toggle {"
                    + "width: 100%;"
                    + "background-color: #385f73;"
                    + "border: none;"
                    + "color: white;"
                    + "font-size: 36px;"
                    + "font-family: Lato, sans-serif;"
                    + "cursor: pointer;"
                    + "text-align: center;"
                    + "margin-bottom: 15px;"
                    + "padding: 0;"
                    + "position: relative;}"

                    + ".about-toggle i {"
                    + "font-size: 16px;"
                    + "position: absolute;"
                    + "right: 10%;"
                    + "top: 15px;}"

                    + ".about-toggle::after {"
                    + "content: '';"
                    + "display: block;"
                    + "width: 80%;"
                    + "max-width: 900px;"
                    + "height: 3px;"
                    + "background-color: white;"
                    + "margin: 15px auto 30px auto;}"

                    + "#about-content {display: block;}"
                    + "#about-content.collapsed {display: none;}"

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
                    + "text-align: left;"
                    + "transition: transform 0.2s ease, box-shadow 0.2s ease;}"

                    + ".info-box:hover {"
                    + "transform: translateY(-3px);"
                    + "box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);}"

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

                    + ".location-box .map-link {"
                    + "margin-top: 12px;}"

                    + ".location-box .map-link a {"
                    + "font-weight: bold;}"

                    + "</style>";

    public static String generateAboutUs() {
        return "<section class='about-us'>"
                + "<button class='about-toggle' onclick='toggleAbout()'>"
                + "<span class='about-title'>About Us</span>"
                + "<i id='about-icon' class='fa fa-minus'></i>"
                + "</button>"
                + "<div id='about-content'>"
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
                + "<li>High Dependency Care (HD) and Special Care (SC)</li></ul>"
                + "</div>"
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
                + "<p class='map-link'>"
                + "<a href=\"https://www.google.com/maps/place/Queen+Charlotte's+and+Chelsea+Hospital/@51.5163664,-0.2374661,17z/data=!3m1!4b1!4m6!3m5!1s0x487611d51e61f219:0x868f7e814c833d2a!8m2!3d51.5163664!4d-0.2374661!16s%2Fm%2F027hryh?entry=ttu&g_ep=EgoyMDI2MDEwNy4wIKXMDSoASAFQAw%3D%3D\""
                + "target='_blank'>"
                + "<i class='fa fa-external-link'></i> Open in Google Maps</a></p>"
                + "</div>";
    }


}
