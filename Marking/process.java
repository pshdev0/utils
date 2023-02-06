import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.Path;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

import java.nio.file.StandardOpenOption;

class Process {

    public static int fileOfInterest(Path path) {

        if (path.endsWith("bulletSystem.js"))
            return 1;
        if (path.endsWith("spaceship.js"))
            return 2;
        if (path.endsWith("sketch.js"))
            return 3;    
        if (path.endsWith("physics.js"))
            return 4;

        return 0;
    }

    public static String findFunction(String name, String code) {

        // \s*edges\s*\(.*\)\s*\{([^\{\}]*|\{([^\{\}]*|\{([^\{\}]*|\{([^\{\}]*|\{([^\{\}]*|\{[^\{\}]*\})*\})*\})*\})*\})*\}

        String patternString = "\\s*" + name
                + "\\s*\\(.*\\)\\s*\\{([^\\{\\}]*|\\{([^\\{\\}]*|\\{([^\\{\\}]*|\\{([^\\{\\}]*|\\{([^\\{\\}]*|\\{[^\\{\\}]*\\})*\\})*\\})*\\})*\\})*\\}";
        Pattern pattern = Pattern.compile(patternString, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(code);
        boolean matchFound = matcher.find();

        String rtn = null;
        
        while(matchFound) {

            if(rtn == null) rtn = "";
            rtn += matcher.group(0) + "\n\n";
            matchFound = matcher.find();
        }

        return rtn;
    }

    public static void appendToFile(String root, String code) {

        if (root.endsWith("/"))
            return;

        try {
            System.out.println(">> " + root);
            code += "\n\n\n   //-------------------------------------------------------------------------\n\n";
            Files.write(Path.of(root), code.getBytes(), StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            System.out.println("Written to: " + root);
        } catch (Exception e) {
            System.out.println("Error writing to: " + root);
            System.out.println(e.getMessage());
        }

        System.out.println("");
    }

    static int nDeleted;

    public static void main(String[] args) {

        System.out.println("Searching for files:\n");

        try {

            nDeleted = 0;

            // delete all extract.js files
            Files.find(Paths.get("./"),
                    Integer.MAX_VALUE,
                    (filePath, fileAttr) -> fileAttr.isRegularFile())
                    .forEach(path -> {

                        if (path.endsWith("extract.js")) {
                            // delete the file

                            try {
                                Files.delete(path);
                                nDeleted++;
                            } catch (Exception e) {
                                System.out.println("Could not delete: " + path);
                            }
                        }
                    });

            System.out.println("Deleted " + nDeleted + " extract.js files.");

            // create new ones
            Files.find(Paths.get("./"),
                    Integer.MAX_VALUE,
                    (filePath, fileAttr) -> fileAttr.isRegularFile())
                    .forEach(path -> {
                        String fileContents = "";
                        int type = Process.fileOfInterest(path);
                        String code = "";
                        String root = "";

                        if (type == 0)
                            return;

                        try {
                            fileContents = Files.readString(path);
                        } catch (Exception e) {
                            System.out.println("Error reading " + path);
                        }

                        switch (type) {
                            case 1: // bulletSystem.js

                                root = path.toString().replace("bulletSystem.js", "") + "extract.js";
                                code = findFunction("edges", fileContents);

                                if (code == null)
                                    break;

                                appendToFile(root, code);

                                break;

                            case 2: // spaceship.js

                                root = path.toString().replace("spaceship.js", "") + "extract.js";

                                code = findFunction("interaction", fileContents);
                                if (code != null) appendToFile(root, code);

                                code = findFunction("setNearEarth", fileContents);
                                if (code != null) appendToFile(root, code);

                                break;

                            case 3: // sketch.js

                                root = path.toString().replace("sketch.js", "") + "extract.js";

                                code = findFunction("isInside", fileContents);
                                if (code != null) appendToFile(root, code);

                                code = findFunction("removeFromWorld", fileContents);
                                if (code != null) appendToFile(root, code);

                                break;

                            case 4: // physics.js

                                root = path.toString().replace("physics.js", "") + "extract.js";

                                code = findFunction("drawBirds", fileContents);
                                if (code != null) appendToFile(root, code);

                                code = findFunction("setupSlingshot", fileContents);
                                if (code != null) appendToFile(root, code);

                            break;
                        }
                    });
        } catch (Exception e) {
            System.out.println("Error");
            return;
        }
    }
}
