package com.example.blockcoverage;

public class HasExceptionsTestee {
  public static void foo(){
    String x = null;
    var y  =x.length();
    y++;
  }
}
