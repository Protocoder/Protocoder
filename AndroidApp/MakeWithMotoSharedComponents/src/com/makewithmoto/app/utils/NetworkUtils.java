package com.makewithmoto.app.utils;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Enumeration;

import org.apache.http.conn.util.InetAddressUtils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
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

	// Get the local IP address
	public static InetAddress getLocalIpAddress() {
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					// if (!inetAddress.isLoopbackAddress() &&
					// !inetAddress.isLinkLocalAddress() &&
					// inetAddress.isSiteLocalAddress() ) {
					if (!inetAddress.isLoopbackAddress()
							&& InetAddressUtils.isIPv4Address(inetAddress.getHostAddress())) {
						return inetAddress;
					}
				}
			}
		} catch (SocketException ex) {
			Log.d(TAG, ex.toString());
		}
		return null;
	}

}
