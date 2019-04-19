package im.zego.publishstreamtest.cls;

import android.content.SharedPreferences;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.zego.zegoliveroom.ZegoLiveRoom;
import com.zego.zegoliveroom.callback.IZegoInitSDKCompletionCallback;
import com.zego.zegoliveroom.callback.IZegoLivePublisherCallback;
import com.zego.zegoliveroom.callback.IZegoLoginCompletionCallback;
import com.zego.zegoliveroom.entity.AuxData;
import com.zego.zegoliveroom.entity.ZegoPublishStreamQuality;
import com.zego.zegoliveroom.entity.ZegoStreamInfo;

import java.util.Date;
import java.util.HashMap;

import im.zego.publishstreamtest.R;
import im.zego.publishstreamtest.ZegoApplication;
import im.zego.publishstreamtest.activities.MainActivity;
import im.zego.publishstreamtest.zgUtils.ZegoAppInfoUtil;

public class MainWindow {

    private EditText et_zegoSDKAppid;
    private EditText et_zegoSDKAppKey;
    private CheckBox cb_isTestEnv;
    private CheckBox cb_isPublishStream;
    private CheckBox cd_isAdaptSystemEarLoopback;
    private CheckBox cd_isSDKLoopback;

    private Button bt_StartTest;
    private TextureView tv_Preview;
    private LinearLayout ll_rootlayout;
    private MainActivity sMainActivity;
    private SharedPreferences sSharedPreferences;
    private SharedPreferences.Editor spEditor;

    private boolean bt_StartTest_flag = false;
    private ZegoLiveRoom zg = ZegoSDKSingleton.getInstance();


