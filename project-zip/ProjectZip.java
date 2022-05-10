import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.imageio.ImageIO;
import java.io.*;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.List;

interface ProjectZipLambda<T, W> {
    W run(T x) throws IOException;
}

interface ProjectZipLambda2<T> {
    T run(JSONObject x) throws IOException;
}

@Retention(RetentionPolicy.RUNTIME)
@interface ProjectZipExport {
    int id();
}

public class ProjectZip {

    static private Path root;
    static long counter = 0;

    static public Path getRoot() {
        return root;
    }

    static public void newProject(String inZipFile) throws IOException {
        // create a new temp project directory & unzip the project into it

        counter = 0;
        root = Paths.get(System.getProperty("java.io.tmpdir"), "pxctemp");
        deleteFolder(new File(root.toString()));
        unzip(inZipFile, System.getProperty("java.io.tmpdir"));
    }

    static public void newProject() throws IOException {
        // start building a new temp project directory

        counter = 0;
        root = Paths.get(System.getProperty("java.io.tmpdir"), "pxctemp");
        deleteFolder(new File(root.toString()));
        Files.createDirectories(root);
    }

    static public JSONObject readJSONFile(String fileName) throws IOException {
        // returns the JSONObject of the given file name
        return new JSONObject(Files.readString(Paths.get(root.toString(), fileName)));
    }

    static public JSONArray readJSONArray(String fileName) throws IOException {
        // returns the JSONArray of the given file name
        return new JSONArray(Files.readString(Paths.get(root.toString(), fileName)));
    }

    static long nextCounter() {
        // used for binary file storage
        return counter++;
    }

    static public void writeString(String fileName, String writeThis) throws IOException {
        // writes writeThis to fileName in path
        Files.writeString(Paths.get(root.toString(), fileName), writeThis);
    }

    static public String writePNG(Image img) throws IOException {
        // writes a png binary and returns the file name
        String fileName = "file_" + ProjectZip.nextCounter() + ".png";
        Path png = Paths.get(root.toString(), fileName);
        File out = new File(png.toString());
        ImageIO.write(SwingFXUtils.fromFXImage(img, null), "png", out);
        return fileName;
    }

    static public Image readPNG(String fileName) throws IOException {
        // reads a png binary
        Path path = Paths.get(root.toString(), fileName);
        return SwingFXUtils.toFXImage(ImageIO.read(new File(path.toString())), null);
    }

    static public String writeBytes(byte [] bytes, String extension) throws IOException {
        // writes a byte array and returns the file name
        String fileName = "file_" + ProjectZip.nextCounter() + "." + extension;
        Path out = Paths.get(root.toString(), fileName);
        Files.write(out, bytes, StandardOpenOption.CREATE);
        return fileName;
    }

    static public byte [] readBytes(String fileName) throws IOException {
        // reads the byte array from file name
        return Files.readAllBytes(Paths.get(root.toString(), fileName));
    }

    static public String base64Of(String input) {
        // helper function encodes base 64
        return Base64.getEncoder().encodeToString(input.getBytes(StandardCharsets.UTF_8));
    }

    static public String fromBase64(String base64) {
        // helper function decode base 64
        return new String(Base64.getDecoder().decode(base64));
    }

    private static void deleteFolder(File folder) {
        // helper function to delete a folder
        File [] files = folder.listFiles();
        if(files != null) for(File f: files) if(f.isDirectory()) deleteFolder(f); else f.delete();
        folder.delete();
    }

    private static void unzip(String zipFile, String unzipDir) throws IOException {
        // unzips zipFile to unzipDir using the local OS - may need changing slightly depending on native OS
        Runtime rt = Runtime.getRuntime();
        Process pr = rt.exec(new String [] { "unzip", zipFile, "-d", unzipDir});

        try {
            pr.waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    static public void zip(String outZipFile) throws IOException {
        // zips the project to outZipFile
        zip(ProjectZip.getRoot().toString(), outZipFile);
    }

    private static void zip(String sourceFile, String zipFile) throws IOException {
        // zips the sourceFile directory to zipFile - may need changing slightly depending on native OS
        try {
            Runtime rt = Runtime.getRuntime();
            rt.exec(new String [] {"zip", zipFile, "pxctemp", "-r" }, null, new File(sourceFile + "/../")).waitFor();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    static public <T, W> void storeJSONArray(String jsonFileName, ArrayList<T> list, ProjectZipLambda<T, W> func) throws IOException {
        // template function to store the JSON array
        JSONArray array = new JSONArray();
        for(T x : list) array.put(func.run(x));
        ProjectZip.writeString(jsonFileName, array.toString());
    }

    public static <T> void restoreJSONArray(String jsonFileName, Collection<T> list, ProjectZipLambda2<T> func) throws IOException {
        // template function to restore the JSON array
        JSONArray array = ProjectZip.readJSONArray(jsonFileName);
        list.clear();
        for(int c1 = 0; c1 < array.length(); c1++) list.add(func.run(array.getJSONObject(c1)));
    }

    public static void storeJSONObject(int id, String jsonFileName, Object obj) throws IOException {
        // automatically stores all fields with attribute ID to JSON file jsonFileName
        List<Field> fields = FieldUtils.getFieldsListWithAnnotation(obj.getClass(), ProjectZipExport.class);
        JSONObject j = new JSONObject();

        try {
            for(Field f : fields) if(f.getAnnotationsByType(ProjectZipExport.class)[0].id() == id) j.put(f.getName(), f.get(obj));
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            System.out.println("Problem getting Field");
        }

        ProjectZip.writeString(jsonFileName, j.toString());
    }

    public static void restoreFromJSONObject(int id, String jsonFileName, Object obj) throws IOException {
        // automatically restores all fields with attribute ID from JSON file jsonFileName
        List<Field> fields = FieldUtils.getFieldsListWithAnnotation(obj.getClass(), ProjectZipExport.class);
        JSONObject j = ProjectZip.readJSONFile(jsonFileName);

        try {
            for(Field f : fields) if(f.getAnnotationsByType(ProjectZipExport.class)[0].id() == id) {
                if(f.getType() == double[].class) f.set(obj, toDoubleArray(j.getJSONArray(f.getName())));
                else if(f.getType() == String.class) f.set(obj, j.getString(f.getName()));
                else if(f.getType() == int.class) f.set(obj, j.getInt(f.getName()));
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            System.out.println("Problem getting Field");
        }
    }

    // todo - can these be made generic:
    static double [] toDoubleArray(JSONArray array) {
        // helper function to convert arrays
        double [] list = new double[array.length()];
        for(int c1 = 0; c1 < array.length(); c1++) list[c1] = array.getDouble(c1);
        return list;
    }

    static float [] toFloatArray(JSONArray array) {
        float [] list = new float[array.length()];
        for(int c1 = 0; c1 < array.length(); c1++) list[c1] = array.getFloat(c1);
        return list;
    }

    static int [] toIntArray(JSONArray array) {
        int [] list = new int[array.length()];
        for(int c1 = 0; c1 < array.length(); c1++) list[c1] = array.getInt(c1);
        return list;
    }
}
