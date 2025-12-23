package com.example.java7;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

public class HasIfOnAStringEqualityTesteeTest {

  private final HasIfOnAStringEqualityTestee testee = new HasIfOnAStringEqualityTestee();

  @Test
  public void ifStringShouldReturnA() throws Exception {
    // given
    final var input = "a";

    // when
    final var result = testee.ifString(input);

    // then
    assertThat(result).isEqualTo("A");
  }

  @Test
  public void ifStringShouldReturnB() throws Exception {
    // given
    final var input = "b";

    // when
    final var result = testee.ifString(input);

    // then
    assertThat(result).isEqualTo("B");
  }

  @Test
  public void ifStringShouldReturnC() throws Exception {
    // given
    final var input = "c";

    // when
    final var result = testee.ifString(input);

    // then
    assertThat(result).isEqualTo("C");
  }

  @Test(expected = IllegalArgumentException.class)
  public void ifStringShouldThrowIllegalArgumentException() throws Exception {
    // given
    final var input = "x";

    // when
    testee.ifString(input);

    // then
    // exception
  }
}
