package an.awesome.pipelinr;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class PreconditionsTest {

  @Test
  void throwsIfArgumentIsNull() {
    Throwable e =
        assertThrows(
            IllegalArgumentException.class,
            () -> {
              an.awesome.pipelinr.Preconditions.checkArgument(null, "Shit has happened");
            });

    assertThat(e).hasMessage("Shit has happened");
  }

  @Test
  void throwsIfArgumentIsFalse() {
    Throwable e =
        assertThrows(
            IllegalArgumentException.class,
            () -> {
              an.awesome.pipelinr.Preconditions.checkArgument(false, "Shit has happened");
            });

    assertThat(e).hasMessage("Shit has happened");
  }

  @Test
  void returnsAnArgumentIfTheArgumentIsAValidObject() {
    // given
    Object checkedObject = new Object();

    // when
    Object returnedObject = an.awesome.pipelinr.Preconditions.checkArgument(checkedObject, "OK");

    // then
    assertThat(returnedObject).isEqualTo(checkedObject);
  }

  @Test
  void passesIfAnArgumentIsTruthy() {
    assertDoesNotThrow(() -> an.awesome.pipelinr.Preconditions.checkArgument(true, "OK"));
  }
}
