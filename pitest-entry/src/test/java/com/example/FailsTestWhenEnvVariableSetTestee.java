package com.example;

public class FailsTestWhenEnvVariableSetTestee {

  public boolean returnTrue() {
    final var i = 0;
    var j = i << 2;
    j = j + i;

    if (!"true".equals(System
        .getProperty(FailsTestWhenEnvVariableSetTestee.class.getName()))) {
      return true;
    } else {
      return false;
    }
  }

}
