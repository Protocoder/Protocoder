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

package org.protocoderrunner.network;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.os.Build;
import android.util.Log;

import org.protocoderrunner.apprunner.AppRunnerFragment;
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
            NsdServiceInfo serviceInfo  = new NsdServiceInfo();

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
