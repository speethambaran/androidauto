#include <jni.h>
#include <string>
#include <iostream>
#include <android/log.h>


#include "my_test_conn.cpp"
#include "wiringPi.h"
#include "wiringSerial.h"

extern "C" JNIEXPORT jstring JNICALL
Java_com_infolitz_mycarspeed_shared_CommunicateWithC_useWiringLib(JNIEnv *env, jobject) {
    std::string hello = "Hello from C";

    int serial_port;
    char my_string[20]="";
    jstring result;

    __android_log_print(ANDROID_LOG_ERROR, "at", "%s", "serial_port");
    system("su");
//    system("chmod 777 /dev/ttyS0");
//    system("cat /dev/ttyS0");
    system("su chmod 777 /dev/ttyS0");
    system("su chmod 777 /dev/gpiomem");
    system("su chmod 777 /dev/mem");
    if ((serial_port = serialOpen ("/dev/ttyS0", 9600)) < 0)	/* open serial port--"/dev/ttyS0",9600 --("/sys/class/tty/ttyS0", 115200) */
    {
        __android_log_print(ANDROID_LOG_ERROR, "Unable to open serial device: ::", "%s", strerror (errno));
        return (env)->NewStringUTF("1");
    }
    __android_log_print(ANDROID_LOG_ERROR, "serial port value: ", "%d", serial_port);
    if (wiringPiSetup () == -1)					/* initializes wiringPi setup */
    {
        __android_log_print(ANDROID_LOG_ERROR, "Unable to start wiringPi:\n", "%s", strerror (errno));
        return (env)->NewStringUTF("2");
    }


    while(1){

        if(serialDataAvail (serial_port) )
        {
            __android_log_print(ANDROID_LOG_ERROR, "serialDataAvail ::", "%s", "inside");
           /* if(serialGetstring( my_string,serial_port) == 0){

                result = (env)->NewStringUTF("wrong format");
                return result;
            }else {*/
                serialGetstring( my_string,serial_port);
                printf("%s \n", my_string);
                fflush(stdout);
                __android_log_print(ANDROID_LOG_ERROR, "string isssss ::", "%s", my_string);

                result = (env)->NewStringUTF(my_string);
                return result;
           // }
        }
    }

}
