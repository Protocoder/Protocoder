from ws4py.client.threadedclient import WebSocketClient
import pprint
import json
import sys
import numpy as np
import matplotlib.pyplot as plt

plt.axis([0, 1000, 0, 12])
plt.ion()
plt.show()
pos = 25;

class Client(WebSocketClient):

    def opened(self):
        self.send('{"name":"startAccelerometer"}')

    def closed(self, code, reason=None):
        print "Closed down", code, reason

    def received_message(self, jsonObj):
        try:
            obj = json.loads(jsonObj.data)
            #print(obj)
            if (obj['name'] == "startAccelerometer"):
                global pos
                x = float(obj['data']['x'])
                print str(pos) + " " + str(x)
                #plt.scatter(pos, x)
                #pos += 1
                #plt.draw()
                #plt.pause(0.001)
            elif (obj.msg == "startOrientation"):
                print m.data

        except:
            print sys.exc_info()[0] # . ' error'
            pass
        #print obj
      


pprint = pprint.PrettyPrinter(indent=4)
if __name__ == '__main__':
    try:
        ws = Client('ws://172.16.17.146:2525/', protocols=['http-only', 'chat'])
        ws.connect()
        ws.run_forever()
    except KeyboardInterrupt:
        ws.close()
