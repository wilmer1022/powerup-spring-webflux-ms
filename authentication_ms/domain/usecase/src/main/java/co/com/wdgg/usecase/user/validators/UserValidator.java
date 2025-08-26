package co.com.wdgg.usecase.user.validators;

import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import co.com.wdgg.model.user.User;

public class UserValidator {
    
    private static final String EMAIL_REGEX = 
        "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
    private static final Pattern PATTERN = Pattern.compile(EMAIL_REGEX);
    private static final BigDecimal lowerBound = BigDecimal.ZERO;
    private static final BigDecimal upperBound = new BigDecimal("15000000");

    private final User user;

    public UserValidator(User user) {
        this.user = user;
    }

    // Functions to validate Not Null and Not Blank
    public String validateDocumentNumber() {
        if (user.documentNumber() == null || user.documentNumber().isBlank()) {
            return "El documento es obligatorio";
        }
        return null;
    }

    public String validateFirstName() {
        if (user.firstName() == null || user.firstName().isBlank()) {
            return "El nombre es obligatorio";
        }
        return null;
    }

    public String validateLastName() {
        if (user.lastName() == null || user.lastName().isBlank()) {
            return "El apellido es obligatorio";
        }
        return null;
    }

    public String validateEmail() {
        if (user.email() == null || user.email().isBlank()) {
            return "El correo electrónico es obligatorio";
        }
        return null;
    }

    public String validateSalary() {
        if (user.salary() == null) {
            return "El salario es obligatorio";
        }
        return null;
    }

    // Functions to validate logical constraints
    public String validateEmailForm() {
        if (!isValid(user.email())) {
            return "El correo electrónico no es válido";
        }
        return null;
    }

    public String validateSalaryForm() {
        if (user.salary().compareTo(lowerBound) < 0 || user.salary().compareTo(upperBound) > 0) {
            return "El salario debe estar entre " + lowerBound + " y " + upperBound;
        }
        return null;
    }

    private static boolean isValid(String email) {
        if (email == null) {
            return false;
        }
        Matcher matcher = PATTERN.matcher(email);
        return matcher.matches();
    }
}
