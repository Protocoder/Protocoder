package com.makewithmoto.apprunner.api;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.nio.channels.DatagramChannel;

import android.app.Activity;
import android.util.Log;
import android.webkit.JavascriptInterface;

import com.makewithmoto.apidoc.annotation.APIMethod;

import de.sciss.net.OSCListener;
import de.sciss.net.OSCMessage;
import de.sciss.net.OSCReceiver;
import de.sciss.net.OSCTransmitter;

public class JNetwork extends JInterface {

	private String TAG = "JNetwork";

	private String callbackfn;

	// OSC server
	OSCReceiver rcv;
	OSCTransmitter trns;
	DatagramChannel dch;
	int n = 0;

	SocketAddress inPort = null;

	// OSC client
	SocketAddress addr2;
	DatagramChannel dch2;
	OSCTransmitter trns2;
	boolean oscConnected = false;

	public JNetwork(Activity a) {
		super(a);

	}

	@JavascriptInterface
	@APIMethod(description = "initializes makr board", example = "makr.start();")
	public void startOSCServer(String port, final String callbackfn) {

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

					Log.d(TAG, "msg rcv " + msg.getName() + " " + msg.getArgCount());
					// msg.getArg(n);

					callback(callbackfn, "\"" +  msg.getName() + "\"");
				}
			});

			rcv.startListening();

		} catch (IOException e2) {
			Log.d(TAG, e2.getLocalizedMessage());
		}
	}

	public void connect(String address, int port) {

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

	@JavascriptInterface
	@APIMethod(description = "initializes makr board", example = "makr.start();")
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
	
	public void stopOSCServer() { 
		try {
			dch.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		rcv.dispose();
		
	}
	
	public boolean isOSCConnected() {
		return oscConnected;
	}

	// previous callback callback("OnSerialRead("+receivedData+");");
	// callback(callbackfn, "\"" + receivedData + "\"");

}