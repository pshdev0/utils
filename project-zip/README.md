# project-zip

🚧 Under Construction 🚧

Flexible Project File Management

An alternative to class Serialisation and its many caveats, ProjectZip provides a simple and flexible way to store project data:

* Easily create a new project and automatically export Field members and Containers to JSON
* Multiple export scopes supported using Reflection Annotations
* Store JSON where it makes sense over binaries
* Store binaries where it makes sense over JSON
* Automatically zips to a single project file
* Fully editable using native file system for greater flexibility

# Example

```
class MyProjectData {

    @ProjectZipExport(id = 0)
    double [] myArray1, myArray2;

    @ProjectZipExport(id = 0)
    public String name;

    @ProjectZipExport(id = 1)
    public int width, height;

    public ArrayList<MyObject> list;

    public void deflate(String zipFileName) throws IOException {
        ProjectZip.newProject();
        ProjectZip.storeJSONObject(0, "fields1.json", this); // stores only those fields with id(0) annotation 
        ProjectZip.storeJSONObject(1, "fields2.json", this); // stores only those fields with id(1) annotation
        ProjectZip.storeJSONArray("list.json", list, MyObject::getJSON);
        ProjectZip.zip(zipFileName);
    }

    public void inflate(String inZipFile) throws IOException {
        ProjectZip.newProject(inZipFile);
        ProjectZip.restoreJSONObject(0, "fields1.json", this); // restore id(0) fields
        ProjectZip.restoreJSONObject(1, "fields2.json", this); // restore id(1) fields
        ProjectZip.restoreJSONArray("list.json", list, MyObject::new);
    }
}

public class MyObject {
    public Image image; // e.g. a JavaFX Image

    MyObject(Image image) {
        this.image = image;
    }
    
    MyObject(JSONObject j) {
        image = ProjectZip.readPNG(j.getString("file"));
    }
    
    JSONObject getJSON() throws IOException {
        JSONObject j = new JSONObject();
        j.put("file", ProjectZip.writePNG(image));
        return j;
    }
}
```

# TODO

* Add zip encryption, where required; also encrypt temp folder where encryption is used
* Migrate storeJSONArray to storeJSONObject for automatic storage
* Add a new Annotation parameter e.g. `@ProjectZipExport(id = 0, binary = true)` for automatically exporting binaries
* Add a custom restore lambda for custom types

# Class Serialisation Caveats I've come to realise

* Most data structures never remain the same, when developing we need some flexibility and serialisation does not like that.
* Changing variable names requires a project load, bespoke copy, project save & remove old fields.
* Changing class structure, and class and package names is arguably worse !
* Version incompatibility problems.
