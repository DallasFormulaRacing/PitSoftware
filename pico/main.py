import time
while True:
    with open("data.txt", "r") as file:
        for line in file.readlines():
            print(line)
            time.sleep(0.05)