#include "com_xiuyukeji_ndk_config_HelloJni.h"

extern "C" {

    JNIEXPORT jstring JNICALL Java_com_xiuyukeji_ndk_1config_HelloJni_hello(JNIEnv * env, jclass jc){
        return (env)->NewStringUTF("Hello jni");
    }

}