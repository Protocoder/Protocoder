package com.makewithmoto.network;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.nio.channels.DatagramChannel;
import java.util.Vector;

import android.util.Log;
import de.sciss.net.OSCListener;
import de.sciss.net.OSCMessage;
import de.sciss.net.OSCReceiver;
import de.sciss.net.OSCTransmitter;

public class OSC {

	protected static final String TAG = "OSC";

	public interface OSCServerListener {
		
		public void onMessage(OSCMessage msg); 
		
	}
	

	public class Server {
		
		// OSC server
		OSCReceiver rcv;
		OSCTransmitter trns;
		DatagramChannel dch;
		int n = 0;

		SocketAddress inPort = null;
		
		Vector<OSCServerListener> listeners = new Vector<OSCServerListener>();


		public void start(String port, final String callbackfn) {

			rcv = null;
			dch = null;

			try {
				inPort = new InetSocketAddress(Integer.parseInt(port));

				dch = DatagramChannel.open();
				dch.socket().bind(inPort); // assigns an automatic local socket
				// address
				rcv = OSCReceiver.newUsing(dch);

				rcv.addOSCListener(new OSCListener() {

					public void messageReceived(OSCMessage msg,
							SocketAddress sender, long time) {

						Log.d(TAG,
								"msg rcv " + msg.getName() + " "
										+ msg.getArgCount());
						// msg.getArg(n);
						for (OSCServerListener l : listeners) {
							((OSCServerListener)l).onMessage(msg);
						}

					}
				});

				rcv.startListening();

			} catch (IOException e2) {
				Log.d(TAG, e2.getLocalizedMessage());
			}
		}

		public void stopOSCServer() {
			try {
				dch.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			rcv.dispose();
		}
		
		public void addListener(OSCServerListener listener) {
			listeners.add(listener);
		}

		public void removeListener(OSCServerListener listener) {
			listeners.remove(listener);
		}


	}

	public class Client {

		// OSC client
		SocketAddress addr2;
		DatagramChannel dch2;
		OSCTransmitter trns2;
		boolean oscConnected = false;


		public void connectOSC(String address, int port) {

			Log.d(TAG, "connecting to " + address + " in " + port);
			try {
				addr2 = new InetSocketAddress(InetAddress.getByName(address), port);
				dch2 = DatagramChannel.open();
				dch2.socket().bind(null);
				trns2 = OSCTransmitter.newUsing(dch2);
				Log.d(TAG, "connected to " + address + " in " + port);
				oscConnected = true;
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (UnknownHostException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		public boolean isOSCConnected() {
			return oscConnected;
		}
		

		public void sendOSC(final String msg, final String content) {

			if (oscConnected == true) {
				// send

				Thread t = new Thread(new Runnable() {

					@Override
					public void run() {
						Object[] o = new Object[1];
						o[0] = content;
						try {
							trns2.send(new OSCMessage(msg, o), addr2);
						} catch (IOException e) {
							e.printStackTrace();
						}

					}
				});
				t.start();
			}
		}

		public void disconnectOSC() {
			try {
				dch2.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			trns2.dispose();

		}
	}
	
	
}
