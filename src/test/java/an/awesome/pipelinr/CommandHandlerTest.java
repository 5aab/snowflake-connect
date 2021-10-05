package an.awesome.pipelinr;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

class CommandHandlerTest {

  @Test
  void resolvesHandlersWithAGenericCommandType() {
    an.awesome.pipelinr.Pipeline pipeline = new an.awesome.pipelinr.Pipelinr(() -> Stream.of(new HandlerWithAGenericCommandType()));

    String results = pipeline.send(new Foo<>(new Bar()));
    assertThat(results).isEqualTo("Bar");
  }

  class Bar implements an.awesome.pipelinr.Command<String> {}

  class Foo<C extends an.awesome.pipelinr.Command<R>, R> implements an.awesome.pipelinr.Command<R> {
    C wrappee;

    Foo(C wrappee) {
      this.wrappee = wrappee;
    }
  }

  class HandlerWithAGenericCommandType<C extends an.awesome.pipelinr.Command<R>, R>
      implements an.awesome.pipelinr.Command.Handler<Foo<C, R>, R> {

    @SuppressWarnings("unchecked")
    @Override
    public R handle(Foo command) {
      return (R) command.wrappee.getClass().getSimpleName();
    }
  }

  @Test
  void handlesCommandsThatAreSubtypesOfAGenericArgument() {
    // given
    Ping.Handler pingHandler = new Ping.Handler();
    NotAPing.Handler notAPingHandler = new NotAPing.Handler();
    Pipeline pipeline = new Pipelinr(() -> Stream.of(pingHandler, notAPingHandler));

    // and
    Ping ping = new Ping();
    SmartPing smartPing = new SmartPing();
    NotAPing notAPing = new NotAPing();

    // when
    pipeline.send(ping);
    pipeline.send(smartPing);
    pipeline.send(notAPing);

    // then
    assertThat(pingHandler.handled).containsOnly(ping, smartPing);
    assertThat(notAPingHandler.handled).containsOnly(notAPing);
  }

  static class Ping implements an.awesome.pipelinr.Command<an.awesome.pipelinr.Voidy> {

    static class Handler implements an.awesome.pipelinr.Command.Handler<Ping, an.awesome.pipelinr.Voidy> {

      private Collection<an.awesome.pipelinr.Command> handled = new ArrayList<>();

      @Override
      public an.awesome.pipelinr.Voidy handle(Ping command) {
        handled.add(command);
        return new an.awesome.pipelinr.Voidy();
      }
    }
  }

  static class SmartPing extends Ping {}

  static class NotAPing implements an.awesome.pipelinr.Command<an.awesome.pipelinr.Voidy> {
    static class Handler implements an.awesome.pipelinr.Command.Handler<NotAPing, an.awesome.pipelinr.Voidy> {

      private Collection<Command> handled = new ArrayList<>();

      @Override
      public an.awesome.pipelinr.Voidy handle(NotAPing command) {
        handled.add(command);
        return new Voidy();
      }
    }
  }
}
