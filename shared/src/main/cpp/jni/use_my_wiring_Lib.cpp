#include <jni.h>
#include <string>
#include <iostream>

#include "wiringPi.h"
#include "wiringSerial.h"

extern "C" JNIEXPORT jstring JNICALL
Java_com_infolitz_newcar_shared_CommunicateWithC_useWiringLib(JNIEnv *env, jobject) {
    std::string hello = "Hello from C";
    int a,b,c;
    a=10;
    b=20;
    c=a+b;
    b=a-c;
    printf("%d",c);

    int serial_port;
    if ((serial_port = serialOpen("/dev/ttyS0", 9600)) < 0)    /* open serial port*/
    {
//        fprintf(stderr, "Unable to open serial device: %s\n", strerror(errno));
//        return reinterpret_cast<jstring>(1);
        printf("%d",c);
    }

//.... for uart
//    int serial_port;
//    char dat;
//    if ((serial_port = serialOpen("/dev/ttyS0", 9600)) < 0)    /* open serial port*/
//    {
//        fprintf(stderr, "Unable to open serial device: %s\n", strerror(errno));
//        return reinterpret_cast<jstring>(1);
//    }
//
//    if (wiringPiSetup() == -1)                     /*initializes wiringPi setup */
//    {
//        fprintf(stdout, "Unable to start wiringPi: %s\n", strerror(errno));
//        return reinterpret_cast<jstring>(1);
//    }
//
//    while (1) {
//
//        if (serialDataAvail(serial_port)) {
//            dat = serialGetchar(serial_port);         /*receive character serially*/
//            printf("%c", dat);
//            fflush(stdout);
//            serialPutchar(serial_port, dat);        /* transmit character serially on port*/
//            return reinterpret_cast<jstring>(dat);
//        }
//    }

//.... for uart end

   return env->NewStringUTF(hello.c_str());

}



