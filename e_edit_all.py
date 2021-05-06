import paho.mqtt.client as mqtt
import RPi.GPIO as GPIO
import time
#import e_pir_a as pir_a
#import e_finedust_a as finedust_a
#import e_led_a as led_a
import e_mq_a3 as mq
import e_alarm as alarm
from random import *

def init():
#    global pirPin
#    global ser
#    global dust
    global check
    mq.init()
#    pirPin=pir_a.init()
#    ser,dust=finedust_a.init()
#    led_a.init()
    check = alarm.init()
    alarm.init()

def on_connect(client, userdata, flags, rc):
    print(("Connect with result code" + str(rc)))
    client.subscribe("LED")
    client.subscribe("TURNOFF_WARNING")

def on_message(client, userdata, msg):
    msg.payload = msg.payload.decode("utf-8")
    if "ALLON" in msg.payload:
        alarm.beepOff()
    if "WRNFF" in msg.payload:
        print("alarm off")
        led_a.led1(True)
        led_a.led2(True)
    if "ALLOFF" in msg.payload:
        led_a.led1(False)
        led_a.led2(False)
    if "ONEON" in msg.payload:
        led_a.led1(True)
        print("one light")
    if "ONEOFF" in msg.payload:
        led_a.led1(False)
    if "TWOON" in msg.payload:
        led_a.led2(True)
        print("second light")
    if "TWOOFF" in msg.payload:
        led_a.led2(False)
    if "register" in msg.payload:
        print("sdfd")

if __name__ == "__main__":
    client = mqtt.Client()
    client.on_connect = on_connect
    client.on_message = on_message

    client.connect("18.224.71.242")
    
    mqttc = mqtt.Client("publisher")
    mqttc.connect("18.224.71.242")

    init()
    mqttc.loop_start()
    client.loop_start()
    try:
        while True:
#            fd, ufd = finedust_a.finedust(ser,dust)
#            mqttc.publish("fine/dust1", fd)
#            mqttc.publish("fine/dust2", ufd)
#            if(pir_a.pir(pirPin)):
#                print("good")
            mqvalue = mq.main()
            if (mqvalue > 60 and check == 0):
#alarm.beepPlay()
                mqttc.publish("CO", mqvalue)
            if (mqvalue <= 60):
                check = 0
    except KeyboardInterrupt:
        GPIO.cleanup()
    client.loop_stop()
    mqttc.loop_stop()
