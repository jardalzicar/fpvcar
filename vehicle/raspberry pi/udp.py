import socket
import serial
import sys


IP = '10.0.0.61'
PORT = 8888
SERIAL = '/dev/ttyACM0'

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
	ser = serial.Serial(SERIAL, 9600)
except Exception as msg:
	print 'Failed open serial port'
	print str(msg)
	sys.exit()

# Main loop
while(1):
	data = recievePacket()
	print 'Recieved: ' + data
	ser.write(data)






