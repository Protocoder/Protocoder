package org.protocoderrunner.apprunner.api.sensors;

import android.content.Context;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.Tag;
import android.nfc.TagLostException;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;

import org.protocoderrunner.apidoc.annotation.ProtoMethod;
import org.protocoderrunner.apidoc.annotation.ProtoMethodParam;
import org.protocoderrunner.apprunner.PInterface;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Locale;

public class PNfc extends PInterface {

    public static String nfcMsg = null;
    private NdefMessage messageToWrite;
    private onNFCCB onNFCfn;

    public PNfc(Context context) {
        super(context);
    }


    // --------- onNFC ---------//
    interface onNFCCB {
        void event(String id, String responseString);
    }


    @ProtoMethod(description = "Gives back data when mContext NFC tag is approached", example = "")
    @ProtoMethodParam(params = {"function(id, data)"})
    public void onNewData(final onNFCCB fn) {

        getActivity().addNFCReadListener(new onNFCListener() {
            @Override
            public void onNewTag(String id, String data) {
                onNFCfn.event(id, data);
            }
        });

        getActivity().initializeNFC();

        onNFCfn = fn;
    }

    // --------- nfc ---------//
    interface writeNFCCB {
        void event(boolean b);
    }


    @ProtoMethod(description = "Write into mContext NFC tag the given text", example = "")
    @ProtoMethodParam(params = {"function()"})
    public void write(String data, final writeNFCCB fn) {
        PNfc.nfcMsg = data;
        getActivity().initializeNFC();

        getActivity().addNFCWrittenListener(new onNFCWrittenListener() {
            @Override
            public void onNewTag() {
                fn.event(true);
            }
        });

        // Construct the data to write to the tag
        // Should be of the form [relay/group]-[rid/gid]-[cmd]
        // String nfcMessage = data;

        // When an NFC tag comes into range, call the main activity which
        // handles writing the data to the tag
        // NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(mContext);

        // Intent nfcIntent = new Intent(mContext,
        // AppRunnerActivity.class).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        // nfcIntent.putExtra("nfcMessage", nfcMessage);
        // PendingIntent pi = PendingIntent.getActivity(mContext, 0, nfcIntent,
        // PendingIntent.FLAG_UPDATE_CURRENT);
        // IntentFilter tagDetected = new
        // IntentFilter(NfcAdapter.ACTION_TAG_DISCOVERED);

        // nfcAdapter.enableForegroundDispatch((Activity) mContext, pi, new
        // IntentFilter[] {tagDetected}, null);
    }

    public interface onNFCWrittenListener {
        public void onNewTag();
    }

    public interface onNFCListener {
        public void onNewTag(String id, String nfcMessage);
    }


    @ProtoMethod(description = "", example = "")
    @ProtoMethodParam(params = {"function(msg)"})
    public void nfcWrite(final onNFCCB fn) {
        getActivity().initializeNFC();

        onNFCfn = fn;
    }


    public static boolean writeTag(Context context, Tag tag, String data) {
        // Record to launch Play Store if app is not installed
        NdefRecord appRecord = NdefRecord.createApplicationRecord(context.getPackageName());

        // Record with actual data we care about
        NdefRecord relayRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA, new String("application/"
                + context.getPackageName()).getBytes(Charset.forName("US-ASCII")), null, data.getBytes());

        // Complete NDEF message with both records
        NdefMessage message = new NdefMessage(new NdefRecord[]{relayRecord, appRecord});

        try {
            // If the tag is already formatted, just write the message to it
            Ndef ndef = Ndef.get(tag);
            if (ndef != null) {
                ndef.connect();

                // Make sure the tag is writable
                if (!ndef.isWritable()) {
                    return false;
                }

                // Check if there's enough space on the tag for the message
                int size = message.toByteArray().length;
                if (ndef.getMaxSize() < size) {
                    return false;
                }

                try {
                    // Write the data to the tag
                    ndef.writeNdefMessage(message);
                    return true;
                } catch (TagLostException tle) {
                    return false;
                } catch (IOException ioe) {
                    return false;
                } catch (FormatException fe) {
                    return false;
                }
                // If the tag is not formatted, format it with the message
            } else {
                NdefFormatable format = NdefFormatable.get(tag);
                if (format != null) {
                    try {
                        format.connect();
                        format.format(message);
                        return true;
                    } catch (TagLostException tle) {
                        return false;
                    } catch (IOException ioe) {
                        return false;
                    } catch (FormatException fe) {
                        return false;
                    }
                } else {
                    return false;
                }
            }
        } catch (Exception e) {
        }

        return false;
    }

    /**
     * Write text to mContext tag
     *
     * @param textToWrite the text to write
     */
    public void write(String textToWrite) {

        Locale locale = Locale.US;
        final byte[] langBytes = locale.getLanguage().getBytes(Charset.forName("UTF-8"));
        final byte[] textBytes = textToWrite.getBytes(Charset.forName("UTF-8"));

        final int utfBit = 0;
        final char status = (char) (utfBit + langBytes.length);
        final byte[] data = new byte[1 + langBytes.length + textBytes.length];

        data[0] = (byte) status;
        System.arraycopy(langBytes, 0, data, 1, langBytes.length);
        System.arraycopy(textBytes, 0, data, 1 + langBytes.length, textBytes.length);

        NdefRecord record = new NdefRecord(NdefRecord.TNF_WELL_KNOWN, NdefRecord.RTD_TEXT, new byte[0], data);
        NdefRecord[] records = {record};
        messageToWrite = new NdefMessage(records);
    }

}
