import pygame
import time

def init():
    BUFFER = 3072
    check = 0
    FREQ, SIZE, CHAN = getmixerargs()
    pygame.mixer.init(FREQ, SIZE, CHAN, BUFFER)
    return check

def getmixerargs():
    pygame.mixer.init()
    freq, size, chan = pygame.mixer.get_init()
    return freq, size, chan

def beepPlay():
    i = 0
    print("playing")
    pygame.init()
    pygame.mixer.init()
    clock = pygame.time.Clock()
    while i < 3:
        pygame.mixer.music.load('beep.mp3')
        pygame.mixer.music.play()
        while pygame.mixer.music.get_busy():
            clock.tick(3000)
        i = i+1

def beepOff():
    print("alarm stopped")
    pygame.mixer.music.stop()
