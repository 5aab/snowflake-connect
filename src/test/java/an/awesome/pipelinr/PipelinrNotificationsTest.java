package an.awesome.pipelinr;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

import static java.lang.Thread.currentThread;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class PipelinrNotificationsTest {

  private AtomicLong timesHandled = new AtomicLong();
  private Collection<Thread> threads = new HashSet<>();

  @Test
  void executesMiddlewareInOrder() {
    // given
    List<String> invokedMiddlewareIds = new ArrayList<>();

    // and
    class IdentifiableMiddleware implements an.awesome.pipelinr.Notification.Middleware {
      private final String id;

      private IdentifiableMiddleware(String id) {
        this.id = id;
      }

      @Override
      public <N extends an.awesome.pipelinr.Notification> void invoke(N notification, Next next) {
        invokedMiddlewareIds.add(id);
        next.invoke();
      }
    }

    // and
    class Greet implements an.awesome.pipelinr.Notification {}

    class OnGreet implements an.awesome.pipelinr.Notification.Handler<Greet> {

      @Override
      public void handle(Greet notification) {
        System.out.println("OK");
      }
    }

    // and
    an.awesome.pipelinr.Notification.Middleware foo = new IdentifiableMiddleware("foo");

    // and
    an.awesome.pipelinr.Notification.Middleware bar = new IdentifiableMiddleware("bar");

    // and
    an.awesome.pipelinr.Notification.Middleware baz = new IdentifiableMiddleware("baz");

    // and
    an.awesome.pipelinr.Pipelinr pipelinr =
        new an.awesome.pipelinr.Pipelinr().with(() -> Stream.of(new OnGreet())).with(() -> Stream.of(foo, bar, baz));

    // when
    new Greet().send(pipelinr);

    // then
    assertThat(invokedMiddlewareIds).containsExactly("foo", "bar", "baz");
  }

  @Test
  void supportsAsyncStrategy() {
    // given:
    ExecutorService threadPool = Executors.newFixedThreadPool(1);
    an.awesome.pipelinr.Pipeline pipeline =
        pipelinrWithHandlers(
                new ThrowingRespublican("Omg"),
                new ThrowingRespublican("Oh!"),
                new Bush(),
                new Trump())
            .with(() -> new Async(threadPool));

    // when:
    RuntimeException e =
        assertThrows(RuntimeException.class, () -> new GreetRespublicans().send(pipeline));

    // then:
    assertThat(e).isExactlyInstanceOf(an.awesome.pipelinr.AggregateException.class);
    assertThat(timesHandled).hasValue(4);
    assertThat(threads).hasSize(1);
    assertThat(threads).doesNotContain(currentThread());
  }

  @Test
  void supportsParallelNoWait() throws InterruptedException {
    // given:
    ExecutorService threadPool = Executors.newFixedThreadPool(4);
    an.awesome.pipelinr.Pipeline pipeline =
        pipelinrWithHandlers(
                new ThrowingRespublican("Omg"),
                new ThrowingRespublican("Oh!"),
                new Bush(),
                new Trump())
            .with(() -> new ParallelNoWait(threadPool));

    // when:
    new GreetRespublicans().send(pipeline);
    threadPool.awaitTermination(3, TimeUnit.SECONDS);

    // then:
    assertThat(timesHandled).hasValue(4);
    assertThat(threads).hasSize(4);
    assertThat(threads).doesNotContain(currentThread());
  }

  @Test
  void supportsParallelWhenAllStrategy() {
    // given:
    ExecutorService threadPool = Executors.newFixedThreadPool(4);
    an.awesome.pipelinr.Pipeline pipeline =
        pipelinrWithHandlers(
                new ThrowingRespublican("Omg"),
                new ThrowingRespublican("Oh!"),
                new Bush(),
                new Trump())
            .with(() -> new ParallelWhenAll(threadPool));

    // when:
    RuntimeException e =
        assertThrows(RuntimeException.class, () -> new GreetRespublicans().send(pipeline));

    // then:
    assertThat(e).isExactlyInstanceOf(an.awesome.pipelinr.AggregateException.class);
    assertThat(e).hasMessageContaining("2 exception(s)");
    assertThat(timesHandled).hasValue(4);
    assertThat(threads).hasSize(4);
    assertThat(threads).doesNotContain(currentThread());
  }

  @Test
  void supportsParallelWhenAnyStrategy() throws InterruptedException {
    // given:
    ExecutorService threadPool = Executors.newFixedThreadPool(4);
    an.awesome.pipelinr.Pipeline pipeline =
        pipelinrWithHandlers(
                new ThrowingRespublican("Omg", 2000),
                new ThrowingRespublican("Oh!", 2000),
                new ThrowingRespublican("Nah"),
                new ThrowingRespublican("Boo", 2000))
            .with(() -> new ParallelWhenAny(threadPool));

    // when:
    RuntimeException e =
        assertThrows(RuntimeException.class, () -> new GreetRespublicans().send(pipeline));

    // then:
    assertThat(e).isExactlyInstanceOf(an.awesome.pipelinr.AggregateException.class);
    assertThat(e).hasMessageContaining("1 exception(s)");

    threadPool.awaitTermination(5, TimeUnit.SECONDS);
    assertThat(timesHandled).hasValue(4);
    assertThat(threads).hasSize(4);
    assertThat(threads).doesNotContain(currentThread());
  }

  @Test
  void supportsContinueOnExceptionStrategy() {
    // given:
    an.awesome.pipelinr.Pipeline pipeline =
        pipelinrWithHandlers(
                new ThrowingRespublican("Omg"),
                new ThrowingRespublican("Oh!"),
                new Bush(),
                new Trump())
            .with(ContinueOnException::new);

    // when:
    RuntimeException e =
        assertThrows(RuntimeException.class, () -> new GreetRespublicans().send(pipeline));

    // then:
    assertThat(e).isExactlyInstanceOf(AggregateException.class);
    assertThat(e).hasMessageContaining("2");
    assertThat(timesHandled).hasValue(4);
    assertThat(threads).containsExactly(currentThread());
  }

  @Test
  void stopOnExceptionIsDefaultStrategy() {
    // given:
    an.awesome.pipelinr.Pipeline pipeline =
        pipelinrWithHandlers(
            new ThrowingRespublican("Omg"),
            new ThrowingRespublican("Oh!"),
            new Bush(),
            new Trump());

    // when:
    RuntimeException e =
        assertThrows(RuntimeException.class, () -> new GreetRespublicans().send(pipeline));

    // then:
    assertThat(timesHandled).hasValue(1);
    assertThat(e).hasMessage("Omg");
    assertThat(threads).containsExactly(currentThread());
  }

  @Test
  void sendsNotificationToAllHandlers() {
    // given:
    Pipeline pipeline = pipelinrWithHandlers(new Bush(), new Trump(), new Hilary());

    // when:
    new GreetRespublicans().send(pipeline);

    // then:
    assertThat(timesHandled).hasValue(2);
  }

  private an.awesome.pipelinr.Pipelinr pipelinrWithHandlers(an.awesome.pipelinr.Notification.Handler... handlers) {
    return new Pipelinr().with(() -> Stream.of(handlers));
  }

  static class GreetDemocrats implements an.awesome.pipelinr.Notification {}

  static class GreetRespublicans implements an.awesome.pipelinr.Notification {}

  class Hilary extends BaseNotificationHandler<GreetDemocrats> {
    @Override
    public void handleNow(GreetDemocrats notification) {}
  }

  class Bush extends BaseNotificationHandler<GreetRespublicans> {
    @Override
    public void handleNow(GreetRespublicans notification) {}
  }

  class Trump extends BaseNotificationHandler<GreetRespublicans> {
    @Override
    public void handleNow(GreetRespublicans notification) {}
  }

  class ThrowingRespublican extends BaseNotificationHandler<GreetRespublicans> {
    private final String message;
    private final long sleepMillis;

    ThrowingRespublican(String message) {
      this.message = message;
      this.sleepMillis = 0;
    }

    public ThrowingRespublican(String message, long sleepMillis) {
      this.message = message;
      this.sleepMillis = sleepMillis;
    }

    @Override
    public void handleNow(GreetRespublicans notification) {
      try {
        Thread.sleep(sleepMillis);
      } finally {
        throw new RuntimeException(message);
      }
    }
  }

  abstract class BaseNotificationHandler<N extends an.awesome.pipelinr.Notification>
      implements Notification.Handler<N> {

    @Override
    public void handle(N notification) {
      threads.add(currentThread());
      timesHandled.incrementAndGet();
      handleNow(notification);
    }

    abstract void handleNow(N notification);
  }
}
