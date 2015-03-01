package org.kentsigsafe.kentnfctest;

import android.nfc.tech.IsoDep;
import android.util.Log;
import java.io.IOException;

public class NfcApi {
    private static final int TIMEOUT = 30000;

    private IsoDep mIsoDep;

    public NfcApi() {}

    public NfcApi(IsoDep isoDep) {
        setIsoDep(isoDep);
    }

    public void setIsoDep(IsoDep isoDep) {
        mIsoDep = isoDep;
        mIsoDep.setTimeout(30000);
    }

    public void connect() throws IOException {
        mIsoDep.connect();
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
                (byte) 0xFF // max respone length
        };
        byte[] result = mIsoDep.transceive(ECHO);
        return result;
    }
}
