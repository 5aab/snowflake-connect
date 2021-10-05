package an.awesome.pipelinr.custom.retry;

import an.awesome.pipelinr.Command;
import lombok.AllArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

@Order(2)
@AllArgsConstructor
public class Try<R, C extends Command<R>> implements Command.Middleware {

    private final C origin;

    @Override
    public <R, C extends Command<R>> R invoke(C command, Next<R> next) {
        RetryTemplate retryTemplate = new RetryTemplate();
        FixedBackOffPolicy fixedBackOffPolicy = new FixedBackOffPolicy();
        fixedBackOffPolicy.setBackOffPeriod(2000l);

        SimpleRetryPolicy simpleRetryPolicy = new SimpleRetryPolicy();
        simpleRetryPolicy.setMaxAttempts(3);

        retryTemplate.setBackOffPolicy(fixedBackOffPolicy);
        retryTemplate.setRetryPolicy(simpleRetryPolicy);
        return retryTemplate.execute(arg -> next.invoke());
    }
}
