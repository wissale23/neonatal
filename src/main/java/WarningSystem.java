public class WarningSystem {

    private final ConsultantServlet consultant;

    public WarningSystem(ConsultantServlet consultant) {
        this.consultant = consultant;
    }

    public boolean isAboveRange(double glucoseLevel) {
        return glucoseLevel > consultant.getUpper();
    }

    public boolean isBelowRange(double glucoseLevel) {
        return glucoseLevel < consultant.getLower();
    }

    public boolean isUnsafe(double glucoseLevel) {
        return isAboveRange(glucoseLevel) || isBelowRange(glucoseLevel);
    }

    public String getWarningMessage(double glucoseLevel) {
        if (isAboveRange(glucoseLevel)){
            return ("WARNING: Glucose level is above the safe range.");
        }
        else if (isBelowRange(glucoseLevel)){
            return ("WARNING: Glucose level below the safe range.");
        }
        return ("Glucose level is within the safe range.");
    }
}
