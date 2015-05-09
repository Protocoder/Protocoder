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

package org.protocoderrunner.apprunner.api;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.protocoderrunner.apidoc.annotation.ProtoMethod;
import org.protocoderrunner.apidoc.annotation.ProtoMethodParam;
import org.protocoderrunner.apprunner.AppRunnerFragment;
import org.protocoderrunner.apprunner.PInterface;
import org.protocoderrunner.apprunner.api.network.PBluetooth;
import org.protocoderrunner.apprunner.api.network.PBluetoothLe;
import org.protocoderrunner.apprunner.api.network.PFtpClient;
import org.protocoderrunner.apprunner.api.network.PFtpServer;
import org.protocoderrunner.apprunner.api.network.PMqtt;
import org.protocoderrunner.apprunner.api.network.PSocketIOClient;
import org.protocoderrunner.apprunner.api.network.PWebSocketClient;
import org.protocoderrunner.apprunner.api.network.PWebSocketServer;
import org.protocoderrunner.apprunner.api.network.PSimpleHttpServer;
import org.protocoderrunner.apprunner.api.other.WhatIsRunning;
import org.protocoderrunner.network.NetworkUtils;
import org.protocoderrunner.network.NetworkUtils.DownloadTask.DownloadListener;
import org.protocoderrunner.network.OSC;
import org.protocoderrunner.network.ServiceDiscovery;
import org.protocoderrunner.project.ProjectManager;
import org.protocoderrunner.utils.ExecuteCmd;
import org.protocoderrunner.utils.MLog;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

public class PNetwork extends PInterface {

    private final String TAG = PNetwork.class.getSimpleName();

    public PBluetooth bluetooth = null;
    private PWebSocketServer PWebsockerServer;

    public PNetwork(Context a) {
        super(a);

        bluetooth = new PBluetooth(a);

        WhatIsRunning.getInstance().add(this);
    }

    public void initForParentFragment(AppRunnerFragment fragment) {
        super.initForParentFragment(fragment);

        //prevent crashing in protocoder app
        MLog.d(TAG, "is getActivity() " + getActivity());

        if (getFragment() != null) {
            bluetooth.initForParentFragment(getFragment());
        }
    }

    // --------- download file ---------//
    interface downloadFileCB {
        void event(int eventType);
    }


    @ProtoMethod(description = "Downloads a file from a given Uri. Returns the progress", example = "")
    @ProtoMethodParam(params = {"url", "fileName", "function(progress)"})
    public void downloadFile(String url, String fileName, final downloadFileCB callbackfn) {

        NetworkUtils.DownloadTask downloadTask = new NetworkUtils.DownloadTask(getContext(), fileName);
        downloadTask.execute(url);
        downloadTask.addListener(new DownloadListener() {

            @Override
            public void onUpdate(int progress) {
                callbackfn.event(progress);
            }
        });

    }

    // @JavascriptInterface
    // @APIMethod(description = "", example = "")
    // @APIParam( params = {"file", "function()"} )
    public void isReachable(final String host, final String callbackfn) {

        Thread t = new Thread(new Runnable() {

            @Override
            public void run() {

                // doesnt work! isReachable
                //
                // try {
                // InetAddress in = InetAddress.getByName(host);
                // boolean isReacheable = in.isReachable(5000);
                // callback(callbackfn, isReacheable);
                // } catch (UnknownHostException e) {
                // e.printStackTrace();
                // } catch (IOException e) {
                // e.printStackTrace();
                // }

            }
        });
        t.start();

    }


    @ProtoMethod(description = "Check if internet connection is available", example = "")
    @ProtoMethodParam(params = {""})
    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    @ProtoMethod(description = "Returns the current device Ip address", example = "")
    @ProtoMethodParam(params = {""})
    public String ipAddress() {
        return NetworkUtils.getLocalIpAddress(getContext());
    }


    @ProtoMethod(description = "Get the wifi ap information", example = "")
    @ProtoMethodParam(params = {""})
    public WifiInfo wifiInfo() {
        return NetworkUtils.getWifiInfo(getContext());
    }


    @ProtoMethod(description = "Starts an OSC server", example = "")
    @ProtoMethodParam(params = {"port", "function(jsonData)"})
    public OSC.Server createOSCServer(String port) {
        OSC osc = new OSC();
        OSC.Server server = osc.new Server();

        server.start(port);
        WhatIsRunning.getInstance().add(server);

        return server;
    }


