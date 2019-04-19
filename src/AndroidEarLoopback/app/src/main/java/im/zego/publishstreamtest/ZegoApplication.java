package im.zego.publishstreamtest;

import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import im.zego.publishstreamtest.cls.ZegoSDKSingleton;

import com.zego.zegoliveroom.ZegoLiveRoom;
import com.zego.zegoliveroom.callback.IZegoLivePublisherCallback;
import com.zego.zegoliveroom.entity.AuxData;
import com.zego.zegoliveroom.entity.ZegoPublishStreamQuality;

import java.util.HashMap;

public class ZegoApplication extends Application {

    private static ZegoApplication sInstance;

    private static Context context;

    private ZegoLiveRoom zgSDK = ZegoSDKSingleton.getInstance();

    @Override
    public void onCreate() {
        super.onCreate();

        context = getApplicationContext();
        sInstance = this;

        this.configZegoSDK();


        this.setZegoSDKCallback(zgSDK);

    }


    /**
     * 配置SDK的基本参数
     */
    private void configZegoSDK(){

        ZegoLiveRoom.SDKContext sdkContext = new ZegoLiveRoom.SDKContextEx() {
            @Override
            public long getLogFileSize() {
                return 10 * 1024 * 1024;
            }
            @Nullable
            @Override
            public String getSoFullPath() {
                return null;
            }
            @Nullable
            @Override
            public String getLogPath() {
                return null;
            }
            @NonNull
            @Override
            public Application getAppContext() {
                return ZegoApplication.sInstance;
            }
        };
        ZegoLiveRoom.setSDKContext(sdkContext);
        ZegoLiveRoom.setUser("zego","Hansons");

    }

    /**
     * 设置相关ZegoLiveRoom对象的回调
     * @param zgSDK
     */
    public void setZegoSDKCallback(ZegoLiveRoom zgSDK){


        /**
         * 设置推流回调
         */
        zgSDK.setZegoLivePublisherCallback(new IZegoLivePublisherCallback() {
            @Override
            public void onPublishStateUpdate(int i, String s, HashMap<String, Object> hashMap) {

                if(i == 0) {
                    Toast.makeText(ZegoApplication.getZeoApplicaton(), "推流成功", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(ZegoApplication.getZeoApplicaton(), "推流失败", Toast.LENGTH_LONG).show();

                }
            }

            @Override
            public void onJoinLiveRequest(int i, String s, String s1, String s2) {

            }

            @Override
            public void onPublishQualityUpdate(String s, ZegoPublishStreamQuality zegoPublishStreamQuality) {

            }

            @Override
            public AuxData onAuxCallback(int i) {
                return null;
            }

            @Override
            public void onCaptureVideoSizeChangedTo(int i, int i1) {

            }

            @Override
            public void onMixStreamConfigUpdate(int i, String s, HashMap<String, Object> hashMap) {

            }

            @Override
            public void onCaptureVideoFirstFrame() {

            }
        });


    }


    public static ZegoApplication getZeoApplicaton() {
        return ZegoApplication.sInstance;
    }

    public static Context getZegoAppContext() {
        return context;
    }
}
