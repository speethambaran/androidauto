#include <jni.h>
#include <string>
#include <iostream>
#include <android/log.h>
#include <threads.h>
#include <unistd.h>


#include "my_test_conn.cpp"
#include "wiringPi.h"
#include "wiringSerial.h"


//these structure is declared for threading purpose..

//these structure is declared for threading purpose..close

extern "C" JNIEXPORT jstring JNICALL
Java_com_infolitz_mycarspeed_shared_CommunicateWithC_useWiringLib(JNIEnv *env, jobject) {
    std::string hello = "Hello from C";

    int serial_port;
    int charCount;
    uint8_t rebitSize = 0;

    char my_string[256] = "";
    jstring result;

    __android_log_print(ANDROID_LOG_ERROR, "at", "%s", "serial_port");
    system("su");
//    system("chmod 777 /dev/ttyS0");
//    system("cat /dev/ttyS0");
//    system("su chmod 777 /dev/ttyS0");
    system("su chmod 777 /dev/ttyS0");
    system("su chmod 777 /dev/gpiomem");
    system("su chmod 777 /dev/mem");
    if ((serial_port = serialOpen1("/dev/ttyS0", 9600)) <
        0)    /* open serial port--"/dev/ttyS0",9600 --("/sys/class/tty/ttyS0", 115200) */
    {
        __android_log_print(ANDROID_LOG_ERROR, "Unable to open serial device: ::", "%s",
                            strerror(errno));
        return (env)->NewStringUTF("1");
    }
    __android_log_print(ANDROID_LOG_ERROR, "serial port value: ", "%d", serial_port);
    /*if (wiringPiSetup () == -1)					*//* initializes wiringPi setup *//*
    {
        __android_log_print(ANDROID_LOG_ERROR, "Unable to start wiringPi:\n", "%s", strerror (errno));
        return (env)->NewStringUTF("2");
    }*/


    while (1) {

        if (serialDataAvail(serial_port)) {
            __android_log_print(ANDROID_LOG_ERROR, "serialDataAvail ::", "%s", "inside");
            /* if(serialGetstring( my_string,serial_port) == 0){

                 result = (env)->NewStringUTF("wrong format");
                 return result;
             }else {*/
            serialGetstring1(my_string, serial_port, &rebitSize);

//                charCount = readline2( serial_port,my_string);
//                __android_log_print(ANDROID_LOG_ERROR, "char count::", "%d", charCount);
            printf("%s \n", my_string);
            fflush(stdout);
            __android_log_print(ANDROID_LOG_ERROR, "string isssss ::", "%s", my_string);
            __android_log_print(ANDROID_LOG_ERROR, "bit size received isssss ::", "%d", rebitSize);

            result = (env)->NewStringUTF(my_string);
            return result;
            // }
        }
    }

}


JNIEnv *env1;

struct ReadThreadParams {
    JNIEnv *env;
};

extern "C" JNIEXPORT jstring JNICALL
//extern "C" JNIEXPORT jobject JNICALL
Java_com_infolitz_mycarspeed_shared_CommunicateWithC_useShortCommLib(JNIEnv *env,
                                                                     jobject thiz/*,int flagPort*/) {
    int serial_port;
    uint8_t rebitSize = 0;
    //...........
    static jint ret = -1;
    jclass wrapperclass;
    jmethodID constructor;
    jobject wrapperObject;
    jmethodID getPortt;
    jclass cls;
    jmethodID callToSetVal;
    //.........


    char my_string[256] = "";//string from uart collected
    jstring result;
    char mystringPort[64] = ""; //will be having or required string and the port value



//    thrd_create(&call_read_thread_id, thread_function, &readParams);// calling thread function
//    thrd_create(&call_read_thread_id, thread_function, &readParams);// calling thread function
    //////////////////////////////////////////////////////////////////////////

    ///safe code
    /*  // create wrapper object
      jclass wrapper = env->FindClass("com/infolitz/commwithc/shared/wrapper/PortflagWrapper");
      jmethodID constructor = env->GetMethodID(wrapper, "<init>", "(I)V");
      jobject wrapperObject = env->NewObject(wrapper, constructor, flagPort);

      // print value before increment
      jmethodID getPortt = env->GetMethodID(wrapper, "getPort", "()I");
      jint ret = env->CallIntMethod(wrapperObject, getPortt);
      __android_log_print(ANDROID_LOG_ERROR, "C:at flag value port :", "%d", ret);*/

    ///safe code closee

    if (ret == -1) {

        wrapperclass = env->FindClass("com/infolitz/commwithc/shared/wrapper/PortflagWrapper");
        constructor = env->GetMethodID(wrapperclass, "<init>", "()V");
        wrapperObject = env->NewObject(wrapperclass, constructor);

        // print value before increment
        getPortt = env->GetMethodID(wrapperclass, "getPort", "()I");
        ret = env->CallIntMethod(wrapperObject, getPortt);
        __android_log_print(ANDROID_LOG_ERROR, "C:at flag value port :", "%d", ret);

        // call inc3
        cls = env->FindClass("com/infolitz/commwithc/shared/SpeedRpmActivity");
        callToSetVal = env->GetStaticMethodID(cls, "callToSetVal", "(I)V");
        //env->CallStaticVoidMethod(cls, callToSetVal, wrapperObject);

        __android_log_print(ANDROID_LOG_ERROR, "C:at", "%s", "serial_port");
        system("su");
        system("su chmod 777 /dev/ttyS0");
        if ((serial_port = serialReadData("/dev/ttyS0", 9600, my_string)) <
            0)    /* open serial port--"/dev/ttyS0",9600 --("/sys/class/tty/ttyS0", 115200) */
        {
            __android_log_print(ANDROID_LOG_ERROR, "Unable to open serial device: ::", "%s",
                                strerror(errno));
//                return (env)->NewStringUTF("1");
        }
        fflush(stdout);
        __android_log_print(ANDROID_LOG_ERROR, "C:string isssss ::", "%s", my_string);
        __android_log_print(ANDROID_LOG_ERROR, "C:bit size received isssss ::", "%d",
                            rebitSize);

        /*set value to setPOrt methond*/
        env->CallStaticVoidMethod(cls, callToSetVal, serial_port); //
        /*close set value to setPOrt methond* */

        snprintf(mystringPort, sizeof(mystringPort), "%s%s%d", my_string, " ; ",
                 serial_port); //concatenate uart string with Serial port number
        __android_log_print(ANDROID_LOG_ERROR, "C:complete string is ::", "%s", mystringPort);
        result = (env)->NewStringUTF(mystringPort); // return with uart string and port number
            return result;
    } else {

        serialGetstring1(my_string, ret, &rebitSize);

        __android_log_print(ANDROID_LOG_ERROR, "C:string2 isssss ::", "%s", my_string);
        result = (env)->NewStringUTF(my_string);// return with uart string only
            return result;
    }


    //////////////////////////////


}



