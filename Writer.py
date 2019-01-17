import time
import os
import sys
import serial
import time

ser = serial.Serial('/dev/cu.usbmodem144401', 115200)

input = ser.readline()
stringInput = input.decode("utf-8")

if(os.path.exists("data.txt")):
    os.remove("data.txt")
f = open("data.txt", "w+")

counter = 1
while(True):
    if stringInput[0] == '#':
        f.write(stringInput + "\n")
    else:
        print("nothing")
    input = ser.readline()
    stringInput = input.decode("utf-8")
    counter+=1
    if counter % 10 == 0:
        f.close()
        time.sleep(.01)
        f = open("data.txt", "a")
