#!/usr/bin/python

from socket import *
import time, sys

address = ('aaaa::c30c:0:0:4', 5554)
#address = ('fe80::c30c:0:0:4', 5555)
client_socket = socket(AF_INET6, SOCK_DGRAM)
client_socket.settimeout(float(sys.argv[1]))
client_socket.bind(('', 5555))
recv_socket = socket(AF_INET6, SOCK_DGRAM)
recv_socket.settimeout(float(sys.argv[1]))

num_retransmits = 0
while(num_retransmits < 60):
    num_retransmits = num_retransmits + 1
    data = "Request "+str(num_retransmits)
    try:
    	client_socket.sendto(data, address)
   	print "Sending request",num_retransmits

    	#recv_data, addr = recv_socket.recvfrom(2048)
    	#print "RECEIVED: ",recv_data
    except Exception,e:
        print "Errore:", e
	pass
    time.sleep(1)

