/*
 * Copyright 2000-2015 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.pitest.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.jar.Attributes;
import java.util.jar.JarInputStream;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;
import java.util.stream.Collectors;

/**
 * Ugly static methods to create and parse classpath manifests
 */
public class ManifestUtils {

  public static final String CLASSPATH_JAR_FILE_PREFIX = "pitest-classpath-jar-file-";

  // Method based on
  // https://github.com/JetBrains/intellij-community/blob/master/java/java-runtime/src/com/intellij/rt/execution/testFrameworks/ForkedByModuleSplitter.java
  // JetBrains copyright notice and licence retained above.
  public static File createClasspathJarFile(String classpath)
          throws IOException {
    final var manifest = new Manifest();
    final var attributes = manifest.getMainAttributes();
    attributes.put(Attributes.Name.MANIFEST_VERSION, "1.0");

    var classpathForManifest = new StringBuilder();
    var idx = 0;
    var endIdx = 0;
    while (endIdx >= 0) {
      endIdx = classpath.indexOf(File.pathSeparator, idx);
      String path = endIdx < 0 ? classpath.substring(idx)
              : classpath.substring(idx, endIdx);
      if (classpathForManifest.length() > 0) {
        classpathForManifest.append(" ");
      }

      classpathForManifest.append(new File(path).toURI().toURL());
      idx = endIdx + File.pathSeparator.length();
    }
    attributes.put(Attributes.Name.CLASS_PATH, classpathForManifest.toString());

    File jarFile = File.createTempFile(CLASSPATH_JAR_FILE_PREFIX, ".jar");
    try (var out = new BufferedOutputStream(new FileOutputStream(jarFile));
         var jarPlugin = new JarOutputStream(out, manifest)
    )  {
      jarFile.deleteOnExit();
    }

    return jarFile;
  }

  public static Collection<File> readClasspathManifest(File file) {
    try (var fis = new FileInputStream(file);
         var jarStream = new JarInputStream(fis)) {
      var mf = jarStream.getManifest();
      var att = mf.getMainAttributes();
      var cp = att.getValue(Attributes.Name.CLASS_PATH);
      var parts = cp.split("file:");
      return Arrays.stream(parts)
              .filter(part -> !part.isEmpty())
              .map(part -> new File(part.trim()))
              .collect(Collectors.toList());
    } catch (IOException ex) {
      throw new RuntimeException("Could not read classpath jar manifest", ex);
    }
  }
}
