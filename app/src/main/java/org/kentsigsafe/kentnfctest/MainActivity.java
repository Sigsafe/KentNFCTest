package org.kentsigsafe.kentnfctest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;


import android.nfc.*;
import android.nfc.tech.*;
import android.nfc.NfcAdapter.ReaderCallback;
import android.content.Intent;

import java.io.IOException;
import android.widget.TextView;


public class MainActivity extends ActionBarActivity implements ReaderCallback {

    private NfcAdapter mNfcAdapter;
    private SigsafeNfcConnection mSigsafeNfcConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // listen for messages to show in the main view
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver,
                new IntentFilter("nfc-output"));

        // check for NFC
        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);

        if (mNfcAdapter == null) {
            handleNFCUnsupported();
            return;
        }

        if (!mNfcAdapter.isEnabled()) {
            handleNFCDisabled();
            return;
        }

        // put NFC tag into reader mode, as workaround for android bug - THIS WILL DISABLE NDEF intents!
        this.enableReaderMode(true);

        this.showMessage("Tap SigSafe");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        this.enableReaderMode(true);
    }
    @Override
    public void onPause() {
        super.onPause();
        this.enableReaderMode(false);
    }

    // fired directly from foreground dispatch
    @Override
    public void onTagDiscovered(Tag tag) {

        IsoDep nfcTech = IsoDep.get(tag);
        mSigsafeNfcConnection = new SigsafeNfcConnection(nfcTech);
        for(int i = 0; i < 1000000; i++) {
            byte[] result = mSigsafeNfcConnection.echo();
            String strResult = this.byteArrayToHex(result);
            this.broadcastMessage(strResult + " (" + i + ")");
        }
    }

    private void handleNFCUnsupported() {
        this.showMessage("NFC Unsupported");
    }

    private void handleNFCDisabled() {
        this.showMessage("NFC Disabled");
    }

    @Override
    protected void onDestroy() {
        // Unregister broadcast receiver
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
        super.onDestroy();
    }


    private void enableReaderMode(boolean enable) {
        if(enable) {
            Bundle options = new Bundle();
            options.putInt(NfcAdapter.EXTRA_READER_PRESENCE_CHECK_DELAY, 10000);
            mNfcAdapter.enableReaderMode(this, this, NfcAdapter.FLAG_READER_NFC_A | NfcAdapter.FLAG_READER_SKIP_NDEF_CHECK | NfcAdapter.FLAG_READER_NO_PLATFORM_SOUNDS, options);
        }
        else
            mNfcAdapter.disableReaderMode(this);
    }

    // handler for "msg-output" events
    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String msg = intent.getStringExtra("message");
            MainActivity.this.showMessage(msg);
        }
    };

    private void broadcastMessage(String msg) {
        Intent intent = new Intent("nfc-output");
        intent.putExtra("message", msg);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void showMessage(String msg) {
        TextView tv = (TextView) findViewById(R.id.main_output);
        tv.setText(msg);
    }

    private String byteArrayToHex(byte[] a) {
        StringBuilder sb = new StringBuilder(a.length * 2);
        for(byte b: a)
            sb.append(String.format("%02x", b & 0xff));
        return sb.toString();
    }
}
