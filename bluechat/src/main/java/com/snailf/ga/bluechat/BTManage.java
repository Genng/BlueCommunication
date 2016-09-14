package com.snailf.ga.bluechat;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class BTManage {

    //	private List<BTItem> mListDeviceBT=null;
    private BluetoothAdapter mBtAdapter =null;
    private static BTManage mag=null;

    private BTManage(){
//		mListDeviceBT=new ArrayList<BTItem>();
        mBtAdapter=BluetoothAdapter.getDefaultAdapter();
    }

    public static BTManage getInstance(){
        if(null==mag)
            mag=new BTManage();
        return mag;
    }

    private StatusBlueTooth blueStatusLis=null;
    public void setBlueListner(StatusBlueTooth blueLis){
        this.blueStatusLis=blueLis;
    }

    public BluetoothAdapter getBtAdapter(){
        return this.mBtAdapter;
    }

    public void openBluetooth(Activity activity){
        if(null==mBtAdapter){	////Device does not support Bluetooth
            AlertDialog.Builder dialog = new AlertDialog.Builder(activity);
            dialog.setTitle("No bluetooth devices");
            dialog.setMessage("Your equipment does not support bluetooth, please change device");

            dialog.setNegativeButton("cancel",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
            dialog.show();
            return;
        }
        // If BT is not on, request that it be enabled.
        if (!mBtAdapter.isEnabled()) {
            /*Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            activity.startActivityForResult(enableIntent, 3);*/
            mBtAdapter.enable();
        }
    }

    public void closeBluetooth(){
        if(mBtAdapter.isEnabled())
            mBtAdapter.disable();
    }

    public boolean isDiscovering(){
        return mBtAdapter.isDiscovering();
    }

    public void scanDevice(){
//		mListDeviceBT.clear();
        if(!mBtAdapter.isDiscovering())
            mBtAdapter.startDiscovery();
    }

    public void cancelScanDevice(){
        if(mBtAdapter.isDiscovering())
            mBtAdapter.cancelDiscovery();
    }

    public void registerBluetoothReceiver(Context mcontext){
        // Register for broadcasts when start bluetooth search
        IntentFilter startSearchFilter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        mcontext.registerReceiver(mBlueToothReceiver, startSearchFilter);
        // Register for broadcasts when a device is discovered
        IntentFilter discoveryFilter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        mcontext.registerReceiver(mBlueToothReceiver, discoveryFilter);
        // Register for broadcasts when discovery has finished
        IntentFilter foundFilter = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        mcontext.registerReceiver(mBlueToothReceiver, foundFilter);

        IntentFilter connFilter = new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED);
        mcontext.registerReceiver(mBlueToothReceiver, connFilter);

        IntentFilter disconnFilter = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        mcontext.registerReceiver(mBlueToothReceiver, disconnFilter);

        IntentFilter stateFilter = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        mcontext.registerReceiver(mBlueToothReceiver, stateFilter);

        IntentFilter connectedFilter = new IntentFilter(BluetoothAdapter.ACTION_CONNECTION_STATE_CHANGED);

        mcontext.registerReceiver(mBlueToothReceiver, connectedFilter);


    }

    public void unregisterBluetooth(Context mcontext){
        cancelScanDevice();
        mcontext.unregisterReceiver(mBlueToothReceiver);
    }

    public List<BTItem> getPairBluetoothItem(){
        List<BTItem> mBTitemList=null;
        // Get a set of currently paired devices
        Set<BluetoothDevice> pairedDevices = mBtAdapter.getBondedDevices();
        Iterator<BluetoothDevice> it=pairedDevices.iterator();
        while(it.hasNext()){
            if(mBTitemList==null)
                mBTitemList=new ArrayList<BTItem>();

            BluetoothDevice device=it.next();
            BTItem item=new BTItem();
            item.setBuletoothName(device.getName());
            item.setBluetoothAddress(device.getAddress());
            item.setBluetoothType(BluetoothDevice.BOND_BONDED);
            mBTitemList.add(item);
        }
        return mBTitemList;
    }


    private final BroadcastReceiver mBlueToothReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            Log.d("Blue",action);

            if(BluetoothAdapter.ACTION_DISCOVERY_STARTED.equals(action)) {
                if(blueStatusLis!=null)
                    blueStatusLis.BTDeviceSearchStatus(StatusBlueTooth.SEARCH_START);
            }
            else if (BluetoothDevice.ACTION_FOUND.equals(action)){
                // When discovery finds a device
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // If it's already paired, skip it, because it's been listed already
                if (device.getBondState() != BluetoothDevice.BOND_BONDED) {
                    BTItem item=new BTItem();
                    item.setBuletoothName(device.getName());
                    item.setBluetoothAddress(device.getAddress());
                    item.setBluetoothType(device.getBondState());

                    if(blueStatusLis!=null)
                        blueStatusLis.BTSearchFindItem(item);
                    //              	mListDeviceBT.add(item);
                }
            }
            else if (BluetoothAdapter.ACTION_DISCOVERY_FINISHED.equals(action)){
                // When discovery is finished, change the Activity title
                if(blueStatusLis!=null)
                    blueStatusLis.BTDeviceSearchStatus(StatusBlueTooth.SEARCH_END);
            }

            if (BluetoothDevice.ACTION_ACL_CONNECTED == action) {
                Log.d("Blue",action);
            }
            if (BluetoothDevice.ACTION_ACL_DISCONNECTED == action) {
                Log.d("Blue",action);
            }
            if (BluetoothAdapter.ACTION_STATE_CHANGED == action) {
                Log.d("Blue",action);
            }
        }
    };


}