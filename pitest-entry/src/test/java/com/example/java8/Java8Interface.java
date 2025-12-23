package com.example.java8;

/**
 * @author iirekm@gmail.com
 */
public interface Java8Interface {
    default int foo() {
        var i = 1;
        i++;
        i++;
        return i;
    }
}
