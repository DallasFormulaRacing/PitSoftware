import os
import sys
sys.path.append("/anaconda3/lib/python3.6/site-packages")
import serial
import time

ser = serial.Serial('/dev/cu.usbmodem144401', 115200)

input = ser.readline()
stringInput = input.decode("utf-8")

count = 0

data = ""

while(count < 16):
    if stringInput[0] == '#':
        data += stringInput + '\n'
    else:
        print("nothing")
    input = ser.readline()
    stringInput = input.decode("utf-8")
    count+=1

print(data)
