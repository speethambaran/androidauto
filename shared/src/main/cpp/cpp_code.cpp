//
// Created by Nithin on 01-11-2022.
//
#include <jni.h>
#include <string>
#include <iostream>

#include <cstdio>
#include <cstring>
#include <cerrno>

//#include <wiringPi.h>
#include "C:/Users/sajil/AppData/Local/Android/Sdk/ndk/21.4.7075529/toolchains/llvm/prebuilt/windows-x86_64/sysroot/usr/include/c++/v1/WiringPi-master/wiringPi/wiringSerial.h"
#include "C:/Users/sajil/AppData/Local/Android/Sdk/ndk/21.4.7075529/toolchains/llvm/prebuilt/windows-x86_64/sysroot/usr/include/c++/v1/WiringPi-master/wiringPi/wiringPi.h"
//#include "D:/Android Infolitz_ NP/Android Automotive Required files/Projects/new car java/WiringPi-master/wiringPi/wiringPi.h"
//#include "D:/Android Infolitz_ NP/Android Automotive Required files/Projects/new car java/WiringPi-master/wiringPi/wiringPi.h"
//#include <wiringSerial.h>

/*
extern "C" JNIEXPORT jstring JNICALL
Java_com_infolitz_newcar_shared_SpeedometerActivity_NewMethod(JNIEnv *env,jobject){
    std::string hello="Hello from C++";
    return env->NewStringUTF(hello.c_str());
}
*/

extern "C"
JNIEXPORT jstring JNICALL
Java_com_infolitz_newcar_shared_CommunicateWithC_Method(JNIEnv *env, jobject thiz) {
    std::string hello="Hello from C++";


    int serial_port ;
    char dat;
    if ((serial_port = serialOpen ("/dev/ttyS0", 9600)) < 0)	/* open serial port */
    {
        fprintf (stderr, "Unable to open serial device: %s\n", strerror (errno)) ;
        return reinterpret_cast<jstring>(1);
    }

    if (wiringPiSetup () == -1)					/* initializes wiringPi setup */
    {
        fprintf (stdout, "Unable to start wiringPi: %s\n", strerror (errno)) ;
        return reinterpret_cast<jstring>(1);
    }

    while(1){

        if(serialDataAvail (serial_port) )
        {
            dat = serialGetchar (serial_port);		/* receive character serially*/
            printf ("%c", dat) ;
            fflush (stdout) ;
            serialPutchar(serial_port, dat);		/* transmit character serially on port */
        }
    }



    return env->NewStringUTF(hello.c_str());
}
