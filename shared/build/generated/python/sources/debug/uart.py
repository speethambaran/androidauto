import serial
import sys
#import command
import os
from time import sleep
import  RPi.GPIO as GPIO



def uart_method():

        #res = command.run(['ls'])
        os.system('su')
        os.system('su chmod 777 /dev/ttyS0')
        os.system('su chmod 777 /dev/gpiomem')
        # res = os.system('reboot') #working reboot
        GPIO.setwarnings(False)
        GPIO.setmode(GPIO.BOARD)

        GPIO.setup(11,GPIO.OUT)

        #print(res.output)


        ser = serial.Serial ("/dev/ttyS0", 9600)    #Open port with baud rate
        while True:
                received_data = ser.readline()              #read serial port
                # received_data = ser.read(10)              #read serial port
                sleep(0.03)
                data_left = ser.inWaiting()             #check for remaining byte
                received_data += ser.read(data_left)
                ser.close()
                print("done::"+received_data)
                return received_data