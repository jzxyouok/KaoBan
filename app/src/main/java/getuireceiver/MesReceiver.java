package getuireceiver;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.dezhou.lsy.projectdezhoureal.R;
import com.igexin.sdk.PushConsts;
import com.igexin.sdk.PushManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fragment.MessageFragment;

/**
 * Created by zy on 15-4-16.
 */
public class MesReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle bundle = intent.getExtras();

        Log.d("cout", "recevier...................");
        Log.d("cout", "bundle get int......" + bundle.getInt(PushConsts.CMD_ACTION));
        Log.d("cout", "push consts......" + PushConsts.CMD_ACTION);

        switch (bundle.getInt(PushConsts.CMD_ACTION)) {

            case PushConsts.GET_SDKSERVICEPID:
                byte[] payload1 = bundle.getByteArray("payload");

                String taskid1 = bundle.getString("taskid");
                String messageid1 = bundle.getString("messageid");

                // smartPush第三方回执调用接口，actionid范围为90000-90999，可根据业务场景执行
                boolean result1 = PushManager.getInstance().sendFeedbackMessage(context, taskid1, messageid1, 90001);

                if (payload1 != null) {
                    String data = new String(payload1);

                    Log.d("cout", "Got Payload:" + data);
                }
                break;

            case PushConsts.GET_MSG_DATA: //接受透传消息
                // 获取透传数据
                // String appid = bundle.getString("appid");
                byte[] payload = bundle.getByteArray("payload");

                String taskid = bundle.getString("taskid");
                String messageid = bundle.getString("messageid");

                Log.d("cout", "case receiver ...................");

                // smartPush第三方回执调用接口，actionid范围为90000-90999，可根据业务场景执行
                boolean result = PushManager.getInstance().sendFeedbackMessage(context, taskid, messageid, 90001);

                if (payload != null) {
                    String data = new String(payload);
                    List<Map<String,Object>> list = new ArrayList<>();
                    Map<String,Object> map = new HashMap<>();
                    map.put("name","new sender");
                    map.put("mes",data);
                    map.put("headImg",R.drawable.headimg);
                    list.add(map);

                    Log.d("cout", "Got Payload:" + data);
                }
                break;
            default:
                break;
        }
    }
}
