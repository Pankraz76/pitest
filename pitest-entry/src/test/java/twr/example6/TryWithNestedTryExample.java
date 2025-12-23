package twr.example6;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * @author Artem Khvastunov
 * Tests use stored compiled binaries of this class. Source here for reference only.
 */
public class TryWithNestedTryExample {

    public static void main(String[] args) throws IOException {
        try (var baos = new ByteArrayOutputStream()) {
            try (var bos = new BufferedOutputStream(baos)) {
                bos.flush();
            }
        }
    }
}
