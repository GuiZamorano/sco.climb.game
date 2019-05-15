import requests
import RPi.GPIO as GPIO
import time
import serial
import logging



white_button  = 11
red_button    = 16
yellow_button = 29
green_button  = 38
white_led     = 13
red_led       = 18
yellow_led    = 31
green_led     = 40
ser = None

domain = 'http://kidsgogreen-env.b3hvhd5jxm.us-east-2.elasticbeanstalk.com'
# domain = 'http://localhost:8080'
url = domain + '/api/calendar/submitSwipes/123/1/PROJECT SMART?' + \
		'rfid=REPLACE_RFID&activityLevel=REPLACE_ACTIVITY_LEVEL' + \
		'&activityType=miles'


def setup():
    global ser

    logging.basicConfig(filename='/home/pi/Documents/Senior_Design/Hardware.log', level=logging.DEBUG)
    logging.info("Program Initializing")

    ser = serial.Serial('/dev/ttyUSB0',
            baudrate=9600,
            parity=serial.PARITY_ODD,
            stopbits=serial.STOPBITS_ONE,
            bytesize=serial.EIGHTBITS,
            xonxoff = False,
            rtscts = False,
            dsrdtr = False)

    if ser.isOpen():
        ser.flushInput()
        ser.flushOutput()


    GPIO.setmode(GPIO.BOARD)
    GPIO.setup(white_button, GPIO.IN, pull_up_down=GPIO.PUD_UP)
    GPIO.setup(white_led, GPIO.OUT)
    GPIO.setup(red_button, GPIO.IN, pull_up_down=GPIO.PUD_UP)
    GPIO.setup(red_led, GPIO.OUT)
    GPIO.setup(yellow_button, GPIO.IN, pull_up_down=GPIO.PUD_UP)
    GPIO.setup(yellow_led, GPIO.OUT)
    GPIO.setup(green_button, GPIO.IN, pull_up_down=GPIO.PUD_UP)
    GPIO.setup(green_led, GPIO.OUT)

    blinkUntilPressed(green_button, green_led)

    logging.info("Initialization Complete")

def loop():
    while True:
        rfid = str(ser.read_until("\cc", 16)[1:-3])

        activityLevel = -1
        allLedOutput(True)
        while activityLevel == -1:
            if GPIO.input(white_button) == False:
                logging.info("White button pressed!")
                activityLevel = 0
                while GPIO.input(white_button) == False:
                    time.sleep(.2)
                allLedOutput(False)
                blink(white_led)
            elif GPIO.input(red_button) == False:
                logging.info("Red button pressed!")
                activityLevel = 1
                while GPIO.input(red_button) == False:
                    time.sleep(.2)
                allLedOutput(False)
                blink(red_led)
            elif GPIO.input(yellow_button) == False:
                logging.info("Yellow button pressed!")
                activityLevel = 2
                while GPIO.input(yellow_button) == False:
                    time.sleep(.2)
                allLedOutput(False)
                blink(yellow_led)
            elif GPIO.input(green_button) == False:
                logging.info("Green button pressed!")
                activityLevel = 3
                while GPIO.input(green_button) == False:
                    time.sleep(.2)
                allLedOutput(False)
                blink(green_led)

        activityLevel = str(activityLevel)
        getString = url.replace('REPLACE_RFID', rfid).replace('REPLACE_ACTIVITY_LEVEL', activityLevel)
        r = requests.get(getString)
        success = str(r.json()) == 'True'
        if success:
            logging.info("Succesfully posted activityLevel \"{}\" for rfid \"{}\"".format(activityLevel, rfid))
        else:
            logging.error("Failed to post activityLevel \"{}\" for rfid \"{}\"".format(activityLevel, rfid))

def endprogram():
    GPIO.output(white_led, False)
    GPIO.output(red_led, False)
    GPIO.output(yellow_led, False)
    GPIO.output(green_led, False)
    GPIO.cleanup()

def blinkUntilPressed(button, led):
    state = False
    while True:
        state = not state
        if GPIO.input(button) == False:
            GPIO.output(led, True)
            while GPIO.input(button) == False:
                time.sleep(2)
            GPIO.output(led, False)
            return
        GPIO.output(led, state)
        time.sleep(.2)


def blink(led):
    for i in range(3):
        GPIO.output(led, False)
        time.sleep(.2)
        GPIO.output(led, True)
        time.sleep(.2)
    GPIO.output(led, False)

def allLedOutput(state):
    GPIO.output(white_led, state)
    GPIO.output(red_led, state)
    GPIO.output(yellow_led, state)
    GPIO.output(green_led, state)

if __name__ == '__main__':
    setup()
    try:
        loop()
    except:
        logging.critical('Program Terminating')
        endprogram()
