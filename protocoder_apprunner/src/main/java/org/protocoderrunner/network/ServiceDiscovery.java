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

import android.annotation.TargetApi;
import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.os.Build;
import android.util.Log;

import org.protocoderrunner.utils.MLog;

import java.net.InetAddress;

@TargetApi(Build.VERSION_CODES.JELLY_BEAN)
public class ServiceDiscovery {

    private static final String TAG = "ServiceDiscovery";

    public Create create(Context a, String serviceName, String serviceType, int port, CreateCB callbackfn) {
        return new Create(a, serviceName, serviceType, port, callbackfn);
    }

    public Discover discover(Context a, String serviceType, DiscoverCB callbackfn) {
        return new Discover(a, serviceType, callbackfn);
    }


    // --------- CreateCB ---------//
    public interface CreateCB {
        void event(String mServiceName, String registered);
    }

    public class Create {

        private final NsdManager mNsdManager;
        private NsdManager.RegistrationListener mRegistrationListener;
        public String mServiceName;

        Create(Context a, String name, String serviceType, int port, final CreateCB callbackfn) {
            mServiceName = name;

            // Create the NsdServiceInfo object, and populate it.
            NsdServiceInfo serviceInfo = new NsdServiceInfo();

            // The name is subject to change based on conflicts
            // with other services advertised on the same network.
            serviceInfo.setServiceName(name);
            serviceInfo.setServiceType(serviceType);
            serviceInfo.setPort(port);

            mNsdManager = (NsdManager) a.getSystemService(Context.NSD_SERVICE);

            mRegistrationListener = new NsdManager.RegistrationListener() {


                @Override
                public void onServiceRegistered(NsdServiceInfo NsdServiceInfo) {
                    // Save the service name.  Android may have changed it in order to
                    // resolve mContext conflict, so update the name you initially requested
                    // with the name Android actually used.
                    mServiceName = NsdServiceInfo.getServiceName();
                    callbackfn.event(mServiceName, "registered");
                }

                @Override
                public void onRegistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
                    // Registration failed!  Put debugging code here to determine why.
                    callbackfn.event(mServiceName, "registration_failed");

                }

                @Override
                public void onServiceUnregistered(NsdServiceInfo arg0) {
                    // Service has been unregistered.  This only happens when you call
                    // NsdManager.unregisterService() and pass in this listener.
                    callbackfn.event(mServiceName, "unregistered");

                }

                @Override
                public void onUnregistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
                    // Unregistration failed.  Put debugging code here to determine why.
                    callbackfn.event(mServiceName, "unregistration_failed");
                }
            };

            mNsdManager.registerService(
                    serviceInfo, NsdManager.PROTOCOL_DNS_SD, mRegistrationListener);


        }

        public void stop() {
            mNsdManager.unregisterService(mRegistrationListener);
        }
    }

    // --------- DiscoverCB ---------//
    public interface DiscoverCB {
        void event(String mServiceName, NsdServiceInfo serviceInfo);
    }

    public class Discover {
        final NsdManager mNsdManager;
        NsdManager.DiscoveryListener mDiscoveryListener;

        Discover(Context a, final String serviceType, final DiscoverCB callbackfn) {

            mNsdManager = (NsdManager) a.getSystemService(Context.NSD_SERVICE);

            // Instantiate mContext new DiscoveryListener
            mDiscoveryListener = new NsdManager.DiscoveryListener() {

                //  Called as soon as service discovery begins.
                @Override
                public void onDiscoveryStarted(String regType) {
                    MLog.d(TAG, "Service discovery started");
                    callbackfn.event("start", null);

                }

                @Override
                public void onServiceFound(NsdServiceInfo serviceInfo) {
                    // A service was found!  Do something with it.

                    //mService = serviceInfo;
                    int port = serviceInfo.getPort();
                    String serviceName = serviceInfo.getServiceName();
                    InetAddress host = serviceInfo.getHost();

                    callbackfn.event("discovered", serviceInfo);

                }


                @Override
                public void onServiceLost(NsdServiceInfo service) {
                    // When the network service is no longer available.
                    // Internal bookkeeping code goes here.
                    Log.e(TAG, "service lost" + service);
                }

                @Override
                public void onDiscoveryStopped(String serviceType) {
                    Log.i(TAG, "Discovery stopped: " + serviceType);
                }

                @Override
                public void onStartDiscoveryFailed(String serviceType, int errorCode) {
                    Log.e(TAG, "Discovery failed: Error code:" + errorCode);
                    //mNsdManager.stopServiceDiscovery(this);
                }

                @Override
                public void onStopDiscoveryFailed(String serviceType, int errorCode) {
                    Log.e(TAG, "Discovery failed: Error code:" + errorCode);
                    //mNsdManager.stopServiceDiscovery(this);
                }
            };

            mNsdManager.discoverServices(serviceType, NsdManager.PROTOCOL_DNS_SD, mDiscoveryListener);
        }

        public void stop() {
            mNsdManager.stopServiceDiscovery(mDiscoveryListener);
        }
    }
}
