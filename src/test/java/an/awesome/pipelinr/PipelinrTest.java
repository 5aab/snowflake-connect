package an.awesome.pipelinr;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import an.awesome.pipelinr.PipelinrTest.Ping.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;

import an.awesome.pipelinr.Command;
import an.awesome.pipelinr.CommandHandlerNotFoundException;
import an.awesome.pipelinr.CommandHasMultipleHandlersException;
import an.awesome.pipelinr.PipelineStep;
import an.awesome.pipelinr.Pipelinr;
import an.awesome.pipelinr.Voidy;
import org.junit.jupiter.api.Test;

class PipelinrTest {

  @Test
  void supportsAbstractCommandHandlers() {
    // given
    HandlerThatExtendsAbstractClass handlerThatExtendsAbstractClass =
        new HandlerThatExtendsAbstractClass();

    // and
    an.awesome.pipelinr.Pipelinr pipelinr = new an.awesome.pipelinr.Pipelinr().with(() -> Stream.of(handlerThatExtendsAbstractClass));

    // when
    pipelinr.send(new Ping("hi"));

    // then:
    assertThat(handlerThatExtendsAbstractClass.receivedPings).containsOnly(new Ping("hi"));
  }

  @Test
  void executesCommandMiddlewaresInOrder() {
    // given
    List<String> invokedMiddlewareIds = new ArrayList<>();

    // and
    class IdentifiableMiddleware implements an.awesome.pipelinr.Command.Middleware {
      private final String id;

      private IdentifiableMiddleware(String id) {
        this.id = id;
      }

      @Override
      public <R, C extends an.awesome.pipelinr.Command<R>> R invoke(C command, Next<R> next) {
        invokedMiddlewareIds.add(id);
        return next.invoke();
      }
    }

    // and
    an.awesome.pipelinr.Command.Middleware foo = new IdentifiableMiddleware("foo");

    // and
    an.awesome.pipelinr.Command.Middleware bar = new IdentifiableMiddleware("bar");

    // and
    an.awesome.pipelinr.Command.Middleware baz = new IdentifiableMiddleware("baz");

    // and
    an.awesome.pipelinr.Pipelinr pipelinr =
        new an.awesome.pipelinr.Pipelinr().with(() -> Stream.of(new Pong2())).with(() -> Stream.of(foo, bar, baz));

    // when
    new Ping().execute(pipelinr);

    // then
    assertThat(invokedMiddlewareIds).containsExactly("foo", "bar", "baz");
  }

  @Test
  void executesPipelineStepsInOrder() {
    // given
    List<Integer> invokedStepNumbers = new ArrayList<>();

    // and
    an.awesome.pipelinr.PipelineStep first = new UniqueStep(1, invokedStepNumbers::add);

    // and
    an.awesome.pipelinr.PipelineStep second = new UniqueStep(2, invokedStepNumbers::add);

    // and
    an.awesome.pipelinr.PipelineStep third = new UniqueStep(3, invokedStepNumbers::add);

    // and
    an.awesome.pipelinr.Pipelinr pipelinr =
        new an.awesome.pipelinr.Pipelinr()
            .with(() -> Stream.of(new Pong2()))
            .with(() -> Stream.of(first, second, third));

    // when
    pipelinr.send(new Ping());

    // then
    assertThat(invokedStepNumbers).containsExactly(1, 2, 3);
  }

  @Test
  void supportsCustomHandlerMatching() {
    // given
    Hi hi = new Hi();
    PingSaver pingSaver = new PingSaver();

    // and
    an.awesome.pipelinr.Pipelinr pipelinr = new an.awesome.pipelinr.Pipelinr().with(() -> Stream.of(hi, pingSaver));

    // when
    pipelinr.send(new Ping("hi"));

    // and
    pipelinr.send(new Ping("bye"));

    // then:
    assertThat(hi.receivedPings).containsOnly(new Ping("hi"));
    assertThat(pingSaver.receivedPings).containsOnly(new Ping("bye"));
  }

  @Test
  void throwsIfSentCommandHasNoMatchingHandler() {
    // given
    an.awesome.pipelinr.Pipelinr pipelinr = new an.awesome.pipelinr.Pipelinr();

    // when
    Throwable e =
        assertThrows(
            CommandHandlerNotFoundException.class,
            () -> {
              pipelinr.send(new Ping());
            });

    // then
    assertThat(e).hasMessage("Cannot find a matching handler for Ping command");
  }

  @Test
  void throwsIfSentCommandHasMultipleHandlers() {
    // given
    an.awesome.pipelinr.Pipelinr pipelinr = new Pipelinr().with(() -> Stream.of(new Pong1(), new Pong2()));

    // when
    Throwable e =
        assertThrows(
            CommandHasMultipleHandlersException.class,
            () -> {
              pipelinr.send(new Ping());
            });

    // then
    assertThat(e)
        .hasMessage("Command Ping must have a single matching handler, but found 2 (Pong1, Pong2)");
  }

  static class UniqueStep implements PipelineStep {

    private final Integer no;
    private final Consumer<Integer> noConsumer;

    UniqueStep(Integer no, Consumer<Integer> noConsumer) {
      this.no = no;
      this.noConsumer = noConsumer;
    }

    @Override
    public <R, C extends an.awesome.pipelinr.Command<R>> R invoke(C command, Next<R> next) {
      noConsumer.accept(no);
      return next.invoke();
    }
  }

  static class Ping implements an.awesome.pipelinr.Command<an.awesome.pipelinr.Voidy> {

    private final String message;

    Ping() {
      this("");
    }

    Ping(String message) {
      this.message = message;
    }

    @Override
    public boolean equals(Object obj) {
      if (obj instanceof Ping) {
        Ping other = (Ping) obj;
        return other.message.equals(this.message);
      }
      return false;
    }

    static class PingSaver implements Handler<Ping, an.awesome.pipelinr.Voidy> {

      Collection<Ping> receivedPings = new ArrayList<>();

      @Override
      public an.awesome.pipelinr.Voidy handle(Ping command) {
        this.receivedPings.add(command);
        return new an.awesome.pipelinr.Voidy();
      }

      @Override
      public boolean matches(Ping command) {
        return command.message.equals("bye");
      }
    }

    static class Hi implements Handler<Ping, an.awesome.pipelinr.Voidy> {

      Collection<Ping> receivedPings = new ArrayList<>();

      @Override
      public an.awesome.pipelinr.Voidy handle(Ping command) {
        this.receivedPings.add(command);
        return new an.awesome.pipelinr.Voidy();
      }

      @Override
      public boolean matches(Ping command) {
        return command.message.equals("hi");
      }
    }

    static class Pong1 implements Handler<Ping, an.awesome.pipelinr.Voidy> {
      @Override
      public an.awesome.pipelinr.Voidy handle(Ping command) {
        System.out.println("Pong 1");
        return new an.awesome.pipelinr.Voidy();
      }
    }

    static class Pong2 implements Handler<Ping, an.awesome.pipelinr.Voidy> {
      @Override
      public an.awesome.pipelinr.Voidy handle(Ping command) {
        System.out.println("Pong 2");
        return new an.awesome.pipelinr.Voidy();
      }
    }

    static class HandlerThatExtendsAbstractClass extends AbstractHandler<Ping, an.awesome.pipelinr.Voidy> {

      Collection<Ping> receivedPings = new ArrayList<>();

      @Override
      public an.awesome.pipelinr.Voidy handle(Ping command) {
        this.receivedPings.add(command);
        return new Voidy();
      }
    }

    abstract static class AbstractHandler<C extends Command<R>, R> implements Handler<C, R> {}
  }
}
