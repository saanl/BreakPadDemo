//
// Created by willl on 8/1/2019.
//

#include "com_ly_jniandbreaddemo_MainActivity.h"
#include "client/linux/handler/exception_handler.h"
#include "client/linux/handler/exception_handler.h"
#include <android/log.h>

bool dumpCallback(const google_breakpad::MinidumpDescriptor &descriptor,
                  void *context,
                  bool succeeded) {

    /* Allow system to log the native stack trace. */
    __android_log_print(ANDROID_LOG_INFO, "AppCenter NDK Crash: ",
                        "Wrote breakpad minidump at %s succeeded=%d\n", descriptor.path(),
                        succeeded);
    return false;
}

// libc: Fatal signal 5 (SIGTRAP), code 1 in tid 19362 没有写return

JNIEXPORT void JNICALL Java_com_ly_jniandbreaddemo_MainActivity_setupNativeCrashesListener
  (JNIEnv *env, jobject, jstring path){
        const char *dumpPath = (char *) env->GetStringUTFChars(path, NULL);
        google_breakpad::MinidumpDescriptor descriptor(dumpPath);
        new google_breakpad::ExceptionHandler(descriptor, NULL, dumpCallback, NULL, true, -1);
        env->ReleaseStringUTFChars(path, dumpPath);
};

int Crash() {
    *( ( char* ) NULL ) = 0;
    int i =  1 / 0;
    return i;
}

JNIEXPORT void JNICALL Java_com_ly_jniandbreaddemo_MainActivity_nativeCrash
        (JNIEnv *, jobject){
    int k = Crash();
}




