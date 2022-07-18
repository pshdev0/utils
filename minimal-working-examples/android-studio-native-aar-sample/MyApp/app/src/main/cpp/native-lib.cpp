#include <jni.h>
#include <string>

#include "MyLibraryCPP.h"

extern "C" JNIEXPORT jstring JNICALL
Java_com_test_myapp_MainActivity_managedFoo(
        JNIEnv* env,
        jobject /* this */) {
    MyLibraryCPP x;

    return env->NewStringUTF(x.foo().c_str());
}