/*JNIEnv *create_vm(JavaVM **pVm) {
    JavaVM* jvm;
    JNIEnv* env;
    JavaVMInitArgs args;
    JavaVMOption options[1];

    *//* There is a new JNI_VERSION_1_4, but it doesn't add anything for the purposes of our example. *//*
    args.version = JNI_VERSION_1_2;
    args.nOptions = 1;
//    C:\Users\sajil\AppData\Local\Android\Sdk\sources\android-31\java
//    D:\\Android Infolitz_ NP\\Android Automotive Required files\\Projects\\Commwithc\\shared\\src\\main\\java\\com\\infolitz\\commwithc\\shared\\wrapper
//Path to the java source code
    options[0].optionString = "-Djava.class.path=D:\\Android Infolitz_ NP\\Android Automotive Required files\\Projects\\Commwithc\\shared\\src\\main\\java\\com\\infolitz\\commwithc\\shared\\wrapper";
    args.options = options;
    args.ignoreUnrecognized = JNI_FALSE;

    JNI_CreateJavaVM(&jvm, reinterpret_cast<JNIEnv **>((void **) &env), &args);
    return env;
}*/

/*JNIEnv* create_vm(JavaVM** jvm)
{
    JNIEnv* env;
    JavaVMInitArgs vm_args;
    JavaVMOption options;
    HMODULE jvm_dll=NULL;
    int ret;

    options.optionString = "C:\\Test\\src\\main\\java"; //Path to the java source code
    vm_args.version = JNI_VERSION_1_6; //JDK version. This indicates version 1.6
    vm_args.nOptions = 1;
    vm_args.options = &options;
    vm_args.ignoreUnrecognized = 0;
    char t[400];
    mbstowcs(t, "C:\\Program Files (x86)\\Java\\jdk-16.0.1\\jre\\bin\\server\\jvm.dll", 400);
    jvm_dll = LoadLibrary(t);

    /// You might check the GetLastError() here after the LoadLibrary()
    if (jvm_dll == NULL) {
        printf("can't load dll\n");
        printf("error code %d", GetLastError());
    }

    JNI_CreateJavaVM_ptr = (JNI_CreateJavaVM_func)GetProcAddress(jvm_dll, "JNI_CreateJavaVM");

    /// You might check the GetLastError() here
    if (JNI_CreateJavaVM_ptr == NULL) { printf("can't load function\n");
        printf("error code %d", GetLastError());
    }

    printf("came here");
    ret = JNI_CreateJavaVM_ptr(jvm, (void**)&env, &vm_args);
    if (ret < 0)
    {
        printf("\nUnable to Launch JVM\n");
        printf("error code %d", GetLastError());
    }
    else
    {
        printf("got it");
    }
    printf("end");
    return env;
}*/












