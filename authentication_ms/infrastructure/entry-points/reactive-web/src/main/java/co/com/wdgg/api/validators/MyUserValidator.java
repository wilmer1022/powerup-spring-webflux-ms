package co.com.wdgg.api.validators;

import java.math.BigDecimal;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.validation.Validator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import co.com.wdgg.model.user.User;

@Component
public class MyUserValidator implements Validator {

    private static final Logger logger = LoggerFactory.getLogger(MyUserValidator.class);

    private static final String EMAIL_REGEX = 
        "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";

    private static final Pattern PATTERN = Pattern.compile(EMAIL_REGEX);
    
    @Override
    public boolean supports(@NonNull Class<?> clazz) {
        return User.class.isAssignableFrom(clazz);
    }

    public void validate(@NonNull Object target, @NonNull Errors errors) {

        final User user = (User) target;

        final BigDecimal lowerBound = BigDecimal.ZERO;
        final BigDecimal upperBound = new BigDecimal("15000000");

        // Validate Not Null and Not Blank
        if (user.documentNumber() == null || user.documentNumber().isBlank()) {
            errors.rejectValue("documentNumber", "documentNumber.required", "El documento es obligatorio");
        }

        if (user.firstName() == null || user.firstName().isBlank()) {
            errors.rejectValue("firstName", "firstName.required", "El nombre es obligatorio");
        }

        if (user.lastName() == null || user.lastName().isBlank()) {
            errors.rejectValue("lastName", "lastName.required", "El apellido es obligatorio");
        }

        if (user.email() == null || user.email().isBlank()) {
            errors.rejectValue("email", "email.required", "El correo electrónico es obligatorio");
        }

        if (user.salary() == null) {
            errors.rejectValue("salary", "salary.required", "El salario es obligatorio");
        }

        // Validate logical constraints
        if (!isValid(user.email())) {
            errors.rejectValue("email", "email.invalid", "El correo electrónico no es válido");
        }
        
        if (user.salary().compareTo(lowerBound) < 0 || user.salary().compareTo(upperBound) > 0) {
            errors.rejectValue("salary", "salary.invalid", "El salario debe estar entre " + lowerBound + " y " + upperBound);
        }

        if (errors.hasErrors()) {
            logger.error("Error al validar el usuario [{}]: {}", user, errors.getAllErrors());
            throw new IllegalArgumentException("Error al validar el usuario: " + errors.getAllErrors().stream()
                    .map(ObjectError::getDefaultMessage)
                    .collect(Collectors.joining(", ")));
        }
    }

    public static boolean isValid(String email) {
        if (email == null) {
            return false;
        }
        Matcher matcher = PATTERN.matcher(email);
        return matcher.matches();
    }
}
