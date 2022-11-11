#include <jni.h>
#include <string>
#include <iostream>
#include <android/log.h>


#include "my_test_conn.cpp"
#include "wiringPi.h"
#include "wiringSerial.h"

extern "C" JNIEXPORT jint JNICALL
Java_com_infolitz_mycarspeed_shared_CommunicateWithC_useWiringLib(JNIEnv *env, jobject) {
    std::string hello = "Hello from C";

    int serial_port=bd() ;
    char dat;

    printf("yess");
    __android_log_print(ANDROID_LOG_ERROR, "at", "%s", "serial_port");
    if ((serial_port = serialOpen ("/dev/ttyS0", 9600)) < 0)	/* open serial port--"/dev/ttyS0",9600 --("/sys/class/tty/ttyS0", 115200) */
    {
        fprintf (stderr, "Unable to open serial device: %s\n", strerror (errno)) ;
//        return serialOpen ("/dev/ttyS0", 115200);
        return serialOpen ("/dev/ttyS0", 9600);
    }

    if (wiringPiSetup () == -1)					/* initializes wiringPi setup */
    {
        fprintf (stdout, "Unable to start wiringPi: %s\n", strerror (errno)) ;
        return 2;
    }

   /* while(1){

        if(serialDataAvail (serial_port) )
        {
            dat = serialGetchar (serial_port);		*//* receive character serially*//*
            printf ("%c", dat) ;
            fflush (stdout) ;
            serialPutchar(serial_port, dat);		*//* transmit character serially on port *//*
            return (jstring) "dat";
        }
    }

*/
    return serial_port;

}




extern "C"
JNIEXPORT jint JNICALL
Java_com_infolitz_mycarspeed_shared_CommunicateWithC_testConn(JNIEnv *env, jobject thiz) {
    int z;
    z= checkConnMetod();
    z=bd();
    return z;
}