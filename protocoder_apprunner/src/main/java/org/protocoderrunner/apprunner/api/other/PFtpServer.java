package org.protocoderrunner.apprunner.api.other;

import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.Authority;
import org.apache.ftpserver.ftplet.DefaultFtplet;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.ftplet.FtpReply;
import org.apache.ftpserver.ftplet.FtpRequest;
import org.apache.ftpserver.ftplet.FtpSession;
import org.apache.ftpserver.ftplet.FtpletContext;
import org.apache.ftpserver.ftplet.FtpletResult;
import org.apache.ftpserver.ftplet.UserManager;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.PropertiesUserManagerFactory;
import org.apache.ftpserver.usermanager.SaltedPasswordEncryptor;
import org.apache.ftpserver.usermanager.impl.BaseUser;
import org.apache.ftpserver.usermanager.impl.WritePermission;
import org.protocoderrunner.project.ProjectManager;
import org.protocoderrunner.utils.MLog;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PFtpServer {

    final String TAG = "PFtpServer";
    private final int mPort;
    private final FtpServerCb mCallback;
    //private final String mUserName;
    //private final String mPassword;

    PropertiesUserManagerFactory userManagerFactory;
    UserManager um;
    org.apache.ftpserver.FtpServer server;


    public interface FtpServerCb {
        public void event(String status);
    }

    public PFtpServer(int port, FtpServerCb callback) {
        mCallback = callback;
        mPort = port;
        //mUserName = userName;
        //mPassword = password;
        
        userManagerFactory = new PropertiesUserManagerFactory();
        //userManagerFactory.setAdminName(mUserName);
        userManagerFactory.setPasswordEncryptor(new SaltedPasswordEncryptor());

        um = userManagerFactory.createUserManager();

        WhatIsRunning.getInstance().add(this);
    }



    //we have to pass the protocoder project folder
    public void addUser(String name, String pass, String directory, boolean canWrite) {
        BaseUser user = new BaseUser();
        user.setName(name);
        user.setPassword(pass);

        //String root = ProjectManager.getInstance().getCurrentProject().getStoragePath() + "/" + directory;
        user.setHomeDirectory(directory);

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

        Map ftpLets = new HashMap<>();
        ftpLets.put("ftpLet", new CallbackFTP(mCallback));


        serverFactory.setFtplets(ftpLets);

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

    private class CallbackFTP extends DefaultFtplet {
        FtpServerCb callback;

        CallbackFTP(FtpServerCb callback) {
            this.callback = callback;
        }

        @Override
        public void init(FtpletContext ftpletContext) throws FtpException {

        }

        @Override
        public void destroy() {

        }

        @Override
        public FtpletResult beforeCommand(FtpSession ftpSession, FtpRequest ftpRequest) throws FtpException, IOException {
            if (callback != null)
                callback.event("Requested command: " + ftpRequest.getCommand() + " " + ftpRequest.getArgument() + " " + ftpRequest.getRequestLine());
            return FtpletResult.DEFAULT;        }

        @Override
        public FtpletResult afterCommand(FtpSession ftpSession, FtpRequest ftpRequest, FtpReply ftpReply) throws FtpException, IOException {
            return null;
        }

        @Override
        public FtpletResult onConnect(FtpSession ftpSession) throws FtpException, IOException {
            if (callback != null)
                callback.event("Connected from " + ftpSession.getClientAddress());
            return FtpletResult.DEFAULT;
        }

        @Override
        public FtpletResult onLogin(FtpSession session, FtpRequest request) throws FtpException, IOException {
            if (callback != null)
                callback.event("Logged in: " + session.getUser().getName());
            return FtpletResult.DEFAULT;
        }

        @Override
        public FtpletResult onDisconnect(FtpSession ftpSession) throws FtpException, IOException {
            if (callback != null)
                callback.event("Disconnected: " + ftpSession.getUser().getName());

            return FtpletResult.DEFAULT;
        }
    }
}
