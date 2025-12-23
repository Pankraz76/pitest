package com.example.coverage.execute.samples.simple;

import org.junit.Test;

public class Tests {

  @Test
  public void testFoo() {
    final var testee = new Testee();
    testee.foo();

  }

  @Test
  public void testFoo2() {
    final var testee2 = new Testee2();
    testee2.foo();
  }

  @Test
  public void testFoo3() {
    final var testee2 = new TesteeWithMultipleLines();
    testee2.foo(1);
  }
}