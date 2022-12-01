import serial
import sys
#import command
import os
from time import sleep



def uart_method():

        #res = command.run(['ls'])
        os.system('su')
        os.system('su chmod 777 /dev/ttyS0')
        os.system('su chmod 777 /dev/gpiomem')
        # res = os.system('reboot') #working reboot

        #print(res.output)


        ser = serial.Serial ("/dev/ttyS0", 9600)    #Open port with baud rate
       # ser = serial.Serial("/dev/ttyS0",9600, serial.PARITY_NONE, serial.STOPBITS_ONE, serial.EIGHTBITS, 1)
        ser = serial.Serial("/dev/ttyS0",9600, timeout=11)
        while True:
                try:
                        received_data = ser.readline()              #read serial port
                        # received_data = ser.read(10)              #read serial port
                        #sleep(0.03)
                        #data_left = ser.inWaiting()             #check for remaining byte
                        #received_data += ser.read(data_left)
                        #ser.close()
                        #print("done::"+received_data)
                except:
                        received_data="ended except"
                return received_data