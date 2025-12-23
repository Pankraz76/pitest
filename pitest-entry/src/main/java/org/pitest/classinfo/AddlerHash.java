package org.pitest.classinfo;

import java.util.zip.Adler32;

public class AddlerHash implements HashFunction {

  @Override
  public long hash(final byte[] value) {
    final var adler = new Adler32();
    adler.update(value, 0, value.length);
    return adler.getValue();
  }

}
