package com.snailf.ga.bluechat.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.snailf.ga.bluechat.BTItem;
import com.snailf.ga.bluechat.BTManage;
import com.snailf.ga.bluechat.BluetoothMsg;
import com.snailf.ga.bluechat.R;
import com.snailf.ga.bluechat.StatusBlueTooth;


/**
 * Description
 * Date: 2016-09-09
 * Time: 17:11
 * User: Genng(genng1991@gmail.com)
 */
public class BTDeviceActivity extends Activity implements AdapterView.OnItemClickListener
        ,View.OnClickListener ,StatusBlueTooth {

    //  private List<BTItem> mListDeviceBT=new ArrayList<BTItem>();
    private ListView deviceListview;
    private Button btserch;
    private BTDeviceAdapter adapter;
    private boolean hasregister=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.finddevice);
        setView();

        BTManage.getInstance().setBlueListner(this);
    }

    private void setView(){
        deviceListview=(ListView)findViewById(R.id.devicelist);
        deviceListview.setOnItemClickListener(this);
        adapter=new BTDeviceAdapter(getApplicationContext());
        deviceListview.setAdapter(adapter);
        deviceListview.setOnItemClickListener(this);
        btserch=(Button)findViewById(R.id.start_seach);
        btserch.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //注册蓝牙接收广播
        if(!hasregister){
            hasregister=true;
            BTManage.getInstance().registerBluetoothReceiver(getApplicationContext());
        }
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(hasregister){
            hasregister=false;
            BTManage.getInstance().unregisterBluetooth(getApplicationContext());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {

        final BTItem item=(BTItem)adapter.getItem(position);


        AlertDialog.Builder dialog = new AlertDialog.Builder(this);// 定义一个弹出框对象
        dialog.setTitle("Confirmed connecting device");
        dialog.setMessage(item.getBuletoothName());
        dialog.setPositiveButton("connect",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //  btserch.setText("repeat search");
                        BTManage.getInstance().cancelScanDevice();
                        BluetoothMsg.BlueToothAddress=item.getBluetoothAddress();

                        if(BluetoothMsg.lastblueToothAddress!=BluetoothMsg.BlueToothAddress){
                            BluetoothMsg.lastblueToothAddress=BluetoothMsg.BlueToothAddress;
                        }
                        setResult(100);
                        finish();
                    }
                });
        dialog.setNegativeButton("cancel",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        BluetoothMsg.BlueToothAddress = null;
                    }
                });
        dialog.show();
    }

    @Override
    public void onClick(View v) {
        BTManage.getInstance().openBluetooth(this);

        if(BTManage.getInstance().isDiscovering()){
            BTManage.getInstance().cancelScanDevice();
            btserch.setText("start search");
        }else{
            BTManage.getInstance().scanDevice();
            btserch.setText("stop search");
        }
    }

    @Override
    public void BTDeviceSearchStatus(int resultCode) {
        switch(resultCode){
            case StatusBlueTooth.SEARCH_START:
                adapter.clearData();
                adapter.addDataModel(BTManage.getInstance().getPairBluetoothItem());
                break;
            case StatusBlueTooth.SEARCH_END:
                break;
        }
    }

    @Override
    public void BTSearchFindItem(BTItem item) {
        adapter.addDataModel(item);
    }

    @Override
    public void BTConnectStatus(int result) {

    }

}