    public MainWindow(MainActivity mainActivity, SharedPreferences sharedPreferences){

        this.sMainActivity = mainActivity;

        this.cb_isTestEnv = mainActivity.findViewById(R.id.cd_isTestEnv);
        this.cb_isPublishStream = mainActivity.findViewById(R.id.cd_isPublishStream);
        this.cd_isAdaptSystemEarLoopback = mainActivity.findViewById(R.id.cd_isAdaptSystemEarLoopback);
        this.cd_isSDKLoopback = mainActivity.findViewById(R.id.cd_isSDKLoopback);
        this.et_zegoSDKAppid = mainActivity.findViewById(R.id.et_zegoSDKAppid);
        this.et_zegoSDKAppKey = mainActivity.findViewById(R.id.et_zegoSDKAppKey);
        this.bt_StartTest = mainActivity.findViewById(R.id.bt_StartTest);
        this.ll_rootlayout = mainActivity.findViewById(R.id.ll_rootlayout);

        this.sSharedPreferences = sharedPreferences;
        this.spEditor = this.sSharedPreferences.edit();

        this.et_zegoSDKAppid.setText(sharedPreferences.getString("appid",""));
        this.et_zegoSDKAppKey.setText(sharedPreferences.getString("appKey",""));
        this.cb_isTestEnv.setChecked(sharedPreferences.getBoolean("isTestEnv",false));
        this.cb_isPublishStream.setChecked(sharedPreferences.getBoolean("isPublishStream",false));
        this.cd_isAdaptSystemEarLoopback.setChecked(sharedPreferences.getBoolean("isAdaptSystemEarLoopback",false));
        this.cd_isSDKLoopback.setChecked(sharedPreferences.getBoolean("isSDKLoopback",false));

        bt_StartTest.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                bt_StartTest_onclick(v);
            }
        });

    }

    /**
     * 点击按钮测试的执行入口
     * @param v
     */
    private void bt_StartTest_onclick(View v){
        if(!bt_StartTest_flag){
            bt_StartTest.setText("停止测试");
            bt_StartTest_flag = true;

            if(sMainActivity.isPermissionGranted()){
                //启动测试
                this.startTest();

                //启动测试之后，使控件不可用，因为测试过程更改控件内容无意义
                this.et_zegoSDKAppid.setEnabled(false);
                this.et_zegoSDKAppKey.setEnabled(false);
                this.cb_isTestEnv.setEnabled(false);
                this.cb_isPublishStream.setEnabled(false);
                this.cd_isAdaptSystemEarLoopback.setEnabled(false);
                this.cd_isSDKLoopback.setEnabled(false);

            }else{
                Toast.makeText(ZegoApplication.getZeoApplicaton(), "没有对应权限", Toast.LENGTH_LONG).show();
            }

        }else{
            bt_StartTest.setText("启动测试");
            bt_StartTest_flag = false;
            //停止测试
            this.stopTest();

            //停止测试之后，使控件可用，恢复更改控件的内容
            this.et_zegoSDKAppid.setEnabled(true);
            this.et_zegoSDKAppKey.setEnabled(true);
            this.cb_isTestEnv.setEnabled(true);
            this.cb_isPublishStream.setEnabled(true);
            this.cd_isAdaptSystemEarLoopback.setEnabled(true);
            this.cd_isSDKLoopback.setEnabled(true);
        }
    }

    /**
     * 开始测试的入口
     */
    private void startTest(){

        String appid = et_zegoSDKAppid.getText().toString().trim();
        String appKey = et_zegoSDKAppKey.getText().toString().trim();

        tv_Preview = new TextureView(sMainActivity);
        tv_Preview.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT));
        ll_rootlayout.addView(tv_Preview,-1);

        if(cb_isTestEnv.isChecked()){
            ZegoLiveRoom.setTestEnv(true);
            this.spEditor.putBoolean("isTestEnv",true);
        }else{
            ZegoLiveRoom.setTestEnv(false);
            this.spEditor.putBoolean("isTestEnv",false);
        }


        if(0 == appid.length()){
            Toast.makeText(ZegoApplication.getZeoApplicaton(), "Appid is null", Toast.LENGTH_LONG).show();
            return ;
        }else if(0 == appKey.length()){
            Toast.makeText(ZegoApplication.getZeoApplicaton(), "App Sign Key Illegal", Toast.LENGTH_LONG).show();
            return ;
        }

        this.spEditor.putString("appid",appid);
        this.spEditor.putString("appKey",appKey);


        if(cd_isAdaptSystemEarLoopback.isChecked()){
            ZegoLiveRoom.setConfig("adapt_to_system_karaoke=true");
            this.spEditor.putBoolean("isAdaptSystemEarLoopback",true);
        }else{
            ZegoLiveRoom.setConfig("adapt_to_system_karaoke=false");
            this.spEditor.putBoolean("isAdaptSystemEarLoopback",false);
        }
        this.spEditor.commit();


        //每次释放SDK之后，回调需重新设置，否则下次无回调
        ZegoApplication.getZeoApplicaton().setZegoSDKCallback(zg);

        /*
         * 登陆房间并推流
         */
        zg.initSDK(ZegoAppInfoUtil.convertAppidToLong(appid),ZegoAppInfoUtil.convertAppKeyToByteArray(appKey),new IZegoInitSDKCompletionCallback() {

            public void onInitSDK(int errorCode){

            if(errorCode == 0){
                _test_Login();
            }else {
                Toast.makeText(ZegoApplication.getZeoApplicaton(), "initSDK error:"+errorCode, Toast.LENGTH_LONG).show();
            }
            }

        });



    }

    private void _test_Login(){


        String curTime =  new Date().getTime() + "";
        String roomid = "rid-" + curTime;
        String roomname = "rname-" + curTime;
        String streamid = "sid-" + curTime;
        String streamname = "sname-" + curTime;

        zg.loginRoom(roomid, roomname, 1, new IZegoLoginCompletionCallback() {
            @Override
            public void onLoginCompletion(int i, ZegoStreamInfo[] zegoStreamInfos) {
                if (i == 0){
                    Toast.makeText(ZegoApplication.getZeoApplicaton(), "登陆房间成功", Toast.LENGTH_LONG).show();
                }else {
                    Toast.makeText(ZegoApplication.getZeoApplicaton(), "登陆房间失败", Toast.LENGTH_LONG).show();

                }
            }
        });

        zg.setPreviewView(MainWindow.this.tv_Preview);
        zg.setPreviewViewMode(1);
        zg.startPreview();

        if(cd_isSDKLoopback.isChecked()){
            zg.enableLoopback(true);
            this.spEditor.putBoolean("isSDKLoopback",true);
            this.spEditor.commit();
        }else{
            zg.enableLoopback(false);
            this.spEditor.putBoolean("isSDKLoopback",false);
            this.spEditor.commit();
        }

        if(cb_isPublishStream.isChecked()){
            zg.startPublishing(streamid,streamname,0);
            this.spEditor.putBoolean("isPublishStream",true);
            this.spEditor.commit();
        }else{
            this.spEditor.putBoolean("isPublishStream",false);
            this.spEditor.commit();
        }



    }

    private void stopTest(){

        final ZegoLiveRoom zg = ZegoSDKSingleton.getInstance();
        zg.setPreviewView(null);
        zg.stopPublishing();
        zg.stopPreview();
        zg.unInitSDK();

        ll_rootlayout.removeView(tv_Preview);

        tv_Preview = null;

    }
}