    @ProtoMethod(description = "Connects to a OSC server. Returns an object that allow sending messages", example = "")
    @ProtoMethodParam(params = {"address", "port"})
    public OSC.Client connectOSC(String address, int port) {
        OSC osc = new OSC();
        OSC.Client client = osc.new Client(address, port);
        WhatIsRunning.getInstance().add(client);

        return client;
    }


    WifiManager.MulticastLock wifiLock;


    @ProtoMethod(description = "Enable multicast networking", example = "")
    @ProtoMethodParam(params = {"boolean"})
    public void multicast(boolean b) {
        WifiManager wifi = (WifiManager) getContext().getSystemService(Context.WIFI_SERVICE);
        if (wifi != null) {
            if (b) {
                wifiLock = wifi.createMulticastLock("mylock");
                wifiLock.acquire();
            } else {
                wifiLock.release();
            }
        }
    }

    class MulticastEnabler {
        WifiManager.MulticastLock wifiLock;

        MulticastEnabler(boolean b) {
            WifiManager wifi = (WifiManager) getContext().getSystemService(Context.WIFI_SERVICE);
            if (wifi != null) {
                if (b) {
                    wifiLock = wifi.createMulticastLock("mylock");
                    wifiLock.acquire();
                    WhatIsRunning.getInstance().add(this);
                } else {
                    wifiLock.release();
                }
            }
        }

        public void stop() {
            if (wifiLock != null) {
                wifiLock.release();
            }
        }

    }


    @ProtoMethod(description = "Start a websocket server", example = "")
    @ProtoMethodParam(params = {"port", "function(status, socket, data)"})
    public PWebSocketServer createWebsocketServer(int port) {
        PWebSocketServer pWebSocketServer = new PWebSocketServer(port);

        return pWebSocketServer;
    }


    @ProtoMethod(description = "Connect to a websocket server", example = "")
    @ProtoMethodParam(params = {"uri", "function(status, data)"})
    public PWebSocketClient connectWebsocket(String uri) {
        PWebSocketClient pWebSocketClient = new PWebSocketClient(uri);

        return pWebSocketClient;
    }


    @ProtoMethod(description = "Connect to a SocketIO server", example = "")
    @ProtoMethodParam(params = {"uri", "function(status, message, data)"})
    public PSocketIOClient connectSocketIO(String uri) {
        PSocketIOClient socketIOClient = new PSocketIOClient(uri);

        return socketIOClient;
    }

    private class EmailConf {
        public String host;
        public String user;
        public String password;
        public String port;
        public String auth;
        public String ttl;
    }

    // public EmailConf emailSettings;


    @ProtoMethod(description = "Creates an object where to set the e-mail sending settings", example = "")
    @ProtoMethodParam(params = {"url", "function(data)"})
    public EmailConf createEmailSettings() {
        /*
		 * String host, String user, String pass, String iPort, String bAuth,
		 * String bTtl) {
		 * 
		 * emailSettings = new EmailConf(); emailSettings.host = host;
		 * emailSettings.user = user; emailSettings.password = pass;
		 * emailSettings.port = iPort; emailSettings.auth = bAuth;
		 * emailSettings.ttl = bTtl;
		 */
        return new EmailConf();
    }

    // http://mrbool.com/how-to-work-with-java-mail-api-in-android/27800#ixzz2tulYAG00

    @ProtoMethod(description = "Send an E-mail. It requires passing a EmailConf object", example = "")
    @ProtoMethodParam(params = {"url", "function(data)"})
    public void sendEmail(String from, String to, String subject, String text, final EmailConf emailSettings)
            throws AddressException, MessagingException {

        if (emailSettings == null) {
            return;
        }

        // final String host = "smtp.gmail.com";
        // final String address = "@gmail.com";
        // final String pass = "";

        Multipart multiPart;
        String finalString = "";

        Properties props = System.getProperties();
        props.put("mail.smtp.starttls.enable", emailSettings.ttl);
        props.put("mail.smtp.host", emailSettings.host);
        props.put("mail.smtp.user", emailSettings.user);
        props.put("mail.smtp.password", emailSettings.password);
        props.put("mail.smtp.port", emailSettings.port);
        props.put("mail.smtp.auth", emailSettings.auth);

        Log.i("Check", "done pops");
        final Session session = Session.getDefaultInstance(props, null);
        DataHandler handler = new DataHandler(new ByteArrayDataSource(finalString.getBytes(), "text/plain"));
        final MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(from));
        message.setDataHandler(handler);
        Log.i("Check", "done sessions");

