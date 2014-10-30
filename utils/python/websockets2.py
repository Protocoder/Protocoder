#!/usr/bin/python
import websocket
import thread
import time
import pprint


def on_message(ws, message):
    pprint.pprint(message)

def on_error(ws, error):
    print error

def on_close(ws):
    print "### closed ###"

def on_open(ws):
	print "hola"
 	ws.send('{"name":"startAccelerometer"}')



pprint = pprint.PrettyPrinter(indent=4)

if __name__ == "__main__":
    websocket.enableTrace(True)
    ws = websocket.WebSocketApp("ws://172.16.17.146:2525",
                                on_message = on_message,
                                on_error = on_error,
                                on_close = on_close)
    ws.on_open = on_open

    ws.run_forever()