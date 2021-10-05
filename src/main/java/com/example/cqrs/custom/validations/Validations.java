package com.example.cqrs.custom.validations;

import an.awesome.pipelinr.Command;
import lombok.AllArgsConstructor;
import org.springframework.core.annotation.Order;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;

@Order(4)
@AllArgsConstructor
public class Validations implements Command.Middleware {

    private Validator validator;

    @Override
    public <R, C extends Command<R>> R invoke(C command, Next<R> next) {
        Set<ConstraintViolation<C>> validations = validator.validate(command);
        if(!validations.isEmpty()){
            throw new CommandValidationException(validations);
        }
        return next.invoke();
    }
}
