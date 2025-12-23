package com.example.java7;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class HasIfOnAStringEqualityInsideSwitchTesteeTest {

  private final HasIfOnAStringEqualityInsideSwitchTestee testee = new HasIfOnAStringEqualityInsideSwitchTestee();

  @Test
  public void stringSwitchShouldReturnA() throws Exception {
    // given
    final var input = "a";
    final var input2 = "a";

    // when
    final var result = testee.ifStringInsideSwitch(input, input2);

    // then
    assertThat(result).isEqualTo("A");
  }

  @Test
  public void stringSwitchShouldReturnAX() throws Exception {
    // given
    final var input = "a";
    final var input2 = "other";

    // when
    final var result = testee.ifStringInsideSwitch(input, input2);

    // then
    assertThat(result).isEqualTo("AX");
  }

  @Test
  public void stringSwitchShouldReturnB() throws Exception {
    // given
    final var input = "b";
    final var input2 = "x";

    // when
    final var result = testee.ifStringInsideSwitch(input, input2);

    // then
    assertThat(result).isEqualTo("B");
  }

  @Test
  public void stringSwitchShouldReturnC() throws Exception {
    // given
    final var input = "c";
    final var input2 = "x";

    // when
    final var result = testee.ifStringInsideSwitch(input, input2);

    // then
    assertThat(result).isEqualTo("C");
  }

  @Test(expected = IllegalArgumentException.class)
  public void stringSwitchShouldThrowIllegalArgumentException()
      throws Exception {
    // given
    final var input = "x";
    final var input2 = "x";

    // when
    testee.ifStringInsideSwitch(input, input2);

    // then
    // exception
  }
}
