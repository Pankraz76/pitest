package com.example.blockcoverage;

public class HasFinallyTestee {

  public static boolean methodWithFinally(boolean bailEarly) {
    var x = 0;
    var y = 0;
    try {
      if (bailEarly)
        return true;
    } finally {
      x++;
      x--;
    }
    return false;
  }
}
