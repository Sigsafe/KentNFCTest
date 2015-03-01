package org.kentsigsafe.kentnfctest;

import android.nfc.tech.IsoDep;
import android.nfc.tech.NfcA;
import android.util.Log;
import java.io.IOException;

public class NfcApi {
    private static final int TIMEOUT = 30000;

    private NfcA mNfcTech;

    public NfcApi() {}

    public NfcApi(NfcA nfcTech) {
        setNfcTech(nfcTech);
    }

    public void setNfcTech(NfcA nfcTech) {
        mNfcTech = nfcTech;
        //mNfcTech.setTimeout(30000);
    }

    public void connect() throws IOException {
        mNfcTech.connect();
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
        byte[] result = mNfcTech.transceive(ECHO);
        return result;
    }
}
