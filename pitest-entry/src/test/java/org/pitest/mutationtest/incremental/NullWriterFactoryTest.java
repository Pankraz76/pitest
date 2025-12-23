package org.pitest.mutationtest.incremental;

import org.junit.Test;

public class NullWriterFactoryTest {

  @Test
  public void shouldCreateAUsableWriter() {
    final var testee = new NullWriterFactory();
    testee.create().println("foo");
    // pass
  }

}