int thread_function(void *args) {
////    struct readThreadParams *readParams= args;
//    JNIEnv* env = create_vm();

    JNIEnv* env;
    /*JavaVM* jvm;
    env = create_vm(&jvm);*/
    char my_string[256] = "";//string from uart collected
    int serial_port;
    uint8_t rebitSize = 0;

    static jint ret = -1;
    jclass wrapperclass;
    jmethodID constructor;
    jobject wrapperObject;
    jmethodID getPortt;
    jclass cls;
    jmethodID callToSetVal;

    serial_port = serialReadData("/dev/ttyS0", 9600, my_string);

//    __android_log_print(ANDROID_LOG_ERROR, "C:at thread::", "%s", env1);
    __android_log_print(ANDROID_LOG_ERROR, "C:at thread::", "%s", "serial_port2");
    wrapperclass = env->FindClass("com/infolitz/commwithc/shared/wrapper/PortflagWrapper");
    __android_log_print(ANDROID_LOG_ERROR, "C:at thread::", "%s", "serial_port1");
    constructor = env->GetMethodID(wrapperclass, "<init>", "()V");
    wrapperObject = env->NewObject(wrapperclass, constructor);

    // print value before increment
    getPortt = env->GetMethodID(wrapperclass, "getPort", "()I");
    ret = env->CallIntMethod(wrapperObject, getPortt);
    __android_log_print(ANDROID_LOG_ERROR, "C:at flag value port @thread :", "%d", ret);


    /* while (1){
         sleep(0.030);
         __android_log_print(ANDROID_LOG_ERROR, "C:at thread::", "%s", "serial_port");
 //        fflush(stdout);
         serialGetstring1(my_string, serial_port, &rebitSize);


         __android_log_print(ANDROID_LOG_ERROR, "C:string2 isssss thread::", "%s", my_string);
     }*/
    return 1;
}

extern "C"
JNIEXPORT jstring JNICALL
Java_com_infolitz_mycarspeed_shared_CommunicateWithC_useThreadTestLib(JNIEnv *env, jobject thiz) {
    // TODO: implement useThreadTestLib()
    int serial_port;
    uint8_t rebitSize = 0;
    //...........
    static jint ret = -1;
    jclass wrapperclass;
    jmethodID constructor;
    jobject wrapperObject;
    jmethodID getPortt;
    jclass cls;
    jmethodID callToSetVal;
    //.........
    struct ReadThreadParams readParams;

    char my_string[256] = "";//string from uart collected
    jstring result;
    char mystringPort[64] = ""; //will be having or required string and the port value

    env1=env;
    thrd_t call_read_thread_id;

//    readParams.env = env;
//    thrd_create(&call_read_thread_id, thread_function, &readParams);// calling thread function
    thrd_create(&call_read_thread_id, thread_function, NULL);// calling thread function
    //////////////////////////////////////////////////////////////////////////

    ///safe code closee

    if (ret == -1) {

        wrapperclass = env->FindClass("com/infolitz/commwithc/shared/wrapper/PortflagWrapper");
        constructor = env->GetMethodID(wrapperclass, "<init>", "()V");
        wrapperObject = env->NewObject(wrapperclass, constructor);

        // print value before increment
        getPortt = env->GetMethodID(wrapperclass, "getPort", "()I");
        ret = env->CallIntMethod(wrapperObject, getPortt);
        __android_log_print(ANDROID_LOG_ERROR, "C:at flag value port :", "%d", ret);

        // call inc3
        cls = env->FindClass("com/infolitz/commwithc/shared/SpeedRpmActivity");
        callToSetVal = env->GetStaticMethodID(cls, "callToSetVal", "(I)V");
        //env->CallStaticVoidMethod(cls, callToSetVal, wrapperObject);

        __android_log_print(ANDROID_LOG_ERROR, "C:at", "%s", "serial_port");
        system("su");
        system("su chmod 777 /dev/ttyS0");
        if ((serial_port = serialReadData("/dev/ttyS0", 9600, my_string)) <
            0)    /* open serial port--"/dev/ttyS0",9600 --("/sys/class/tty/ttyS0", 115200) */
        {
            __android_log_print(ANDROID_LOG_ERROR, "Unable to open serial device: ::", "%s",
                                strerror(errno));
//                return (env)->NewStringUTF("1");
        }
        fflush(stdout);
        __android_log_print(ANDROID_LOG_ERROR, "C:string isssss ::", "%s", my_string);
        __android_log_print(ANDROID_LOG_ERROR, "C:bit size received isssss ::", "%d",
                            rebitSize);

        /*set value to setPOrt methond*/
        env->CallStaticVoidMethod(cls, callToSetVal, serial_port); //
        /*close set value to setPOrt methond* */

        snprintf(mystringPort, sizeof(mystringPort), "%s%s%d", my_string, " ; ",
                 serial_port); //concatenate uart string with Serial port number
        __android_log_print(ANDROID_LOG_ERROR, "C:complete string is ::", "%s", mystringPort);
        result = (env)->NewStringUTF(mystringPort); // return with uart string and port number
            return result;
    } else {

        serialGetstring1(my_string, ret, &rebitSize);

        __android_log_print(ANDROID_LOG_ERROR, "C:string2 isssss ::", "%s", my_string);
        result = (env)->NewStringUTF(my_string);// return with uart string only
            return result;
    }


    //////////////////////////////


    //.........
}
