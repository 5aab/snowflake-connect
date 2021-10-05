package an.awesome.pipelinr.custom.validations;

import an.awesome.pipelinr.Command;

import javax.validation.ConstraintViolation;
import java.util.Set;
import java.util.TreeSet;

public class CommandValidationException extends RuntimeException {
    public <R, C extends Command<R>> CommandValidationException(Set<ConstraintViolation<C>> violations) {
        super("One or More JSR303 constraints were violated " + System.lineSeparator() + convertToString(violations));
    }

    private static <R, C extends Command<R>> String convertToString(Set<ConstraintViolation<C>> violations) {
        Set<String> sortedViolations = new TreeSet<>();
        for (ConstraintViolation<C> violation : violations) {
            String msg = "Property :" + violation.getPropertyPath();
            msg += " in " + violation.getRootBeanClass();
            msg += " " + violation.getMessage();
            sortedViolations.add(msg);
        }
        return String.join(System.lineSeparator(), sortedViolations);
    }
}
