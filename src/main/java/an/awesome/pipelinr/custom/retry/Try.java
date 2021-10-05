package an.awesome.pipelinr.custom.retry;

import an.awesome.pipelinr.Command;
import lombok.AllArgsConstructor;
import org.springframework.core.annotation.Order;

@Order(2)
@AllArgsConstructor
public class Try<R, C extends Command<R>> implements Command.Middleware {

    private final C origin;

    @Override
    public <R, C extends Command<R>> R invoke(C command, Next<R> next) {
        return null;
    }
}
