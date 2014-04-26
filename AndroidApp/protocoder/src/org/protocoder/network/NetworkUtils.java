/*
 * Protocoder 
 * A prototyping platform for Android devices 
 * 
 * Victor Diaz Barrales victormdb@gmail.com
 *
 * Copyright (C) 2014 Victor Diaz
 * Copyright (C) 2013 Motorola Mobility LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions: 
 * 
 * The above copyright notice and this permission notice shall be included in all 
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN 
 * THE SOFTWARE.
 * 
 */

package org.protocoder.network;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.ByteOrder;
import java.util.Enumeration;
import java.util.Properties;

import javax.activation.DataHandler;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.util.ByteArrayDataSource;

import org.protocoder.apprunner.AppRunnerSettings;
import org.protocoder.utils.MLog;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.text.format.Formatter;
import android.util.Log;

public class NetworkUtils {

	private static final String TAG = "NetworkUtils";

	// usually, subclasses of AsyncTask are declared inside the activity class.
	// that way, you can easily modify the UI thread from here
	public static class DownloadTask extends AsyncTask<String, Integer, String> {

		private final Context context;
		private DownloadListener downloadListener;
		private final String path;

		public interface DownloadListener {
			public void onUpdate(int progress);
		}

		public DownloadTask(Context context, String fileName) {
			this.context = context;
			path = AppRunnerSettings.get().project.getStoragePath() + File.separator + fileName;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}

		@Override
		protected void onProgressUpdate(Integer... progress) {
			super.onProgressUpdate(progress);
			// if we get here, length is known, now set indeterminate to false
			// mProgressDialog.setProgress(progress[0]);
			downloadListener.onUpdate(progress[0]);
		}

		@Override
		protected void onPostExecute(String result) {
			downloadListener = null;
		}

		@Override
		protected String doInBackground(String... sUrl) {
			// take CPU lock to prevent CPU from going off if the user
			// presses the power button during download
			PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
			PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getClass().getName());
			wl.acquire();

			try {
				InputStream input = null;
				OutputStream output = null;
				HttpURLConnection connection = null;
				try {
					URL url = new URL(sUrl[0]);
					connection = (HttpURLConnection) url.openConnection();
					connection.connect();

					// expect HTTP 200 OK, so we don't mistakenly save error
					// report
					// instead of the file
					if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
						return "Server returned HTTP " + connection.getResponseCode() + " "
								+ connection.getResponseMessage();
					}

					// this will be useful to display download percentage
					// might be -1: server did not report the length
					int fileLength = connection.getContentLength();

					// download the file
					input = connection.getInputStream();
					output = new FileOutputStream(path);

					byte data[] = new byte[4096];
					long total = 0;
					int count;
					while ((count = input.read(data)) != -1) {
						// allow canceling with back button
						if (isCancelled()) {
							return null;
						}
						total += count;
						// publishing the progress....
						if (fileLength > 0) {
							publishProgress((int) (total * 100 / fileLength));
						}
						output.write(data, 0, count);
					}
				} catch (Exception e) {
					return e.toString();
				} finally {
					try {
						if (output != null) {
							output.close();
						}
						if (input != null) {
							input.close();
						}
					} catch (IOException ignored) {
					}

					if (connection != null) {
						connection.disconnect();
					}
				}
			} finally {
				wl.release();
			}
			return null;
		}

		public void addListener(DownloadListener listener) {
			downloadListener = listener;

		}
	}

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

		if (dhcp == null) {
			return InetAddress.getByAddress(null);
		}

		int broadcast = (dhcp.ipAddress & dhcp.netmask) | ~dhcp.netmask;

		byte[] quads = new byte[4];
		for (int k = 0; k < 4; k++) {
			quads[k] = (byte) ((broadcast >> k * 8) & 0xFF);
		}

		return InetAddress.getByAddress(quads);
	}

	public static void getGatewayIpAddress(Context c) {
		// get wifi ip

		final WifiManager manager = (WifiManager) c.getSystemService(Context.WIFI_SERVICE);
		final DhcpInfo dhcp = manager.getDhcpInfo();
		final String address = Formatter.formatIpAddress(dhcp.gateway);

		StringBuilder IFCONFIG = new StringBuilder();
		try {
			for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
				NetworkInterface intf = en.nextElement();
				for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress() && !inetAddress.isLinkLocalAddress()
							&& inetAddress.isSiteLocalAddress()) {
						IFCONFIG.append(inetAddress.getHostAddress().toString() + "\n");
					}

				}
			}
		} catch (SocketException ex) {
			Log.e("LOG_TAG", ex.toString());
		}
		MLog.d(TAG, "ifconfig " + IFCONFIG.toString());

		MLog.d(TAG, "hotspot address is " + address);

	}

	// Get the local IP address
	public static String getLocalIpAddress(Context c) {

		WifiManager wifiManager = (WifiManager) c.getSystemService(Context.WIFI_SERVICE);
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

		// try {
		// for (Enumeration<NetworkInterface> en =
		// NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
		// NetworkInterface intf = en.nextElement();
		// for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses();
		// enumIpAddr.hasMoreElements();) {
		// InetAddress inetAddress = enumIpAddr.nextElement();
		// // if (!inetAddress.isLoopbackAddress() &&
		// // !inetAddress.isLinkLocalAddress() &&
		// // inetAddress.isSiteLocalAddress() ) {
		// if (!inetAddress.isLoopbackAddress()
		// && InetAddressUtils.isIPv4Address(inetAddress.getHostAddress())) {
		// return inetAddress;
		// }
		// }
		// }
		// } catch (SocketException ex) {
		// MLog.d(TAG, ex.toString());
		// }
		// return null;

	}

	// http://mrbool.com/how-to-work-with-java-mail-api-in-android/27800#ixzz2tulYAG00
	public static void sendEmail() throws AddressException, MessagingException {
		String host = "smtp.gmail.com";
		String address = "@gmail.com";
		String pass = "";

		String from = "@gmail.com";
		String to = "@gmail.com";

		Multipart multiPart;
		String finalString = "";

		Properties props = System.getProperties();
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", host);
		props.put("mail.smtp.user", address);
		props.put("mail.smtp.password", pass);
		props.put("mail.smtp.port", "587");
		props.put("mail.smtp.auth", "true");
		Log.i("Check", "done pops");
		Session session = Session.getDefaultInstance(props, null);
		DataHandler handler = new DataHandler(new ByteArrayDataSource(finalString.getBytes(), "text/plain"));
		MimeMessage message = new MimeMessage(session);
		message.setFrom(new InternetAddress(from));
		message.setDataHandler(handler);
		Log.i("Check", "done sessions");

		multiPart = new MimeMultipart();

		InternetAddress toAddress;
		toAddress = new InternetAddress(to);
		message.addRecipient(Message.RecipientType.TO, toAddress);
		Log.i("Check", "added recipient");
		message.setSubject("Send Auto-Mail");
		message.setContent(multiPart);
		message.setText("Demo For Sending Mail in Android Automatically");

		Log.i("check", "transport");
		Transport transport = session.getTransport("smtp");
		Log.i("check", "connecting");
		transport.connect(host, address, pass);
		Log.i("check", "wana send");
		transport.sendMessage(message, message.getAllRecipients());
		transport.close();

		Log.i("check", "sent");

	}

}
