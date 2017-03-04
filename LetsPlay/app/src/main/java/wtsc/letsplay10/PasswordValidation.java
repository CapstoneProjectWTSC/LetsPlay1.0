package wtsc.letsplay10;

/**
 * Created by John on 2/21/2017.
 */

public class PasswordValidation {

    public String validateNewPass(String pass1, String pass2) {
        StringBuilder retVal = new StringBuilder();

        if (pass1.length() < 1 || pass2.length() < 1) retVal.append("Empty fields\n");

        if (pass1 != null && pass2 != null) {

            if (pass1.equals(pass2)) {
                pass1 = pass2;
                boolean hasUppercase = !pass1.equals(pass1.toLowerCase());
                boolean hasLowercase = !pass1.equals(pass1.toUpperCase());
                boolean hasNumber = pass1.matches(".*\\d.*");
                boolean noSpecialChar = pass1.matches("[a-zA-Z0-9 ]*");

                if (pass1.length() < 8) {
                    if (retVal.length() == 0)
                    {
                        retVal.append("Your password needs:\n");
                    }
                    retVal.append("to be 8 characters or greater\n");
                }
                if (!hasUppercase) {
                    if (retVal.length() == 0)
                    {
                        retVal.append("Your password needs:\n");
                    }
                    retVal.append("to use at least one uppercase character\n");
                }
                if (!hasLowercase) {
                    if (retVal.length() == 0)
                    {
                        retVal.append("Your password needs:\n");
                    }
                    retVal.append("to use at least one lowercase character\n");
                }
                if (!hasNumber && noSpecialChar) {
                    if (retVal.length() == 0)
                    {
                        retVal.append("Your password needs:\n");
                    }
                    retVal.append("to use at least one number or special character i.e. !,@,#, etc.\n");
                }
            } else {
                retVal.append("Your password entries don't match!");
            }
        } else {
            retVal.append("You must enter a password!");
        }
        if (retVal.length() == 0) {
            retVal.append("Success!");
        }
        return retVal.toString();
    }
}