
cmake_minimum_required(VERSION 3.4.1)

project(“commwithc”)

include_directories(
        jni
)


add_library(
        srkMyLib
        SHARED
        jni/use_my_wiring_Lib.cpp
        )
add_library(
        wiringPi
        SHARED
        jni/wiringPi.c
)
add_library(
        wiringSerial
        SHARED
        jni/wiringSerial.c
)

add_library(
        myTestConnLib
        SHARED
        jni/my_test_conn.cpp
        )

find_library(
        log-lib

        log)

target_link_libraries(
        srkMyLib
        myTestConnLib
        wiringPi
        wiringSerial

        ${log-lib})