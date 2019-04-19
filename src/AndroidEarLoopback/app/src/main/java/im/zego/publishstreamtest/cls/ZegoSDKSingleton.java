package im.zego.publishstreamtest.cls;

import com.zego.zegoliveroom.ZegoLiveRoom;

/**
 * ZegoSDK的单例，引用SDK的实例
 */
public class ZegoSDKSingleton {


    private static ZegoLiveRoom zg;

    /**
     * SDK单例构造器
     */
    private ZegoSDKSingleton(){

    }

    /**
     * 获取SDK的实例
     * @return ZegoLiveRoom
     */
    public static ZegoLiveRoom getInstance(){
        
        if(zg == null){
            zg = new ZegoLiveRoom();
        }
        return zg;
    }


}
