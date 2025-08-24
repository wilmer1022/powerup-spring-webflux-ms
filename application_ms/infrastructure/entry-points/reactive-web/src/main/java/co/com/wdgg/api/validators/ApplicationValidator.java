package co.com.wdgg.api.validators;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.security.SecurityProperties.User;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ObjectError;
import org.springframework.validation.Validator;

import co.com.wdgg.model.application.Application;

@Component
public class ApplicationValidator implements Validator {
    
    private static final Logger logger = LoggerFactory.getLogger(ApplicationValidator.class);

    private enum ApplicationCreditType {
        PERSONAL,
        HIPOTECARIO,
        LIBRANZA,
        VEHICULO
    }

    @Override
    public boolean supports(@NonNull Class<?> clazz) {
        return User.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(@NonNull Object target, @NonNull Errors errors) {

        final Application application = (Application) target;

        final BigDecimal lowerBound = BigDecimal.ZERO;
        final BigDecimal upperBound = new BigDecimal("10000000000");

        // Validate Not Null and Not Blank
        if (application.userDocumentNumber() == null || application.userDocumentNumber().isBlank()) {
            errors.rejectValue("userDocumentNumber", "userDocumentNumber.required", "El documento es obligatorio");
        }

        if (application.amount() == null) {
            errors.rejectValue("amount", "amount.required", "El monto es obligatorio");
        }

        // Validate logical constraints
        if (application.amount().compareTo(lowerBound) < 0 || application.amount().compareTo(upperBound) > 0) {
            errors.rejectValue("amount", "amount.invalid", "El monto debe estar entre " + lowerBound + " y " + upperBound);
        }

        if (Arrays.stream(ApplicationCreditType.values()).noneMatch(type -> type.name().equals(application.creditType().toUpperCase()))) {
            errors.rejectValue("creditType", "creditType.invalid", "El tipo de credito no es válido");
        }

        if (errors.hasErrors()) {
            logger.error("Error al validar la solicitud de credito [{}]: {}", application, errors.getAllErrors());
            throw new IllegalArgumentException("Error al validar la solicitud de credito: " + errors.getAllErrors().stream()
                    .map(ObjectError::getDefaultMessage)
                    .collect(Collectors.joining(", ")));
        }
    }
}
