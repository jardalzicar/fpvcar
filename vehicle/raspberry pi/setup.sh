#!/bin/bash 
# Setup remote control and video stream

python scripts/udp.py & 
raspivid -n -w 1280 -h 720 -fps 25 -b 3600000 -t 0 -o - | gst-launch-1.0 -v fdsrc !  h264parse ! rtph264pay config-interval=10 pt=96 ! udpsink host=jarda-mac.local port=9000
