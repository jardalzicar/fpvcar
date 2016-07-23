import socket
import serial
import sys


IP = 'raspberrypi.local'
PORT = 8888
SERIAL = '/dev/ttyACM0'
serialActive = False

# Recieve data from socket
def recievePacket():
	try:
		data = sock.recv(1024)
		return data
	except socket.error as msg:
		print 'Failed to recieve data'
		print ('Error Code : ' + str(msg[0]) + ' Message ' + msg[1])
		sys.exit()

# Initialize socket
try:
	sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
	sock.bind((IP, PORT))
	print 'Socket created'
except socket.error as msg:
	print 'Failed to create socket'
	print ('Error Code : ' + str(msg[0]) + ' Message ' + msg[1])
	sys.exit()

# Setup serial port
try:
	ser = serial.Serial(SERIAL, 115200)
	serialActive = True
	print 'Serial communication established'
except Exception as msg:
	print 'Failed open serial port, continuing without serial communication'
	print str(msg)

# Main loop
while(1):
	data = recievePacket()
	#print 'Recieved: ' + data
	if serialActive:
		ser.write(data)
		bytesToRead = ser.inWaiting()
    	print ser.read(bytesToRead)






