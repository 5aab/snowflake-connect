package an.awesome.pipelinr.custom.logging;

import an.awesome.pipelinr.Command;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;

@Order(1)
public class Loggable implements Command.Middleware {

    @Override
    public <R, C extends Command<R>> R invoke(C command, Next<R> next) {
        Logger logger = logger(command);
        logger.info("Command =>>>>>>>>>>>> {}", command);
        R response = next.invoke();
        logger.info("Response for {} =>>>>>>>>>>>> {}",command.getClass().getSimpleName(), response);
        return response;
    }

    private <R, C extends Command<R>> Logger logger(C command) {
        return LoggerFactory.getLogger(command.getClass());
    }
}
