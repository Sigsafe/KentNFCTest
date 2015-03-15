package org.kentsigsafe.kentnfctest;

import android.nfc.tech.IsoDep;
import android.nfc.tech.NfcA;
import android.util.Log;
import java.io.IOException;
import java.lang.InterruptedException;

public class NfcApi {
    private static final int TIMEOUT = 30000;
    private static final int MAX_RETRIES = 100;

    private IsoDep mNfcTech;

    public NfcApi() {}

    public NfcApi(IsoDep nfcTech) {
        setNfcTech(nfcTech);
    }

    public void setNfcTech(IsoDep nfcTech) {
      mNfcTech = nfcTech;
      mNfcTech.setTimeout(30000);
    }

    public void connect() throws IOException {
      mNfcTech.connect();
    }

    public void disconnect() throws IOException {
      mNfcTech.close();
    }

    public byte[] echo() throws IOException {

        byte[] ECHO = {
                (byte) 0xCC, // CLA: Instruction class
                (byte) 0x01, // OP Code
                (byte) 0x00, // P1: Argument 1
                (byte) 0x00, // P2: Argument 2
                (byte) 0x04, // byte length of payload
                (byte) 0x11,
                (byte) 0x22,
                (byte) 0xCA,
                (byte) 0xFE,
                (byte) 0xFF // max response length
        };
        return sendCommand(ECHO);
    }

    public byte[] sendCommand(byte[] payload) throws IOException {
        int retries = 0;
        byte[] result = null;

        boolean success = false;
        while (!success && retries++ < MAX_RETRIES){
            try {
                connect();
                result = mNfcTech.transceive(payload);

                // 0xB2009001 and 0x00b29001 are errors
                if (!(result == null || result[0] == 0xB2 || result[0] == 0x00))
                  success = true;
            } catch (IOException e) {
            }

            disconnect();
        }
        return result;
    }
}
