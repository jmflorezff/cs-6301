package team3;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Main {
    /**
     * Main method
     *
     * @param args hi
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        final ExecutorService executor = Executors.newFixedThreadPool(4);

        Files.walkFileTree(Paths.get(args[0]), new FileVisitor<Path>() {
            @Override
            public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(final Path file, BasicFileAttributes attrs) throws IOException {
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            UnusedVariableVisitor.processFile(file.toString());
                        } catch (IOException e) {
                            System.out.println("Error with file " + file.toString());
                            e.printStackTrace();
                        }
                    }
                });
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                return FileVisitResult.CONTINUE;
            }
        });

        executor.shutdown();
    }


}
