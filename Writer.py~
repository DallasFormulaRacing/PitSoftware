import time
import os
import sys
import serial
import time

print("trying to create Serial")
ser = serial.Serial('/dev/cu.usbmodem143401', 115200)
print("created")
print("read input")
if(os.path.exists("data.txt")):
    os.remove("data.txt")
print("removed file if existed")
f = open("data.txt", "w+")
f.write("hello")
print("Created and wrote")
input = ser.readline()
stringInput = input.decode("utf-8")
counter = 1
print("About to enter")
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
