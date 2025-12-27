public class WarningSystem {

    private final double lower;
    private final double upper;

    public WarningSystem(double lower, double upper) {
        this.lower = lower;
        this.upper = upper;
    }

    public boolean isAboveRange(double glucoseLevel) {
        return glucoseLevel > upper;
    }

    public boolean isBelowRange(double glucoseLevel) {
        return glucoseLevel < lower;
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
