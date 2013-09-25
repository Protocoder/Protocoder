package com.makewithmoto.network;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteOrder;
import java.util.Enumeration;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import android.util.Log;

public class NetworkUtils {

	private static final String TAG = "NetworkUtils";

	public static boolean isNetworkAvailable(Context con) {
		ConnectivityManager connectivityManager = (ConnectivityManager) con
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();

		// return (activeNetworkInfo != null &&
		// activeNetworkInfo.isConnectedOrConnecting());
		return (activeNetworkInfo != null && activeNetworkInfo.isConnected());
	}

	// Get broadcast Address
	public static InetAddress getBroadcastAddress(Context c) throws UnknownHostException {
		WifiManager wifi = (WifiManager) c.getSystemService(Context.WIFI_SERVICE);
		DhcpInfo dhcp = wifi.getDhcpInfo();

		if (dhcp == null)
			return InetAddress.getByAddress(null);

		int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;

		byte[] quads = new byte[4];
		for (int k = 0; k < 4; k++) {
			quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
		}

		return InetAddress.getByAddress(quads);
	}

	public static void getGatewayIpAddress(Context c) { 
		//get wifi ip 
		
		final WifiManager manager = (WifiManager) c.getSystemService(c.WIFI_SERVICE);
		final DhcpInfo dhcp = manager.getDhcpInfo();
		final String address = Formatter.formatIpAddress(dhcp.gateway);
		
		  StringBuilder IFCONFIG=new StringBuilder();
		    try {
		        for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
		            NetworkInterface intf = en.nextElement();
		            for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
		                InetAddress inetAddress = enumIpAddr.nextElement();
		                if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress() && inetAddress.isSiteLocalAddress()) {
		                IFCONFIG.append(inetAddress.getHostAddress().toString()+"\n");
		                }

		            }
		        }
		    } catch (SocketException ex) {
		        Log.e("LOG_TAG", ex.toString());
		    }
		    Log.d(TAG, "ifconfig " + IFCONFIG.toString());
		
		Log.d(TAG, "hotspot address is " + address);

	}

	// Get the local IP address
	public static String getLocalIpAddress(Context c) {
		
		    WifiManager wifiManager = (WifiManager) c.getSystemService(c.WIFI_SERVICE);
		    int ipAddress = wifiManager.getConnectionInfo().getIpAddress();

		    // Convert little-endian to big-endianif needed
		    if (ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN)) {
		        ipAddress = Integer.reverseBytes(ipAddress);
		    }

		    byte[] ipByteArray = BigInteger.valueOf(ipAddress).toByteArray();

		    String ipAddressString;
		    try {
		        ipAddressString = InetAddress.getByAddress(ipByteArray).getHostAddress();
		    } catch (UnknownHostException ex) {
		        Log.e("WIFIIP", "Unable to get host address.");
		        ipAddressString = null;
		    }

		    return ipAddressString;
		
		
//		try {
//			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
//				NetworkInterface intf = en.nextElement();
//				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
//					InetAddress inetAddress = enumIpAddr.nextElement();
//					// if (!inetAddress.isLoopbackAddress() &&
//					// !inetAddress.isLinkLocalAddress() &&
//					// inetAddress.isSiteLocalAddress() ) {
//					if (!inetAddress.isLoopbackAddress()
//							&& InetAddressUtils.isIPv4Address(inetAddress.getHostAddress())) {
//						return inetAddress;
//					}
//				}
//			}
//		} catch (SocketException ex) {
//			Log.d(TAG, ex.toString());
//		}
//		return null; 
		    
	}

}
