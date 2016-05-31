package com.dezhou.lsy.projectdezhoureal;

import android.media.AudioManager;
import android.media.RingtoneManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.applib.controller.HXSDKHelper;
import com.easemob.chat.EMCallStateChangeListener;
import com.easemob.chat.EMChatManager;
import com.easemob.exceptions.EMServiceNotReadyException;

import java.util.UUID;

/**
 *
 */
public class VoiceCallActivity extends CallActivity implements OnClickListener {
    private LinearLayout comingBtnContainer;
    private Button hangupBtn;
    private Button refuseBtn;
    private Button answerBtn;
    private ImageView muteImage;
    private ImageView handsFreeImage;

    private boolean isMuteState;
    private boolean isHandsfreeState;

    private TextView callStateTextView;
    private int streamID;
    private boolean endCallTriggerByMe = false;
    private Handler handler = new Handler();
    private TextView nickTextView;
    private TextView durationTextView;
    private Chronometer chronometer;
    String st1;
    private boolean isAnswered;
    private LinearLayout voiceContronlLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState != null){
            finish();
            return;
        }
        setContentView(R.layout.activity_voice_call);

        HXSDKHelper.getInstance().isVoiceCalling = true;

        comingBtnContainer = (LinearLayout) findViewById(R.id.ll_coming_call);
        refuseBtn = (Button) findViewById(R.id.btn_refuse_call);
        answerBtn = (Button) findViewById(R.id.btn_answer_call);
        hangupBtn = (Button) findViewById(R.id.btn_hangup_call);
        muteImage = (ImageView) findViewById(R.id.iv_mute);
        handsFreeImage = (ImageView) findViewById(R.id.iv_handsfree);
        callStateTextView = (TextView) findViewById(R.id.tv_call_state);
        nickTextView = (TextView) findViewById(R.id.tv_nick);
        durationTextView = (TextView) findViewById(R.id.tv_calling_duration);
        chronometer = (Chronometer) findViewById(R.id.chronometer);
        voiceContronlLayout = (LinearLayout) findViewById(R.id.ll_voice_control);

        refuseBtn.setOnClickListener(this);
        answerBtn.setOnClickListener(this);
        hangupBtn.setOnClickListener(this);
        muteImage.setOnClickListener(this);
        handsFreeImage.setOnClickListener(this);

        getWindow().addFlags(
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD
                        | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        addCallStateListener();
        msgid = UUID.randomUUID().toString();

        username = getIntent().getStringExtra("username");
        isInComingCall = getIntent().getBooleanExtra("isComingCall", false);

        nickTextView.setText(username);
        if (!isInComingCall) {// ����绰
            soundPool = new SoundPool(1, AudioManager.STREAM_RING, 0);
            outgoing = soundPool.load(this, R.raw.outgoing, 1);

            comingBtnContainer.setVisibility(View.INVISIBLE);
            hangupBtn.setVisibility(View.VISIBLE);
            st1 = getResources().getString(R.string.Are_connected_to_each_other);
            callStateTextView.setText(st1);
            handler.postDelayed(new Runnable() {
                public void run() {
                    streamID = playMakeCallSounds();
                }
            }, 300);
            try {
                EMChatManager.getInstance().makeVoiceCall(username);
            } catch (EMServiceNotReadyException e) {
                e.printStackTrace();
                final String st2 = getResources().getString(R.string.Is_not_yet_connected_to_the_server);
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(VoiceCallActivity.this, st2, 0).show();
                    }
                });
            }
        } else {
            voiceContronlLayout.setVisibility(View.INVISIBLE);
            Uri ringUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            audioManager.setMode(AudioManager.MODE_RINGTONE);
            audioManager.setSpeakerphoneOn(true);
            ringtone = RingtoneManager.getRingtone(this, ringUri);
            ringtone.play();
        }
    }

    /**
     */
    void addCallStateListener() {
        callStateListener = new EMCallStateChangeListener() {

            @Override
            public void onCallStateChanged(CallState callState, CallError error) {
                // Message msg = handler.obtainMessage();
                switch (callState) {

                    case CONNECTING:
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                // TODO Auto-generated method stub
                                callStateTextView.setText(st1);
                            }

                        });
                        break;
                    case CONNECTED:
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                // TODO Auto-generated method stub
                                String st3 = getResources().getString(R.string.have_connected_with);
                                callStateTextView.setText(st3);
                            }

                        });
                        break;

                    case ACCEPTED:
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                try {
                                    if (soundPool != null)
                                        soundPool.stop(streamID);
                                } catch (Exception e) {
                                }
                                if(!isHandsfreeState)
                                    closeSpeakerOn();
                                ((TextView)findViewById(R.id.tv_is_p2p)).setText(EMChatManager.getInstance().isDirectCall()
                                        ? R.string.direct_call : R.string.relay_call);
                                chronometer.setVisibility(View.VISIBLE);
                                chronometer.setBase(SystemClock.elapsedRealtime());
                                chronometer.start();
                                String str4 = getResources().getString(R.string.In_the_call);
                                callStateTextView.setText(str4);
                                callingState = CallingState.NORMAL;
                            }

                        });
                        break;
                    case DISCONNNECTED:
                        final CallError fError = error;
                        runOnUiThread(new Runnable() {
                            private void postDelayedCloseMsg() {
                                handler.postDelayed(new Runnable() {

                                    @Override
                                    public void run() {
                                        saveCallRecord(0);
                                        Animation animation = new AlphaAnimation(1.0f, 0.0f);
                                        animation.setDuration(800);
                                        findViewById(R.id.root_layout).startAnimation(animation);
                                        finish();
                                    }

                                }, 200);
                            }

                            @Override
                            public void run() {
                                chronometer.stop();
                                callDruationText = chronometer.getText().toString();
                                String st2 = getResources().getString(R.string.The_other_party_refused_to_accept);
                                String st3 = getResources().getString(R.string.Connection_failure);
                                String st4 = getResources().getString(R.string.The_other_party_is_not_online);
                                String st5 = getResources().getString(R.string.The_other_is_on_the_phone_please);

                                String st6 = getResources().getString(R.string.The_other_party_did_not_answer_new);
                                String st7 = getResources().getString(R.string.hang_up);
                                String st8 = getResources().getString(R.string.The_other_is_hang_up);

                                String st9 = getResources().getString(R.string.did_not_answer);
                                String st10 = getResources().getString(R.string.Has_been_cancelled);
                                String st11 = getResources().getString(R.string.hang_up);

                                if (fError == CallError.REJECTED) {
                                    callingState = CallingState.BEREFUESD;
                                    callStateTextView.setText(st2);
                                } else if (fError == CallError.ERROR_TRANSPORT) {
                                    callStateTextView.setText(st3);
                                } else if (fError == CallError.ERROR_INAVAILABLE) {
                                    callingState = CallingState.OFFLINE;
                                    callStateTextView.setText(st4);
                                } else if (fError == CallError.ERROR_BUSY) {
                                    callingState = CallingState.BUSY;
                                    callStateTextView.setText(st5);
                                } else if (fError == CallError.ERROR_NORESPONSE) {
                                    callingState = CallingState.NORESPONSE;
                                    callStateTextView.setText(st6);
                                } else {
                                    if (isAnswered) {
                                        callingState = CallingState.NORMAL;
                                        if (endCallTriggerByMe) {
//                                        callStateTextView.setText(st7);
                                        } else {
                                            callStateTextView.setText(st8);
                                        }
                                    } else {
                                        if (isInComingCall) {
                                            callingState = CallingState.UNANSWERED;
                                            callStateTextView.setText(st9);
                                        } else {
                                            if (callingState != CallingState.NORMAL) {
                                                callingState = CallingState.CANCED;
                                                callStateTextView.setText(st10);
                                            }else {
                                                callStateTextView.setText(st11);
                                            }
                                        }
                                    }
                                }
                                postDelayedCloseMsg();
                            }

                        });

                        break;

                    default:
                        break;
                }

            }
        };
        EMChatManager.getInstance().addCallStateChangeListener(callStateListener);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_refuse_call:
                refuseBtn.setEnabled(false);
                if (ringtone != null)
                    ringtone.stop();
                try {
                    EMChatManager.getInstance().rejectCall();
                } catch (Exception e1) {
                    e1.printStackTrace();
                    saveCallRecord(0);
                    finish();
                }
                callingState = CallingState.REFUESD;
                break;

            case R.id.btn_answer_call:
                answerBtn.setEnabled(false);
                if (ringtone != null)
                    ringtone.stop();
                if (isInComingCall) {
                    try {
                        callStateTextView.setText("���ڽ���...");
                        EMChatManager.getInstance().answerCall();
                        isAnswered = true;
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                        saveCallRecord(0);
                        finish();
                        return;
                    }
                }
                comingBtnContainer.setVisibility(View.INVISIBLE);
                hangupBtn.setVisibility(View.VISIBLE);
                voiceContronlLayout.setVisibility(View.VISIBLE);
                closeSpeakerOn();
                break;

            case R.id.btn_hangup_call:
                hangupBtn.setEnabled(false);
                if (soundPool != null)
                    soundPool.stop(streamID);
                chronometer.stop();
                endCallTriggerByMe = true;
                callStateTextView.setText(getResources().getString(R.string.hanging_up));
                try {
                    EMChatManager.getInstance().endCall();
                } catch (Exception e) {
                    e.printStackTrace();
                    saveCallRecord(0);
                    finish();
                }
                break;

            case R.id.iv_mute:
                if (isMuteState) {
                    muteImage.setImageResource(R.drawable.icon_mute_normal);
                    audioManager.setMicrophoneMute(false);
                    isMuteState = false;
                } else {
                    muteImage.setImageResource(R.drawable.icon_mute_on);
                    audioManager.setMicrophoneMute(true);
                    isMuteState = true;
                }
                break;
            case R.id.iv_handsfree:
                if (isHandsfreeState) {
                    handsFreeImage.setImageResource(R.drawable.icon_speaker_normal);
                    closeSpeakerOn();
                    isHandsfreeState = false;
                } else {
                    handsFreeImage.setImageResource(R.drawable.icon_speaker_on);
                    openSpeakerOn();
                    isHandsfreeState = true;
                }
                break;
            default:
                break;
        }
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        HXSDKHelper.getInstance().isVoiceCalling = false;
    }

    @Override
    public void onBackPressed() {
        EMChatManager.getInstance().endCall();
        callDruationText = chronometer.getText().toString();
        saveCallRecord(0);
        finish();
    }






}
