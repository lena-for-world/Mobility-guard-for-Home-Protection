import RPi.GPIO as GPIO
import time
import e_pisql3
from random import *

# change these as desired - they're the pins connected from the
# SPI port on the ADC to the Cobbler
SPICLK = 11
SPIMISO = 9
SPIMOSI = 10
SPICS = 8
mq7_dpin = 26
mq7_apin = 0
CO_density = 0

#port init
def init():
         GPIO.setwarnings(False)
         GPIO.cleanup()
         GPIO.setmode(GPIO.BCM)
         GPIO.setup(SPIMOSI, GPIO.OUT)
         GPIO.setup(SPIMISO, GPIO.IN)
         GPIO.setup(SPICLK, GPIO.OUT)
         GPIO.setup(SPICS, GPIO.OUT)
         GPIO.setup(mq7_dpin,GPIO.IN,pull_up_down=GPIO.PUD_DOWN)


#read SPI data from MCP3008(or MCP3204) chip,8 possible adc's (0 thru 7)
def readadc(adcnum, clockpin, mosipin, misopin, cspin):
        if ((adcnum > 7) or (adcnum < 0)):
                return -1
        GPIO.output(cspin, True)	

        GPIO.output(clockpin, False)  # start clock low
        GPIO.output(cspin, False)     # bring CS low

        commandout = adcnum
        commandout |= 0x18  # start bit + single-ended bit
        commandout <<= 3    # we only need to send 5 bits here
        for i in range(5):
                if (commandout & 0x80):
                        GPIO.output(mosipin, True)
                else:
                        GPIO.output(mosipin, False)
                commandout <<= 1
                GPIO.output(clockpin, True)
                GPIO.output(clockpin, False)

        adcout = 0
        # read in one empty bit, one null bit and 10 ADC bits
        for i in range(12):
                GPIO.output(clockpin, True)
                GPIO.output(clockpin, False)
                adcout <<= 1
                if (GPIO.input(misopin)):
                        adcout |= 0x1

        GPIO.output(cspin, True)
        
        adcout >>= 1       # first bit is 'null' so drop it
        return adcout

#main
def main():
    time.sleep(5)
    COlevel=readadc(mq7_apin, SPICLK, SPIMOSI, SPIMISO, SPICS)
    if GPIO.input(mq7_dpin):
        print("CO not leak")
        ppm = 0
        temp = uniform(1.0, 99.9)
        time.sleep(1)
    else:
        print("CO is detected")
        volt = (COlevel/1024.)*5 +0.00001
        RS = (5.0-volt) / volt
#        ppm = 19.32 * (RS ** -0.64)
#        CO_AD = str("%.2f"%((COlevel/1024.)*5))+" V"
#        CO_density = str("%.2f"%((COlevel/1024.)*100))+" %"
        temp = uniform(1.0, 99.9)
#        print(ppm)
        print(temp)
    e_pisql3.insert(temp)
    return temp

#if __name__=='__main__':
#    init()
#    while(1):
#        main()
