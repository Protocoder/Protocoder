package org.protocoderrunner.apprunner.api.other;

import android.util.Log;

import org.apache.commons.net.ftp.*;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;

//current source :http://androiddev.orkitra.com/?p=28

public class PFtpClient {

    public static String workDir;

    private FTPClient mFTPClient;
    String TAG = "PFtpClient";
    Boolean isConnected = false;

    public PFtpClient() {

    }

    public boolean ftpConnect(String host, String username, String password, int port)
    {
        try {
            mFTPClient = new FTPClient();
            mFTPClient.connect(host, port);

            // check connection
            if (FTPReply.isPositiveCompletion(mFTPClient.getReplyCode())) {
                // login username & password
                boolean status = mFTPClient.login(username, password);

                mFTPClient.setFileType(FTP.BINARY_FILE_TYPE);
                mFTPClient.enterLocalPassiveMode();
                isConnected = true;
                return status;
            }
        } catch(Exception e) {
            Log.d(TAG, "connection failed error:" + e);
        }
        return false;
    }

    public void upload()
    {

    }

    public String ftpGetCurrentWorkingDirectory()
    {
        try {
            String workingDir = mFTPClient.printWorkingDirectory();
            return workingDir;
        } catch(Exception e) {
            Log.d(TAG, "Error: could not get current working directory.");
        }

        return null;
    }

    public boolean ftpChangeDirectory(String directory_path)
    {
        try {
            return mFTPClient.changeWorkingDirectory(directory_path);
        } catch(Exception e) {
            Log.d(TAG, "Error:" + e);
        }
        return false;
    }

    public ArrayList<String> getFileList(String dir_path)
    {
        ArrayList<String> list = new ArrayList<String>();
        try {
            FTPFile[] ftpFiles = mFTPClient.listFiles(dir_path);
            int length = ftpFiles.length;

            for (int i = 0; i < length; i++) {
                String name = ftpFiles[i].getName();
                boolean isFile = ftpFiles[i].isFile();

                if (isFile) {
                    Log.i(TAG, "File : " + name);
                    list.add("File: " + name);
                }
                else {
                    Log.i(TAG, "Directory : " + name);
                    list.add("Dir : " + name);
                }
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean download(String srcFilePath, String desFilePath)
    {
        boolean status = false;
        try {
            FileOutputStream desFileStream = new FileOutputStream(desFilePath);;
            status = mFTPClient.retrieveFile(srcFilePath, desFileStream);
            desFileStream.close();

            return status;
        } catch (Exception e) {
            Log.d(TAG, "download failed error:"+ e);
        }

        return status;
    }

    public boolean ftpUpload(String srcFilePath, String desFileName, String desDirectory)
    {
        boolean status = false;

        while (!isConnected)
        {
            try {
                Log.d(TAG,"waiting for connection");
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        try {
            Log.d(TAG,"Uploading File:"+ srcFilePath);
            Log.d(TAG,"upload to path:"+ ftpGetCurrentWorkingDirectory());
            FileInputStream srcFileStream = new FileInputStream(srcFilePath);
            status = mFTPClient.storeFile(desFileName, srcFileStream);
            srcFileStream.close();
            return status;
        } catch (Exception e) {
            Log.d(TAG, "upload failed" + e);
        }

        return status;
    }

    public void deleteFile(String file)
    {
        try {
            boolean status = mFTPClient.deleteFile(file);
            Log.e(TAG,file);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG,e+"");
            Log.e(TAG,file);
        }
    }

    public void disconnect()
    {
        try {
            mFTPClient.logout();
            mFTPClient.disconnect();
        }catch (Exception e)
        {

        }
        isConnected = false;
    }
}
