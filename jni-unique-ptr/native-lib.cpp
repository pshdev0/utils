static JavaVM* globalVM;
static jclass java_util_ArrayList;
static jmethodID java_util_ArrayList_;

jmethodID java_util_ArrayList_size;
jmethodID java_util_ArrayList_get;
jmethodID java_util_ArrayList_add;

JNIEXPORT jint JNICALL JNI_OnLoad (JavaVM* vm, void* reserved) {
    globalVM = vm;
    JNIEnv* env;

    if (vm->GetEnv(reinterpret_cast<void**>(&env), JNI_VERSION_1_6) != JNI_OK) return JNI_ERR;

    java_util_ArrayList = static_cast<jclass>(env->NewGlobalRef(env->FindClass("java/util/ArrayList")));
    java_util_ArrayList_ = env->GetMethodID(java_util_ArrayList, "<init>", "(I)V");
    java_util_ArrayList_size = env->GetMethodID (java_util_ArrayList, "size", "()I");
    java_util_ArrayList_get = env->GetMethodID(java_util_ArrayList, "get", "(I)Ljava/lang/Object;");
    java_util_ArrayList_add = env->GetMethodID(java_util_ArrayList, "add", "(Ljava/lang/Object;)Z");

    return JNI_VERSION_1_6;
}

class JNIConverterJStringToConstCharPtr {
private:
    JNIEnv* env;
    jstring *jStr;

public:
    JNIConverterJStringToConstCharPtr(JNIEnv* env, jstring *str) {
        this->env = env;
        this->jStr = str;
        this->result = env->GetStringUTFChars(*jStr, nullptr);
    }

    ~JNIConverterJStringToConstCharPtr() {
        env->ReleaseStringUTFChars(*jStr, result);
    }

    const char * result = nullptr;
};

class JNIConverterListOfStringToVectorOfString {
public:
    JNIConverterListOfStringToVectorOfString(JNIEnv* env, jobject *arrayList) {
        jint len = env->CallIntMethod(*arrayList, java_util_ArrayList_size);
        result.reserve(len);
        for (jint i=0; i<len; i++) {
            auto element = static_cast<jstring>(env->CallObjectMethod(*arrayList, java_util_ArrayList_get, i));
            const char* pchars = env->GetStringUTFChars(element, nullptr);
            result.emplace_back(pchars);
            env->ReleaseStringUTFChars(element, pchars);
            env->DeleteLocalRef(element);
        }
    }

    vector<string> result;
};

class JNIConverterArrayListOfStringToCharPtrPtr {
private:
    unique_ptr<JNIConverterListOfStringToVectorOfString> ptr;

public:
    JNIConverterArrayListOfStringToCharPtrPtr(JNIEnv* env, jobject *arrayList) {
        // we don't want this deleting after construction scope
        ptr = make_unique<JNIConverterListOfStringToVectorOfString>(env, arrayList);
        result = new char*[ptr->result.size()+1];
        for (int c1 = 0; c1 < ptr->result.size(); c1++) result[c1] = const_cast<char*>(ptr->result[c1].c_str());
        result[ptr->result.size()] = nullptr; // denotes end of list
    }

    ~JNIConverterArrayListOfStringToCharPtrPtr() {
        delete [] result;
    }

    char** result = nullptr;
};
