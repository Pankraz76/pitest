package org.pitest.mutationtest.incremental;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;

import org.hamcrest.Matcher;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.pitest.util.PitError;

public class FileWriterFactoryTest {

  @Rule
  public TemporaryFolder   testFolder = new TemporaryFolder();

  @Rule
  public ExpectedException thrown     = ExpectedException.none();

  @Test
  public void writeToFile() throws IOException {
    final var file = this.testFolder.newFile();
    final var writerFactory = new FileWriterFactory(file);
    final var writer = writerFactory.create();
    writer.write("test");
    writerFactory.close();

    final String content = Files.readString(file.toPath());
    assertThat(content, equalTo("test"));
  }

  @Test
  public void writeToFolder() throws IOException {
    this.thrown.expect(PitError.class);
    final Matcher<? extends Throwable> causedBy = instanceOf(IOException.class);
    this.thrown.expectCause(causedBy);

    final var folder = this.testFolder.newFolder();
    final var writerFactory = new FileWriterFactory(folder);
    writerFactory.create();
  }

  @Test
  public void writeToFileWithinFolder() throws IOException {
    final var folder = this.testFolder.newFolder();
    final var file = new File(folder, "subfolder/file");
    final var writerFactory = new FileWriterFactory(file);
    final var writer = writerFactory.create();
    writer.write("test");
    writerFactory.close();

    final String content = Files.readString(file.toPath());
    assertThat(content, equalTo("test"));
  }

}
