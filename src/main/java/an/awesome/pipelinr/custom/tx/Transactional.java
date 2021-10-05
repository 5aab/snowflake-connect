package an.awesome.pipelinr.custom.tx;

import an.awesome.pipelinr.Command;
import lombok.AllArgsConstructor;
import org.springframework.core.annotation.Order;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import static org.springframework.transaction.TransactionDefinition.PROPAGATION_REQUIRED;
import static org.springframework.transaction.TransactionDefinition.PROPAGATION_SUPPORTS;

@Order(6)
@AllArgsConstructor
public class Transactional implements Command.Middleware {

    private final PlatformTransactionManager txManager;

    @Override
    public <R, C extends Command<R>> R invoke(C command, Next<R> next) {
        TransactionTemplate tx = new TransactionTemplate(txManager);
        tx.setName("Tx for "+ command.getClass().getSimpleName());
        tx.setReadOnly(command instanceof ReadOnly);
        tx.setPropagationBehavior((command instanceof NoTransaction) ? PROPAGATION_SUPPORTS : PROPAGATION_REQUIRED);
        return tx.execute(status -> next.invoke());
    }
}
