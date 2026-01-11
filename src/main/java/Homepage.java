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
                + "</head><body>"
                + generateSidebar()           // sidebar HTML
                + generateSlideshow(contextPath)         // slideshow content
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
        // Reference 3 - Slideshow taken from https://www.w3schools.com/howto/howto_js_slideshow.asp
        return "<div class='slideshow-container'>"
                + "  <div class='mySlides fade'>"
                + "  <img src='" + contextPath + "/images/1.png' style='width:100%'>"
                + "  </div>"
                + "  <div class='mySlides fade'>"
                + "    <img src='" + contextPath + "/images/2.jpg' style='width:100%'>"
                + "  </div>"
                + "  <div class='mySlides fade'>"
                + "    <img src='" + contextPath + "/images/3.jpg' style='width:100%'>"
                + "  </div>"
                + "  <a class='prev' onclick='plusSlides(-1)'>&#10094;</a>"
                + "  <a class='next' onclick='plusSlides(1)'>&#10095;</a>"
                + "</div>"
                + "<br>"
                + "<div style='text-align:center'>"
                + "  <span class='dot' onclick='currentSlide(1)'></span>"
                + "  <span class='dot' onclick='currentSlide(2)'></span>"
                + "  <span class='dot' onclick='currentSlide(3)'></span>"
                + "</div>"
                + "<script>"
                + "let slideIndex = 1;"
                + "showSlides(slideIndex);"
                + "function plusSlides(n){ showSlides(slideIndex += n); }"
                + "function currentSlide(n){ showSlides(slideIndex = n); }"
                + "function showSlides(n){"
                + "  let i;"
                + "  let slides = document.getElementsByClassName('mySlides');"
                + "  let dots = document.getElementsByClassName('dot');"
                + "  if(n > slides.length){slideIndex = 1;}"
                + "  if(n < 1){slideIndex = slides.length;}"
                + "  for(i=0;i<slides.length;i++){slides[i].style.display='none';}"
                + "  for(i=0;i<dots.length;i++){dots[i].className=dots[i].className.replace(' active','');}"
                + "  slides[slideIndex-1].style.display='block';"
                + "  dots[slideIndex-1].className += ' active';"
                + "}"
                + "</script>"
                + "<style>"
                + " .mySlides{display:none;} "
                + " .fade{animation-name:fade;animation-duration:1.5s;} "
                + " @keyframes fade{from{opacity:.4} to{opacity:1}} "
                + " .prev, .next{cursor:pointer;position:absolute;top:50%;width:auto;padding:16px;margin-top:-22px;color:white;font-weight:bold;font-size:18px;transition:0.6s;user-select:none;} "
                + " .prev{left:0;border-radius:0 3px 3px 0;} "
                + " .next{right:0;border-radius:3px 0 0 3px;} "
                + " .prev:hover, .next:hover{background-color:rgba(0,0,0,0.8);} "
                + " .dot{cursor:pointer;height:15px;width:15px;margin:0 2px;background-color:#bbb;border-radius:50%;display:inline-block;transition:background-color 0.6s;} "
                + " .active, .dot:hover{background-color:#717171;}"
                + "</style>";
    }

}
