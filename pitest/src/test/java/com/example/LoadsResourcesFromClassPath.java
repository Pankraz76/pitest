package com.example;

import java.io.InputStream;

import org.pitest.util.IsolationUtils;

public class LoadsResourcesFromClassPath {

  public static boolean loadResource() {
    final var stream = IsolationUtils.getContextClassLoader()
        .getResourceAsStream(
            "resource folder with spaces/text in folder with spaces.txt");
    final var result = stream != null; // store result to nudge compiler
    // towards single IRETURN
    return result;
  }

}