        multiPart = new MimeMultipart();

        InternetAddress toAddress;
        toAddress = new InternetAddress(to);
        message.addRecipient(Message.RecipientType.TO, toAddress);
        Log.i("Check", "added recipient");
        message.setSubject(subject);
        message.setContent(multiPart);
        message.setText(text);

        Thread t = new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    //MLog.i("check", "transport");
                    Transport transport = session.getTransport("smtp");
                    //MLog.i("check", "connecting");
                    transport.connect(emailSettings.host, emailSettings.user, emailSettings.password);
                    //MLog.i("check", "wana send");
                    transport.sendMessage(message, message.getAllRecipients());
                    transport.close();
                    //MLog.i("check", "sent");
                } catch (AddressException e) {
                    e.printStackTrace();
                } catch (MessagingException e) {
                    e.printStackTrace();
                }

            }
        });
        t.start();

    }

    // --------- getRequest ---------//
    public interface HttpGetCB {
        void event(int eventType, String responseString);
    }


    @ProtoMethod(description = "Simple http get. It returns the data using the callback", example = "")
    @ProtoMethodParam(params = {"url", "function(eventType, responseString)"})
    public void httpGet(String url, final HttpGetCB callbackfn) {

        class RequestTask extends AsyncTask<String, String, String> {
            String responseString = null;

            @Override
            protected String doInBackground(String... uri) {
                HttpClient httpclient = new DefaultHttpClient();
                HttpResponse response;
                try {
                    URL url = new URL(uri[0]);
                    response = httpclient.execute(new HttpGet(url.toString()));
                    final StatusLine statusLine = response.getStatusLine();
                    if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        response.getEntity().writeTo(out);
                        out.close();
                        responseString = out.toString();
                    } else {
                        // Closes the connection.
                        response.getEntity().getContent().close();
                        throw new IOException(statusLine.getReasonPhrase());
                    }

                    mHandler.post(new Runnable() {

                        @Override
                        public void run() {
                            callbackfn.event(statusLine.getStatusCode(), responseString);
                        }
                    });

                } catch (ClientProtocolException e) {
                    MLog.e(TAG, e.toString());
                } catch (IOException e) {
                    MLog.e(TAG, e.toString());
                } finally {
                    MLog.e(TAG, "error");
                }
                return responseString;
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                // Do anything with response..
            }
        }

        MLog.d(TAG, "" + new RequestTask().execute(url));
    }

    // --------- postRequest ---------//
    interface HttpPostCB {
        void event(String string);
    }


    @ProtoMethod(description = "Simple http post request. It needs an object to be sent. If an element of the object contains the key file then it will try to upload the resource indicated in the value as Uri ", example = "")
    @ProtoMethodParam(params = {"url", "params", "function(responseString)"})
    public void httpPost(String url, Object object, final HttpPostCB callbackfn) {
        final HttpClient httpClient = new DefaultHttpClient();
        final HttpContext localContext = new BasicHttpContext();
        final HttpPost httpPost = new HttpPost(url);

        Gson g = new Gson();
        JsonArray q = g.toJsonTree(object).getAsJsonArray();

        MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

        for (int i = 0; i < q.size(); i++) {
            Set<Entry<String, JsonElement>> set = q.get(i).getAsJsonObject().entrySet();

            // go through elements
            String name = "";
            String content = "";
            String type = "";
            for (Object element : set) {
                Entry<String, JsonElement> entry = (Entry<String, JsonElement>) element;
                if (entry.getKey().equals("name")) {
                    name = entry.getValue().getAsString();
                } else if (entry.getKey().equals("content")) {
                    content = entry.getValue().getAsString();
                } else if (entry.getKey().equals("type")) {
                    type = entry.getValue().getAsString();
                }
            }

            // create the multipart
            if (type.contains("file")) {
                File f = new File(ProjectManager.getInstance().getCurrentProject().getStoragePath() + "/" + content);
                ContentBody cbFile = new FileBody(f);
                entity.addPart(name, cbFile);
            } else if (type.contains("text")) { // Normal string data
                try {
                    entity.addPart(name, new StringBody(content));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
        }

        // send
        httpPost.setEntity(entity);
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    HttpResponse response = httpClient.execute(httpPost, localContext);
                    callbackfn.event(response.getStatusLine().toString());
                } catch (ClientProtocolException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    //gives the url trying to access
    //if (url == "") {
    //} else {
    //server.serveFiles()
    //
    //}


    @ProtoMethod(description = "Simple http server, serving the content of the project folder", example = "")
    @ProtoMethodParam(params = {"port", "function(responseString)"})
    public PSimpleHttpServer createSimpleHttpServer(int port) {
        PSimpleHttpServer httpServer = null;
        try {
            httpServer = new PSimpleHttpServer(getContext(), port);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return httpServer;
    }



//       public PBluetooth createBluetoothSerialServer() {
//        PBluetooth pBluetooth = new PBluetooth(getActivity());
//        pBluetooth.initForParentFragment(getFragment());
//
//        pBluetooth.start();
//
//        return pBluetooth;
//    }
//
//
//    @ProtoMethod(description = "Start the bluetooth interface", example = "")
//    @ProtoMethodParam(params = {})
//    public PBluetooth connectBluetoothSerial() {
//        PBluetooth pBluetooth = new PBluetooth(getActivity());
//        pBluetooth.initForParentFragment(getFragment());
//
//        pBluetooth.start();
//
//        return pBluetooth;
//    }


    @ProtoMethod(description = "Enable/Disable the Wifi adapter", example = "")
    @ProtoMethodParam(params = {"boolean"})
    public void enableWifi(boolean enabled) {
        WifiManager wifiManager = (WifiManager) getContext().getSystemService(Context.WIFI_SERVICE);
        wifiManager.setWifiEnabled(enabled);
    }


    @ProtoMethod(description = "Check if the Wifi adapter is enabled", example = "")
    @ProtoMethodParam(params = {})
    public boolean isWifiEnabled() {
        WifiManager wifiManager = (WifiManager) getContext().getSystemService(Context.WIFI_SERVICE);
        return wifiManager.isWifiEnabled();
    }

    // http://stackoverflow.com/questions/8818290/how-to-connect-to-mContext-specific-wifi-network-in-android-programmatically

    @ProtoMethod(description = "Connect to mContext given Wifi network with mContext given 'wpa', 'wep', 'open' type and mContext password", example = "")
    @ProtoMethodParam(params = {"ssidName", "type", "password"})
    public void connectWifi(String networkSSID, String type, String networkPass) {

        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID = "\"" + networkSSID + "\""; // Please note the quotes. String
        // should contain ssid in quotes

        if (type.equals("wep")) {
            // wep
            conf.wepKeys[0] = "\"" + networkPass + "\"";
            conf.wepTxKeyIndex = 0;
            conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
        } else if (type.equals("wpa")) {
            // wpa
            conf.preSharedKey = "\"" + networkPass + "\"";
        } else if (type.equals("open")) {
            // open
            conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        }

        WifiManager wifiManager = (WifiManager) getContext().getSystemService(Context.WIFI_SERVICE);
        wifiManager.addNetwork(conf);

        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
        for (WifiConfiguration i : list) {
            if (i.SSID != null && i.SSID.equals("\"" + networkSSID + "\"")) {
                wifiManager.disconnect();
                wifiManager.enableNetwork(i.networkId, true);
                wifiManager.reconnect();

                break;
            }
        }

    }

    private Object mIsWifiAPEnabled = true;


    @ProtoMethod(description = "Enable/Disable mContext Wifi access point", example = "")
    @ProtoMethodParam(params = {"boolean, apName"})
    public void wifiAP(boolean enabled, String wifiName) {

        WifiManager wifi = (WifiManager) getContext().getSystemService(getContext().WIFI_SERVICE);
        Method[] wmMethods = wifi.getClass().getDeclaredMethods();
        Log.d(TAG, "enableMobileAP methods " + wmMethods.length);
        for (Method method : wmMethods) {
            Log.d(TAG, "enableMobileAP method.getName() " + method.getName());
            if (method.getName().equals("setWifiApEnabled")) {
                WifiConfiguration netConfig = new WifiConfiguration();
                netConfig.SSID = wifiName;
                netConfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
                netConfig.allowedProtocols.set(WifiConfiguration.Protocol.RSN);
                netConfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                netConfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);

                //
                try {
                    //MLog.d(TAG, "enableMobileAP try: ");
                    method.invoke(wifi, netConfig, enabled);
                    if (netConfig.wepKeys != null && netConfig.wepKeys.length >= 1) {
                        Log.d(TAG, "enableMobileAP key : " + netConfig.wepKeys[0]);
                    }
                    //MLog.d(TAG, "enableMobileAP enabled: ");
                    mIsWifiAPEnabled = enabled;
                } catch (Exception e) {
                    //MLog.e(TAG, "enableMobileAP failed: ", e);
                }
            }
        }
    }


    // --------- RegisterServiceCB ---------//
    public interface RegisterServiceCB {
        void event();
    }


    @ProtoMethod(description = "Register mContext discovery service", example = "")
    @ProtoMethodParam(params = {"serviceName, serviceType, port, function(name, status)"})
    public void registerService(String serviceName, String serviceType, int port, ServiceDiscovery.CreateCB callbackfn) {
        ServiceDiscovery.Create rD = new ServiceDiscovery().create(getContext(), serviceName, serviceType, port, callbackfn);
        WhatIsRunning.getInstance().add(rD);
    }


    @ProtoMethod(description = "Discover services in the current network", example = "")
    @ProtoMethodParam(params = {"serviceType, function(name, jsonData)"})
    public void discoverServices(final String serviceType, ServiceDiscovery.DiscoverCB callbackfn) {
        ServiceDiscovery.Discover sD = new ServiceDiscovery().discover(getContext(), serviceType, callbackfn);
        WhatIsRunning.getInstance().add(sD);

    }

    public interface PingCallback {
        void event(float ms);
    }


    @ProtoMethod(description = "Ping mContext Ip address", example = "")
    @ProtoMethodParam(params = {"ip", "function(time)"})
    public void ping(final String where, final PingCallback callbackfn) {
       mHandler.post(new Runnable() {
           @Override
           public void run() {
               final Pattern pattern = Pattern.compile("time=(\\d.+)\\s*ms");
               final Matcher[] m = {null};

               new ExecuteCmd("/system/bin/ping -c 8 " + where, new ExecuteCmd.ExecuteCommandCB() {
                   @Override
                   public void event(String buffer) {
                       //MLog.d(TAG, pattern.toString() + "" + buffer);

                       m[0] = pattern.matcher(buffer);
                       if (m[0].find()) {
                           callbackfn.event(Float.parseFloat(m[0].group(1)));
                       } else {
                           callbackfn.event(-1);
                           //MLog.d(TAG, "" + -1);
                       }

                   }
               }).start();
           }
       });
    }


    @ProtoMethod(description = "Start a ftp server in the given port", example = "")
    @ProtoMethodParam(params = {"port", "function(activity)"})
    public PFtpServer createFtpServer(final int port, PFtpServer.FtpServerCb callback) {
        PFtpServer ftpServer = new PFtpServer(port, callback);

        return ftpServer;
    }


    @ProtoMethod(description = "Connect to ftp", example = "")
    @ProtoMethodParam(params = {})
    public PFtpClient createFtpConnection() {
        PFtpClient ftpClient = new PFtpClient(getContext());

        return ftpClient;
    }
    @ProtoMethod(description = "Initialize Bluetooth Low Energy System")
    @ProtoMethodParam(params = {})
    public PBluetoothLe startBLE() {

        //PNetwork p=null;
        //p.startBLE();

        Log.i("PROTOBLE","STARTING BLE");
        PBluetoothLe pBluetoothLe = new PBluetoothLe(getContext());

        return pBluetoothLe;
    }


    @ProtoMethod(description = "Connect to a MQTT service", example = "")
    @ProtoMethodParam(params = {})
    public PMqtt createMqttClient() {
        PMqtt pMqtt = new PMqtt(getContext());

        return pMqtt;
    }


    public void stop() {

    }


}