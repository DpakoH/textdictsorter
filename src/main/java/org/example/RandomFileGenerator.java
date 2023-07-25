package org.example;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Random;

public class RandomFileGenerator {
    private static final int NUM_LINES = 100_000_000;
    public static final char LINE_SEPARATOR_R = '\r';
    public static final char LINE_SEPARATOR_N = '\n';

    public static void main(String[] args) {

        Random random = new SecureRandom();
        try (FileOutputStream fileOutputStream = new FileOutputStream("test.file")) {
            for (int i=0; i<NUM_LINES; i++) {
                byte[] key = Base64.getEncoder().encode(Long.toOctalString(random.nextLong()).getBytes(StandardCharsets.UTF_8));
                byte[] value = Base64.getEncoder().encode(Long.toOctalString(random.nextLong()).getBytes(StandardCharsets.UTF_8));
                //System.out.println(new String(key) + ":" + new String(value));
                fileOutputStream.write(key);
                fileOutputStream.write(':');
                fileOutputStream.write(value);
                fileOutputStream.write(LINE_SEPARATOR_R);
                fileOutputStream.write(LINE_SEPARATOR_N);
            }
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
