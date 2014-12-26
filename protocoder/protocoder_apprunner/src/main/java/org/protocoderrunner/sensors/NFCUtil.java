package org.protocoderrunner.sensors;

import android.content.Context;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.Tag;
import android.nfc.TagLostException;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Locale;

public class NFCUtil {

	public static String nfcMsg = null;
	private NdefMessage messageToWrite;

	public static boolean writeTag(Context context, Tag tag, String data) {
		// Record to launch Play Store if app is not installed
		NdefRecord appRecord = NdefRecord.createApplicationRecord(context.getPackageName());

		// Record with actual data we care about
		NdefRecord relayRecord = new NdefRecord(NdefRecord.TNF_MIME_MEDIA, new String("application/"
				+ context.getPackageName()).getBytes(Charset.forName("US-ASCII")), null, data.getBytes());

		// Complete NDEF message with both records
		NdefMessage message = new NdefMessage(new NdefRecord[] { relayRecord, appRecord });

		try {
			// If the tag is already formatted, just write the message to it
			Ndef ndef = Ndef.get(tag);
			if (ndef != null) {
				ndef.connect();

				// Make sure the tag is writable
				if (!ndef.isWritable()) {
					// DialogUtils.displayErrorDialog(mainScriptContext,
					// R.string.nfcReadOnlyErrorTitle,
					// R.string.nfcReadOnlyError);
					return false;
				}

				// Check if there's enough space on the tag for the message
				int size = message.toByteArray().length;
				if (ndef.getMaxSize() < size) {
					// DialogUtils.displayErrorDialog(mainScriptContext,
					// R.string.nfcBadSpaceErrorTitle,
					// R.string.nfcBadSpaceError);
					return false;
				}

				try {
					// Write the data to the tag
					ndef.writeNdefMessage(message);

					// DialogUtils.displayInfoDialog(mainScriptContext,
					// R.string.nfcWrittenTitle, R.string.nfcWritten);
					return true;
				} catch (TagLostException tle) {
					// DialogUtils.displayErrorDialog(mainScriptContext,
					// R.string.nfcTagLostErrorTitle, R.string.nfcTagLostError);
					return false;
				} catch (IOException ioe) {
					// DialogUtils.displayErrorDialog(mainScriptContext,
					// R.string.nfcFormattingErrorTitle,
					// R.string.nfcFormattingError);
					return false;
				} catch (FormatException fe) {
					// DialogUtils.displayErrorDialog(mainScriptContext,
					// R.string.nfcFormattingErrorTitle,
					// R.string.nfcFormattingError);
					return false;
				}
				// If the tag is not formatted, format it with the message
			} else {
				NdefFormatable format = NdefFormatable.get(tag);
				if (format != null) {
					try {
						format.connect();
						format.format(message);

						// DialogUtils.displayInfoDialog(mainScriptContext,
						// R.string.nfcWrittenTitle, R.string.nfcWritten);
						return true;
					} catch (TagLostException tle) {
						// DialogUtils
						// .displayErrorDialog(mainScriptContext,
						// R.string.nfcTagLostErrorTitle,
						// R.string.nfcTagLostError);
						return false;
					} catch (IOException ioe) {
						// DialogUtils.displayErrorDialog(mainScriptContext,
						// R.string.nfcFormattingErrorTitle,
						// R.string.nfcFormattingError);
						return false;
					} catch (FormatException fe) {
						// DialogUtils.displayErrorDialog(mainScriptContext,
						// R.string.nfcFormattingErrorTitle,
						// R.string.nfcFormattingError);
						return false;
					}
				} else {
					// DialogUtils.displayErrorDialog(mainScriptContext,
					// R.string.nfcNoNdefErrorTitle, R.string.nfcNoNdefError);
					return false;
				}
			}
		} catch (Exception e) {
			// DialogUtils.displayErrorDialog(mainScriptContext,
			// R.string.nfcUnknownErrorTitle, R.string.nfcUnknownError);
		}

		return false;
	}

	/**
	 * Write text to mContext tag
	 * 
	 * @param textToWrite
	 *            the text to write
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
		NdefRecord[] records = { record };
		messageToWrite = new NdefMessage(records);
	}

}
