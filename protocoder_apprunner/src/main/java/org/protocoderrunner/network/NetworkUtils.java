/*
* Part of Protocoder http://www.protocoder.org
* A prototyping platform for Android devices 
*
* Copyright (C) 2013 Victor Diaz Barrales victormdb@gmail.com
* 
* Protocoder is free software: you can redistribute it and/or modify
* it under the terms of the GNU Lesser General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
*
* Protocoder is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
* 
* You should have received a copy of the GNU Lesser General Public License
* along with Protocoder. If not, see <http://www.gnu.org/licenses/>.
*/

package org.protocoderrunner.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.text.format.Formatter;
import android.util.Log;

import org.protocoderrunner.apprunner.AppRunnerSettings;
import org.protocoderrunner.utils.MLog;

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
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
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
        String ipAddressString = "-1";
        if (ipAddress != 0) {

            // Convert little-endian to big-endianif needed
            if (ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN)) {
                ipAddress = Integer.reverseBytes(ipAddress);
            }

            byte[] ipByteArray = BigInteger.valueOf(ipAddress).toByteArray();


            try {
                ipAddressString = InetAddress.getByAddress(ipByteArray).getHostAddress();
            } catch (UnknownHostException ex) {
                Log.e("WIFIIP", "Unable to get host address.");
                ipAddressString = null;
            }
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

    public static WifiInfo getWifiInfo(Context c) {
        WifiManager wifiManager = (WifiManager) c.getSystemService(c.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();

        return wifiInfo;
    }

}
