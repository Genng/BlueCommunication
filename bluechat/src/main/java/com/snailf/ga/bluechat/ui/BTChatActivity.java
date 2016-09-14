package com.snailf.ga.bluechat.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.snailf.ga.bluechat.BTClient;
import com.snailf.ga.bluechat.BTManage;
import com.snailf.ga.bluechat.BTServer;
import com.snailf.ga.bluechat.BluetoothMsg;
import com.snailf.ga.bluechat.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Description
 * Date: 2016-09-09
 * Time: 16:59
 * User: Genng(genng1991@gmail.com)
 */
public class BTChatActivity extends Activity {

    private ListView mListView;
    private Button sendButton;
    private Button disconnectButton;
    private EditText editMsgView;
    private ArrayAdapter<String> mAdapter;
    private List<String> msgList=new ArrayList<String>();

    private BTClient client;
    private BTServer server;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bt_chat);
        initView();

    }

    private Handler detectedHandler = new Handler(){
        public void handleMessage(Message msg) {
            if(msg.what==11){
                if(null!=server){
                    server.closeBTServer();
                    server=null;
                }
                initServerStart();
            }
            msgList.add(msg.obj.toString());
            mAdapter.notifyDataSetChanged();
            mListView.setSelection(msgList.size() - 1);
        };
    };

    private void initServerStart(){

       new Thread(){
           @Override
           public void run() {
               super.run();
               try {
                   sleep(5000);
                   BluetoothMsg.isOpen = false;
                   BluetoothMsg.serviceOrCilent = BluetoothMsg.ServerOrCilent.SERVICE;
                   initConnecter();
               } catch (InterruptedException e) {
                   e.printStackTrace();
               }
           }
       }.start();

    }

    private void initView() {

        mAdapter=new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, msgList);
        mListView = (ListView) findViewById(R.id.list);
        mListView.setAdapter(mAdapter);
        mListView.setFastScrollEnabled(true);
        editMsgView= (EditText)findViewById(R.id.MessageText);
        editMsgView.clearFocus();

        RadioGroup group = (RadioGroup)this.findViewById(R.id.radioGroup);
        group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            //             @Override
//             public void onCheckedChanged(RadioGroup arg0, int arg1) {
//                 int radioId = arg0.getCheckedRadioButtonId();
//
//             }
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {

                if(checkedId==R.id.radio_none){
                    BluetoothMsg.serviceOrCilent = BluetoothMsg.ServerOrCilent.NONE;
                    if(null!=client){
                        client.closeBTClient();
                        client=null;
                    }
                    if(null!=server){
                        server.closeBTServer();
                        server=null;
                    }
                }else  if(checkedId==R.id.radio_client){
                    BluetoothMsg.serviceOrCilent = BluetoothMsg.ServerOrCilent.CILENT;
                    Intent it=new Intent(getApplicationContext(),BTDeviceActivity.class);
                    startActivityForResult(it, 100);
                }else if(checkedId==R.id.radio_server){
                    BluetoothMsg.serviceOrCilent = BluetoothMsg.ServerOrCilent.SERVICE;
                    initConnecter();
                }
            }
        });

        sendButton= (Button)findViewById(R.id.btn_msg_send);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {

                String msgText =editMsgView.getText().toString();
                if (msgText.length()>0) {
                    if (BluetoothMsg.serviceOrCilent == BluetoothMsg.ServerOrCilent.CILENT){
                        if(null==client)
                            return;
                        if(client.sendmsg(msgText)){
                            Message msg = new Message();
                            msg.obj = "send: "+msgText;
                            msg.what = 1;
                            detectedHandler.sendMessage(msg);
                        }else{
                            Message msg = new Message();
                            msg.obj = "send fail!! ";
                            msg.what = 1;
                            detectedHandler.sendMessage(msg);
                        }
                    }
                    else if (BluetoothMsg.serviceOrCilent == BluetoothMsg.ServerOrCilent.SERVICE) {
                        if(null==server)
                            return;
                        if(server.sendmsg(msgText)){
                            Message msg = new Message();
                            msg.obj = "send: "+msgText;
                            msg.what = 1;
                            detectedHandler.sendMessage(msg);
                        }else{
                            Message msg = new Message();
                            msg.obj = "send fail!! ";
                            msg.what = 1;
                            detectedHandler.sendMessage(msg);
                        }
                    }
                    editMsgView.setText("");
//                     editMsgView.clearFocus();
//                     //close InputMethodManager
//                     InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
//                     imm.hideSoftInputFromWindow(editMsgView.getWindowToken(), 0);
                }else{
                    Toast.makeText(getApplicationContext(), "发送内容不能为空！", Toast.LENGTH_SHORT).show();
                }
            }
        });

        disconnectButton= (Button)findViewById(R.id.btn_disconnect);
        disconnectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                if (BluetoothMsg.serviceOrCilent == BluetoothMsg.ServerOrCilent.CILENT){
                    if(null==client)
                        return;
                    client.closeBTClient();
                }
                else if (BluetoothMsg.serviceOrCilent == BluetoothMsg.ServerOrCilent.SERVICE) {
                    if(null==server)
                        return;
                    server.closeBTServer();
                }
                BluetoothMsg.isOpen = false;
                BluetoothMsg.serviceOrCilent=BluetoothMsg.ServerOrCilent.NONE;
                Toast.makeText(getApplicationContext(), "已断开连接！", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (BluetoothMsg.isOpen) {
            Toast.makeText(getApplicationContext(), "连接已经打开，可以通信。如果要再建立连接，请先断开！",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==100){
            //从设备列表返回
            initConnecter();
        }
    }

    private void initConnecter(){
        if (BluetoothMsg.serviceOrCilent == BluetoothMsg.ServerOrCilent.CILENT) {
            String address = BluetoothMsg.BlueToothAddress;
            if (!TextUtils.isEmpty(address)) {
                if(null==client)
                    client=new BTClient(BTManage.getInstance().getBtAdapter(), detectedHandler);
                client.connectBTServer(address);
                BluetoothMsg.isOpen = true;
            } else {
                Toast.makeText(getApplicationContext(), "address is empty please choose server address !",
                        Toast.LENGTH_SHORT).show();
            }
        } else if (BluetoothMsg.serviceOrCilent == BluetoothMsg.ServerOrCilent.SERVICE) {
            if(null==server)
                server=new BTServer(BTManage.getInstance().getBtAdapter(), detectedHandler);
            server.startBTServer();
            BluetoothMsg.isOpen = true;
        }
    }

}