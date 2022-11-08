
cmake_minimum_required(VERSION 3.4.1)

project(“mycarspeed”)

# wiringPiLib library
include_directories(
        jni
)

add_library (wiringPiLib SHARED IMPORTED)
set_target_properties (wiringPiLib
        PROPERTIES IMPORTED_LOCATION
        "wiringPiLib/${ANDROID_ABI}/libwiringPi.so")

#\wiringPiLib\arm64-v8a

add_library( # Sets the name of the library.
        srkMyLib
        # Sets the library as a shared library.
        SHARED
        #        src/main/jni/use_my_wiring_Lib.c
        jni/use_my_wiring_Lib.cpp
        )


find_library(
        log-lib

        log)

target_link_libraries(
        srkMyLib
        wiringPiLib

        ${log-lib})
