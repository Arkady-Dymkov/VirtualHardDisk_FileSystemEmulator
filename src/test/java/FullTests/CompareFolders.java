/*
 * Copyright (c) 2022. Arkady Dymkov townhospis<townhospis@gmail.com>
 */

package FullTests;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

public class CompareFolders {
    private static void verifyDirsAreEqual(Path one, Path other) throws IOException {
        Files.walkFileTree(one, new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult visitFile(Path file,
                                             BasicFileAttributes attrs)
                    throws IOException {
                FileVisitResult result = super.visitFile(file, attrs);

                // get the relative file name from path "one"
                Path relativize = one.relativize(file);
                // construct the path for the counterpart file in "other"
                Path fileInOther = other.resolve(relativize);

                byte[] otherBytes = Files.readAllBytes(fileInOther);
                byte[] theseBytes = Files.readAllBytes(file);
                if (!Arrays.equals(otherBytes, theseBytes)) {
                    System.out.println(file + " is not equal to " + fileInOther);
                }
                return result;
            }
        });
    }

    @Test
    public void test() {
        assertDoesNotThrow(() -> verifyDirsAreEqual(
                Paths.get("../VirtualHardDisk"),
                Paths.get("../VirtualHardDiskCopy/Another_Copy")));
    }
}
