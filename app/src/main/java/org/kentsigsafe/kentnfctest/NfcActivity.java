package org.kentsigsafe.kentnfctest;

import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;


import android.nfc.*;
import android.nfc.tech.*;
import android.nfc.NfcAdapter.ReaderCallback;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import java.io.IOException;
import android.util.Log;
import android.widget.TextView;

public class NfcActivity extends Activity {

    private NfcAdapter mNfcAdapter;
    private NfcApi mNfcApi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        mNfcApi = new NfcApi();

        if (mNfcAdapter == null) {
            handleNFCUnsupported();
            return;
        }

        if (!mNfcAdapter.isEnabled()) {
            handleNFCDisabled();
            return;
        }

        handleIntent(getIntent());
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

    // fired directly from foreground dispatch
    public void onTagDiscovered(Tag tag) {

        IsoDep isoDep = IsoDep.get(tag);
        try {
            isoDep.connect();
            mNfcApi.setIsoDep(isoDep);
            byte[] result = mNfcApi.echo();
        }
        catch(IOException e){
            //this.showMessage("IO Exception");
        }
    }

    // fired from background dispatch
    private void handleIntent(Intent intent) {
       String action = intent.getAction();
        //if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(action)) {
        if (NfcAdapter.ACTION_TAG_DISCOVERED.equals(action)) {
            Tag tag = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
            String[] techList = tag.getTechList();
            String searchedTech = IsoDep.class.getName();

            for (String tech : techList) {
                if (searchedTech.equals(tech)) {
                   this.onTagDiscovered(tag);
                }
            }
        }
    }

    private void showMessage(String msg) {
        Intent intent = new Intent("nfc-output");
        intent.putExtra("message", msg);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void handleNFCUnsupported() {
        this.showMessage("NFC Unsupported");
    }

    private void handleNFCDisabled() {
        this.showMessage("NFC Disabled");
    }
}
