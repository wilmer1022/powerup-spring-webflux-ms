package co.com.wdgg.model.application.validators;

import java.math.BigDecimal;

import co.com.wdgg.model.application.Application;

public class ApplicationValidator {

    private static final BigDecimal lowerBound = BigDecimal.ZERO;
    private static final BigDecimal upperBound = new BigDecimal("10000000000");

    private final Application application;
    
    public ApplicationValidator(Application application) {
        this.application = application;
    }

    // Validate Not Null and Not Blank
    public String validateUserDocumentNumber() {
        if (application.userDocumentNumber() == null || application.userDocumentNumber().isBlank()) {
            return "El documento es obligatorio";
        }
        return null;
    }
    
    public String validateAmount() {
        if (application.amount() == null) {
            return "El monto es obligatorio";
        }
        return null;
    }

    public String validateCreditType() {
        if (application.applicationCreditType() == null) {
            return "El tipo de credito es obligatorio";
        }
        return null;
    }

    // Validate logical constraints
    public String validateAmountForm() {
        if (application.amount().compareTo(lowerBound) < 0 || application.amount().compareTo(upperBound) > 0) {
            return "El monto debe estar entre " + lowerBound + " y " + upperBound;
        }
        return null;
    }
}
