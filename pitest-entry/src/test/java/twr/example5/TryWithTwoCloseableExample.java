package twr.example5;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author Artem Khvastunov
 * Tests use stored compiled binaries of this class. Source here for reference only.
 */
public class TryWithTwoCloseableExample {

    public static void main(String[] args) throws IOException {
        try (var baos /* 1 */ = new ByteArrayOutputStream(); // L22 // null /* 2 */ // L23
             var bos /* 3 */ = new BufferedOutputStream(baos)) { // L14 // null /* 4 */ // L24
            bos.flush(); // L3
        } // L4
    }
}
