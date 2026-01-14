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
                + buttonsCSS

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

    public static final String buttonsCSS =
            "<style>" +
                    ".links-grid {"
                    + "max-width: 1000px;"
                    + "display: grid;"
                    + "gap: 50px;"
                    + "grid-template-columns: repeat(2, 1fr);"
                    + "margin: 40px auto;}"

                    // Reference 1 - Button taken from https://www.w3schools.com/howto/howto_css_pill_button.asp
                    + ".button {"
                    + "background-color: #ae2573;"
                    + "border: none;"
                    + "border-radius: 10px;"
                    + "color: white;"
                    + "padding:20px 20px;"
                    + "display: flex;"
                    + "align-items: center;"
                    + "justify-content: center;"
                    + "gap: 20px;"
                    + "text-decoration: none;"
                    + "font-size: 20px;"
                    + "font-weight: bold;"
                    + "cursor: pointer;"
                    + "transition: transform 0.2s ease, box-shadow 0.2s ease;}"

                    + ".button:hover {"
                    + "text-decoration: underline;"
                    + "transform: translateY(-3px);"
                    + "box-shadow: 0 5px 15px rgba(0, 0, 0, 0.1);}"

                    + "</style>";

    public static String generateLinks(){
        return "<div class='links-grid'>"
                + "<a class='button' href=\"https://www.imperial.nhs.uk/-/media/website/patient-information-leaflets/neonatology/welcome-to-the-neonatal-unit-at-queen-charlottes-and-chelsea-hospital.pdf?rev=677ec09113014b638bf5bc434c1bd69c&sc_lang=en&hash=502A869B7B6D03FBDD796F1E07818A99\""
                + "target='_blank'>"
                + "<i class='fa fa-external-link'></i> Welcome to the neonatal unit at Queen Charlotte's & Chelsea Hospital</a>"

                + "<a class='button' href=\"https://www.imperial.nhs.uk/-/media/website/patient-information-leaflets/neonatology/biometric-fingerprint-access-to-imperial-neonatal-units.pdf?rev=14ab2d05efe24f31bfef1fc55b946cf4&hash=D2E131CD2C9DDA3E578BE583BF9992D4\""
                + "target='_blank'>"
                + "<i class='fa fa-external-link'></i> Biometric fingerprint access to Imperial neonatal units</a>"

                + "<a class='button' href=\"https://www.imperial.nhs.uk/-/media/website/patient-information-leaflets/neonatology/breast-milk-fortifier-on-discharge.pdf?rev=7ea9156575694eafae64325f348a3c2a&hash=DD856CE71F8C53120A28AC435FF1E20A\""
                + "target='_blank'>"
                + "<i class='fa fa-external-link'></i> Breast milk fortifier on discharge</a>"

                + "<a class='button' href=\"https://www.imperial.nhs.uk/-/media/website/patient-information-leaflets/neonatology/choosing-a-bottle-for-your-baby.pdf?rev=9ee0aaa2921e4f68b26892c0d687d5dd&hash=2CBF7ADA2E554D3DDAC2BF9855F3371E\""
                + "target='_blank'>"
                + "<i class='fa fa-external-link'></i> Choosing a bottle for your baby</a>"

                + "<a class='button' href=\"https://www.imperial.nhs.uk/-/media/website/patient-information-leaflets/maternity-services/choosing-a-breast-pump.pdf?rev=1043388c05c4401b99c4027581df4f6f&hash=455DF3C403B46FF96D349FBF50F22340\""
                + "target='_blank'>"
                + "<i class='fa fa-external-link'></i> Choosing a breast pump</a>"

                + "<a class='button' href=\"https://www.imperial.nhs.uk/-/media/website/patient-information-leaflets/neonatology/delayed-cord-clamping.pdf?rev=31dd7a6f13b04277a8e46704d443b76d&hash=2E6386D07A93561578EAC7BE8AD81A62\""
                + "target='_blank'>"
                + "<i class='fa fa-external-link'></i> Delayed cord clamping</a>"

                + "<a class='button' href=\"https://www.imperial.nhs.uk/-/media/website/patient-information-leaflets/neonatology/dummies-on-the-neonatal-unit.pdf?rev=cd54f9b741c14cf0b5221af47d45a595&sc_lang=en&hash=A39ADD55A98ADA31A1B6F3A351B6DE75\""
                + "target='_blank'>"
                + "<i class='fa fa-external-link'></i> Dummies on the neonatal unit</a>"

                + "<a class='button' href=\"https://www.imperial.nhs.uk/-/media/website/patient-information-leaflets/neonatology/dummies-on-the-neonatal-unit.pdf?rev=cd54f9b741c14cf0b5221af47d45a595&sc_lang=en&hash=A39ADD55A98ADA31A1B6F3A351B6DE75\""
                + "target='_blank'>"
                + "<i class='fa fa-external-link'></i> Eating well for mothers who are expressing</a>"

                + "<a class='button' href=\"https://www.imperial.nhs.uk/-/media/website/patient-information-leaflets/neonatology/food-for-parents-guardians-and-carers-on-the-qcch-neonatal-unit.pdf?rev=3009ae9d0e7d441792df9d5e885c24ac&hash=9A6C2F1606A1254C7635DA932583600D\""
                + "target='_blank'>"
                + "<i class='fa fa-external-link'></i> Food for parents, guardians, and carers on the QCCH</a>"

                + "<a class='button' href=\"https://www.imperial.nhs.uk/-/media/website/patient-information-leaflets/neonatology/food-for-parents-guardians-and-carers-on-the-qcch-neonatal-unit.pdf?rev=3009ae9d0e7d441792df9d5e885c24ac&hash=9A6C2F1606A1254C7635DA932583600D\""
                + "target='_blank'>"
                + "<i class='fa fa-external-link'></i> General movements assessment (GMA)</a>"

                + "<a class='button' href=\"https://www.imperial.nhs.uk/-/media/website/patient-information-leaflets/neonatology/food-for-parents-guardians-and-carers-on-the-qcch-neonatal-unit.pdf?rev=3009ae9d0e7d441792df9d5e885c24ac&hash=9A6C2F1606A1254C7635DA932583600D\""
                + "target='_blank'>"
                + "<i class='fa fa-external-link'></i> Non-birthing partners in the neonatal intensive care unit (NICU)</a>"

                + "<a class='button' href=\"https://www.imperial.nhs.uk/-/media/website/patient-information-leaflets/neonatology/food-for-parents-guardians-and-carers-on-the-qcch-neonatal-unit.pdf?rev=3009ae9d0e7d441792df9d5e885c24ac&hash=9A6C2F1606A1254C7635DA932583600D\""
                + "target='_blank'>"
                + "<i class='fa fa-external-link'></i> Integrated family care programme</a>"

                + "<a class='button' href=\"https://apps.apple.com/gb/app/ifdc/id1196284490\""
                + "target='_blank'>"
                + "<i class='fa fa-external-link'></i> Integrated family delivery care (IFDC) app</a>"

                + "<a class='button' href=\"https://www.imperial.nhs.uk/-/media/website/patient-information-leaflets/childrens-services/childrens-physiotherapy/positional-talipes-calcaneovalgus.pdf?rev=c0042113cb464805bce04436e30cfee4&hash=E57D079826357BAD8D3B5289486A0E27\""
                + "target='_blank'>"
                + "<i class='fa fa-external-link'></i> Positional Talipes Calcaneovalgus</a>"

                + "<a class='button' href=\"https://www.imperial.nhs.uk/-/media/website/patient-information-leaflets/neonatology/probiotics-for-premature-babies.pdf?rev=fbd8f1e0dace4199be003478593a34e7&hash=8FA286CC9BBC48C06418AB08C501EF9B\""
                + "target='_blank'>"
                + "<i class='fa fa-external-link'></i> Probiotics for premature babies</a>"

                + "<a class='button' href=\"https://www.imperial.nhs.uk/-/media/website/patient-information-leaflets/neonatology/screening-for-antibiotic-resistant-bacteria.pdf?rev=379e3e70bb1b4777b76685419dd831a9&hash=E4BE24927890974DDC8415D2074B437D\""
                + "target='_blank'>"
                + "<i class='fa fa-external-link'></i> Screening for antibiotic resistant bacteria</a>"

                + "<a class='button' href=\"https://www.imperial.nhs.uk/-/media/website/patient-information-leaflets/neonatology/vitamin-d.pdf?rev=9cdca3a9793b47ef968b3b83c2763a21&hash=B6DDF2C04603FC5C7BE7A6075D7AB133\""
                + "target='_blank'>"
                + "<i class='fa fa-external-link'></i> Vitamin D</a>"

                + "<a class='button' href=\"https://www.imperial.nhs.uk/-/media/website/patient-information-leaflets/neonatology/vitamin-d.pdf?rev=9cdca3a9793b47ef968b3b83c2763a21&hash=B6DDF2C04603FC5C7BE7A6075D7AB133\""
                + "target='_blank'>"
                + "<i class='fa fa-external-link'></i> Vitamin K</a>"

                + "</div>";
    }
}
