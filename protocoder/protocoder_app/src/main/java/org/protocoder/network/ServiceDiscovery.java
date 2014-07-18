package org.protocoder.network;

import android.app.Activity;
import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.util.Log;

import org.protocoder.apprunner.AppRunnerActivity;
import org.protocoder.utils.MLog;

import java.net.InetAddress;

/**
 * Created by victormanueldiazbarrales on 18/07/14.
 */
public class ServiceDiscovery {

    private static final String TAG = "ServiceDiscovery";

    public Create create(AppRunnerActivity a, String serviceName, String serviceType, int port, CreateCB callbackfn) {
        return new Create(a, serviceName, serviceType, port, callbackfn);
    }

    public Discover discover(AppRunnerActivity a, String serviceType, DiscoverCB callbackfn) {
        return new Discover(a, serviceType, callbackfn);
    }


    // --------- addGenericButton ---------//
    public interface CreateCB {
        void event(String mServiceName, String registered);
    }

    public class Create {

        private final NsdManager mNsdManager;
        private NsdManager.RegistrationListener mRegistrationListener;
        public String mServiceName;

        Create(Activity a, String name, String serviceType, int port, final CreateCB callbackfn) {
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
                    // resolve a conflict, so update the name you initially requested
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

    // --------- addGenericButton ---------//
    public interface DiscoverCB {
        void event(String mServiceName, NsdServiceInfo serviceInfo);
    }

    public class Discover {
        final NsdManager mNsdManager;
        NsdManager.DiscoveryListener mDiscoveryListener;

        Discover(Activity a, final String serviceType, final DiscoverCB callbackfn) {

            mNsdManager = (NsdManager) a.getSystemService(Context.NSD_SERVICE);

            // Instantiate a new DiscoveryListener
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
