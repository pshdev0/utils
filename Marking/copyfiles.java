/*
 * run this command in the termina:
 * 
 *      find ./ -type f -exec grep -i -l "var tX = (x" {} \; >> copycats.txt
 * 
 * to identify copy-cats against external sources / tutorial / etc.
 * 
 * 
 * then run this java program to copy the files listed in copycats.txt to ./copycats folder
 * 
 * you can then open ./copycats folder in vs code, examine, and flag as plagiarism as necessary
 * 
 */

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;

import java.util.List;

class CopyFiles {

    public static void main(String[] args) {

        System.out.println("Listing files:\n");

        Path filePath = Paths.get("copycats.txt");
        Charset charset = StandardCharsets.UTF_8;

        try {
            List<String> lines = Files.readAllLines(filePath, charset);

            for (String line : lines) {
                String path = line;
                String name = line.replace(".", "_");
                name = name.replace("/", "_");
                name = name.replace(" ", "_");
                name += ".js";

                System.out.println(path + " -> " + name);

                Files.copy(Path.of(line), Path.of("./copycats/" + name));
            }
        } catch (IOException ex) {
            System.out.format("I/O error: %s%n", ex);
        }
    }
}
