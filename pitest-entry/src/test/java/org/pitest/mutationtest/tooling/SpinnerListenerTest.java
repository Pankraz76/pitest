package org.pitest.mutationtest.tooling;

import static org.junit.Assert.assertEquals;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.Test;

public class SpinnerListenerTest {

  @Test
  public void shouldPrintSpinnerSequence() {
    final var bos = new ByteArrayOutputStream();
    final var out = new PrintStream(bos);
    final var testee = new SpinnerListener(out);
    testee.handleMutationResult(null);
    testee.handleMutationResult(null);
    assertEquals("\u0008/\u0008-", new String(bos.toByteArray()));
  }

}
