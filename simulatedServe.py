import socket 

def checkFormat(msg):
	formatedMsg = str(msg).split(':')
	if len(formatedMsg) != 3:
		return []
	protocol = formatedMsg[2].split('/')[0]
	if formatedMsg[0] != "b'GET" or protocol != 'SDTP':
		return []
	return formatedMsg

def buildMsg(value):
	return "100 Ok:"+value+":SDTP/0.9"


PORT = 80
CONNECTION_LIMIT = 1


host = ''
port = PORT
origin = (host,port) 
server = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
server.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
server.bind(origin)
server.listen(CONNECTION_LIMIT)
print("Server 1")
while True:
	con, client = server.accept()
	print("Client connected!")
	msg = con.recv(1024)
	formatedMsg = checkFormat(msg)
	if len(formatedMsg) != 0:
		if formatedMsg[1] == 'brightness':
			responseMsg = buildMsg("0.65") #Simulating brightness
			con.send(responseMsg.encode())
		elif formatedMsg[1] == "temperature": #Simulating temperature
			responseMsg = buildMsg("25.3")
			con.send(responseMsg.encode())
		elif formatedMsg[1] == "humidity": #Simulating humidity
			responseMsg = buildMsg("87")
			con.send(responseMsg.encode())
	else:
		con.send('400 Bad Request:-1:SDTP/0.9'.encode())
	
	print("Closing connection")
	con.close() #Closing connection
