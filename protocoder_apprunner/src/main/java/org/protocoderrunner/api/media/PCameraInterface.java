package org.protocoderrunner.api.media;


import org.protocoderrunner.api.common.ReturnInterface;
import org.protocoderrunner.base.gui.CameraNew;

public interface PCameraInterface {

    void takePicture(String file, final ReturnInterface callbackfn);
    void recordVideo(String file);
    void stopRecordingVideo();
    void focus(CameraNew.FocusCB callback);
    void setPreviewSize(int w, int h);
    void setPictureResolution(int w, int h);
    boolean isFlashAvailable();
    void turnOnFlash(boolean b);
    void setColorEffect(String effect);
    void onNewBitmap(final CameraNew.CallbackBmp callbackfn);
    void onNewStreamFrame(CameraNew.CallbackStream callbackfn);

}
