public class AlertRenderer {

    public static final String alertCSS =
            "<style>" +
                    ".alert-bubble {" +
                    "position: sticky;" +
                    "top: 15px;" +
                    "margin: 0 auto 20px auto;" +
                    "width: fit-content;" +
                    "max-width: 90%;" +
                    "background-color: #ffebee;" +
                    "color: #b71c1c;" +
                    "padding: 14px 20px;" +
                    "border-radius: 12px;" +
                    "box-shadow: 0 4px 10px rgba(0,0,0,0.15);" +
                    "font-weight: 600;" +
                    "z-index: 1000;" +
                    "display: flex;" +
                    "align-items: center;" +
                    "animation: fadeIn 0.5s ease-out;" +
                    "}" +

                    ".alert-high {" +
                    "background-color: #ffebee;" +
                    "border: 2px solid #b71c1c;" +
                    "color: #b71c1c;" +
                    "}" +

                    ".alert-low {" +
                    "background-color: #e3f2fd;" +
                    "border: 2px solid #0d47a1;" +
                    "color: #0d47a1;" +
                    "}" +

                    ".alert-close {" +
                    "margin-left: 15px;" +
                    "cursor: pointer;" +
                    "font-weight: bold;" +
                    "border: none;" +
                    "background: none;" +
                    "font-size: 16px;" +
                    "}" +

                    "@keyframes fadeIn {" +
                    "from {opacity: 0; transform: translateY(-10px);}" +
                    "to {opacity: 1; transform: translateY(0);}" +
                    "}" +

                    "</style>";

    public static String buildAlertHTML(WarningSystem warningSystem, double latestGlucose) {

        if (!warningSystem.isUnsafe(latestGlucose)) {
            return "";
        }

        String alertCategory = warningSystem.isAboveRange(latestGlucose)
                ? "alert-bubble alert-high"
                : "alert-bubble alert-low";

        return "<div class='"+ alertCategory +"'>" +
                warningSystem.getWarningMessage(latestGlucose)+
                "<button class='alert-close'" +
                "onclick=\"this.parentElement.style.display='none'\">&times</button>" +
                "</div>";
    }

}
