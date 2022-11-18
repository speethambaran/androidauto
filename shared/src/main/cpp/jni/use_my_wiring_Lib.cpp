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

    int serial_port;
    char dat;
    char my_string[20]="";

    __android_log_print(ANDROID_LOG_ERROR, "at", "%s", "serial_port");
    system("su");
//    system("chmod 777 /dev/ttyS0");
//    system("cat /dev/ttyS0");
    system("su chmod 777 /dev/ttyS0");
    system("su chmod 777 /dev/gpiomem");
    system("su chmod 777 /dev/mem");
    if ((serial_port = serialOpen ("/dev/ttyS0", 9600)) < 0)	/* open serial port--"/dev/ttyS0",9600 --("/sys/class/tty/ttyS0", 115200) */
    {
        fprintf (stderr, "Unable to open serial device: %s\n", strerror (errno)) ;
        __android_log_print(ANDROID_LOG_ERROR, "Unable to open serial device: ::", "%s", strerror (errno));
//        return (jstring) "Unable to open serial device: ";
        return 1;
    }
    __android_log_print(ANDROID_LOG_ERROR, "serial port value: ", "%d", serial_port);
//    return wiringPiSetup (); //was checking return value
    if (wiringPiSetup () == -1)					/* initializes wiringPi setup */
    {
        fprintf (stdout, "Unable to start wiringPi: %s\n", strerror (errno)) ;
        __android_log_print(ANDROID_LOG_ERROR, "Unable to start wiringPi:\n", "%s", strerror (errno));
//        return (jstring) "Unable to start wiringPi";
        return 2;
    }
    int i=0;


    while(1){

        if(serialDataAvail (serial_port) )
        {
            __android_log_print(ANDROID_LOG_ERROR, "serialDataAvail ::", "%s", "inside");
          /*  dat = serialGetchar (serial_port);		*//* receive character serially*//*
//            printf ("%c", dat) ;
            __android_log_print(ANDROID_LOG_ERROR, "data found is: ::", "%c", dat);*/

//            fflush (stdout) ;
//            return dat;
//            serialPutchar(serial_port, dat);		/* transmit character serially on port */
//            return serial_port;
//            dat = serialGetchar (serial_port);		/* receive character serially*/
            //read(serial_port,&dataa,10);		/* receive character serially*/

//            int buff[20] = {0};

//            serialGetstring( my_string, serial_port);

//            for(int j=0;serialGetchar(serial_port)!='/0';j++){
//                my_string[j] = (char) serialGetstring(serial_port);
               serialGetstring( my_string,serial_port);
//                my_string[j] = (((int)buff[j-1]) & 0xFF);
//            }
            printf ("%s \n", my_string) ;
            fflush (stdout) ;
            __android_log_print(ANDROID_LOG_ERROR, "string isssss ::", "%s", my_string);
            return 90;

          /*  if(dat =='s')
            {
                printf("\n found :-- \t");
                printf ("%s \n", my_string) ;    //printf ("%c", dat) ;
                i=0;
//                return reinterpret_cast<jstring>(my_string);
                return 90;
            }*/
            my_string[i]=dat;
            //printf("data : %s \n", dataa);
            fflush (stdout) ;
            i++;

            //printf("dat %c \n",dat);
            // printf ("%s", my_string) ;
            // serialPutchar(serial_port, dat);		/* transmit character serially on port */
            //return 3;
        }
    }

//    return (jstring) "N";
    return 70;

}




extern "C"
JNIEXPORT jint JNICALL
Java_com_infolitz_mycarspeed_shared_CommunicateWithC_testConn(JNIEnv *env, jobject thiz) {
    int z;
    z= checkConnMetod();
    z=bd();
    return z;
}