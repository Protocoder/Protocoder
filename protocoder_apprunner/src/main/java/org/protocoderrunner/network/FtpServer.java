package org.protocoderrunner.network;

import android.content.Context;

import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.Authority;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.UserManager;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.PropertiesUserManagerFactory;
import org.apache.ftpserver.usermanager.SaltedPasswordEncryptor;
import org.apache.ftpserver.usermanager.impl.BaseUser;
import org.apache.ftpserver.usermanager.impl.WritePermission;
import org.protocoderrunner.apprunner.PInterface;
import org.protocoderrunner.utils.MLog;

import java.util.ArrayList;
import java.util.List;


public class FtpServer {

    final String TAG = "FtpServer";
    private final int mPort;
    //private final String mUserName;
    //private final String mPassword;

    PropertiesUserManagerFactory userManagerFactory;
    UserManager um;
    org.apache.ftpserver.FtpServer server;

    public FtpServer(int port) {
        mPort = port;
        //mUserName = userName;
        //mPassword = password;
        
        userManagerFactory = new PropertiesUserManagerFactory();
        //userManagerFactory.setAdminName(mUserName);
        userManagerFactory.setPasswordEncryptor(new SaltedPasswordEncryptor());

        um = userManagerFactory.createUserManager();

        //startServer();
    }

    public void addUser(String name, String pass, String root, boolean canWrite) {
        BaseUser user = new BaseUser();
        user.setName(name);
        user.setPassword(pass);
        user.setHomeDirectory(root);

        //check if user can write
        if(canWrite)
        {
            List<Authority> auths = new ArrayList<Authority>();
            Authority auth = new WritePermission();
            auths.add(auth);
            user.setAuthorities(auths);
        }

        try {
            um.save(user);
        } catch (FtpException e) {
            e.printStackTrace();
        }

    }


    public void start() {
        FtpServerFactory serverFactory = new FtpServerFactory();
        ListenerFactory factory = new ListenerFactory();
        factory.setPort(mPort);

        // replace the default listener
        serverFactory.addListener("default", factory.createListener());
        serverFactory.setUserManager(um);

        // start the server
        try {
            server = serverFactory.createServer();
            server.start();
            MLog.d(TAG, "server started");
        } catch (FtpException e) {
            e.printStackTrace();
            MLog.d(TAG, "server not started");
        }
    }

    public void stop() {
        try{
            server.stop();
        }catch (Exception e)
        {
            MLog.d(TAG, e.toString());
        }
    }
}
