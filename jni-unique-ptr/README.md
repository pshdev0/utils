# jni-unique-ptr
Helper classes for Java / Kotlin / C JNI interop.

Use smart pointers to automatically manage Java / C++ data type and container conversions. 

The `unique_ptr` will automatically destruct on scope exit and perform all necessary JNI cleanup.

*Example 1*

```
extern "C" JNIEXPORT void JNICALL
Java_com_my_project_myJNIFunction(JNIEnv* env, jobject, jstring myString) {
    auto chars = make_unique<JNIConverterJStringToConstCharPtr>(env, &myString);
    char * x = chars->result;
}
```

*Example 2*

```
extern "C" JNIEXPORT void JNICALL
Java_com_my_project_myJNIFunction(JNIEnv* env, jobject, jstring name, jobject arrayList) {
    auto array = make_unique<JNIConverterArrayListOfStringToCharPtrPtr>(env, &arrayList);
    char ** y = array->result;
    
    int counter = 0;
    while(array->result[counter] != nullptr) cout << array->result[c1++] << endl;
}
```
