package com.example.vivaaidemo.demo.presentation.demo.speech;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.MutableLiveData;

import com.example.vivaaidemo.demo.common.BaseViewModel;
import com.example.vivaaidemo.demo.common.Utility;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import vcc.viva.ai.bin.VccAiResult;
import vcc.viva.ai.bin.callback.VccAiRequestCallback;
import vcc.viva.ai.bin.entity.speech.SpeechToText;
import vcc.viva.ai.bin.entity.speech.TextToSpeech;
import vcc.viva.ai.bin.entity.speech.TtsOutput;
import vcc.viva.ai.bin.entity.speech.TtsRate;
import vcc.viva.ai.bin.entity.speech.TtsVoice;

public class SpeechViewModel extends BaseViewModel {
    /* **********************************************************************
     * Variable
     ********************************************************************** */
    private final MutableLiveData<SpeechMethod> type;
    private final MutableLiveData<String> message;

    private final MutableLiveData<TextToSpeech> tts;
    private final MutableLiveData<Boolean> ttsProgress;
    private String ttsUrl;
    private final MutableLiveData<Boolean> isPlayingTts;
    private final MutableLiveData<Integer> ttsPlayingDuration;
    private final MutableLiveData<Integer> ttsPlayingProgress;
    Timer ttsTimer;
    TimerTask ttsTimerTask;

    private final MutableLiveData<SpeechToText> stt;
    private final MutableLiveData<Boolean> sttProgress;
    private String sttUrl;
    private final MutableLiveData<Boolean> isPlayingStt;
    private final MutableLiveData<Integer> sttPlayingDuration;
    private final MutableLiveData<Integer> sttPlayingProgress;
    private final MutableLiveData<List<String>> permissions;
    private final List<String> requiredPermissions = new ArrayList<String>() {
        {
            add(Manifest.permission.READ_EXTERNAL_STORAGE);
            add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
    };
    Timer sttTimer;
    TimerTask sttTimerTask;

    private final MediaPlayer player;
    private final ExecutorService executor;

    final Handler handler = new Handler();

    /* **********************************************************************
     * Constructor
     ********************************************************************** */
    public SpeechViewModel() {
        type = new MutableLiveData<>(SpeechMethod.TEXT_TO_SPEECH);
        message = new MutableLiveData<>();

        tts = new MutableLiveData<>();
        ttsProgress = new MutableLiveData<>(false);
        isPlayingTts = new MutableLiveData<>(false);
        ttsPlayingDuration = new MutableLiveData<>(0);
        ttsPlayingProgress = new MutableLiveData<>(0);

        stt = new MutableLiveData<>();
        sttProgress = new MutableLiveData<>(false);
        isPlayingStt = new MutableLiveData<>(false);
        sttPlayingDuration = new MutableLiveData<>(0);
        sttPlayingProgress = new MutableLiveData<>(0);

        this.permissions = new MutableLiveData<>();

        player = new MediaPlayer();
        player.setLooping(false);
        player.setOnCompletionListener(MediaPlayer::reset);

        executor = Executors.newSingleThreadExecutor();
    }

    /* **********************************************************************
     * Function - Text to Speech
     ********************************************************************** */
    public MutableLiveData<TextToSpeech> getTextToSpeech() {
        return tts;
    }

    public MutableLiveData<Boolean> getTtsProgress() {
        return ttsProgress;
    }

    public MutableLiveData<Boolean> getPlayingTtsStatus() {
        return isPlayingTts;
    }

    public MutableLiveData<Integer> getTtsPlayingDuration() {
        return ttsPlayingDuration;
    }

    public MutableLiveData<Integer> getTtsPlayingProgress() {
        return ttsPlayingProgress;
    }

    public void textToSpeech(String text) {
        if (TextUtils.isEmpty(text)) {
            message.setValue("Please fill some text");
            return;
        }

        ttsProgress.postValue(true);
        manager.runBackground(() -> manager.speech().textToSpeech(new VccAiRequestCallback() {
            @Override
            public void success(VccAiResult result) {
                ttsProgress.postValue(false);
                try {
                    TextToSpeech data = (TextToSpeech) result.getData();
                    Log.d("tt", gson.toJson(data));
                    tts.postValue(data);

                    ttsUrl = data.path;
//                    play();
                    message.postValue("Convert Success");
                } catch (Exception e) {
                    message.postValue(e.getMessage());
                }
            }

            @Override
            public void fail(int code, String msg) {
                ttsProgress.postValue(false);
                message.postValue(String.format("Error[%s] : %s", code, msg));
            }
        }, text));
    }

    public void textToSpeech(Context context, String text, String voice, String rate, String output) {
        if (TextUtils.isEmpty(text)) {
            message.setValue("Please fill some text");
            return;
        }

        TtsVoice ttsVoice = null;
        TtsRate ttsRate = null;
        TtsOutput ttsOutput = null;
        for (TtsVoice ttsV : TtsVoice.values()) {
            if(context.getResources().getString(ttsV.getShowName()).equals(voice)){
                ttsVoice = ttsV;
            }
        }
        for (TtsRate ttsR : TtsRate.values()) {
            if(ttsR.getValue() == Integer.valueOf(rate)){
                ttsRate = ttsR;
            }
        }
        for (TtsOutput ttsO : TtsOutput.values()) {
            if(ttsO.getTag().equals(output)){
                ttsOutput = ttsO;
            }
        }
        TtsVoice finalTtsVoice = ttsVoice;
        TtsRate finalTtsRate = ttsRate;
        TtsOutput finalTtsOutput = ttsOutput;

        ttsProgress.postValue(true);

        manager.runBackground(() -> manager.speech().textToSpeech(new VccAiRequestCallback() {
            @Override
            public void success(VccAiResult result) {
                ttsProgress.postValue(false);
                try {
                    TextToSpeech data = (TextToSpeech) result.getData();
                    Log.d("tt", gson.toJson(data));
                    tts.postValue(data);

                    ttsUrl = data.path;
//                    play();
                    message.postValue("Convert Success");
                } catch (Exception e) {
                    message.postValue(e.getMessage());
                }
            }

            @Override
            public void fail(int code, String msg) {
                ttsProgress.postValue(false);
                message.postValue(String.format("Error[%s] : %s", code, msg));
            }
        }, text, finalTtsVoice, finalTtsRate, finalTtsOutput));
    }

    public void playTts() {
        play(ttsUrl, SpeechMethod.TEXT_TO_SPEECH);
    }

    public void startTtsTimer() {
        ttsTimer = new Timer();
        doTtsTimerTask();
        ttsTimer.schedule(ttsTimerTask, 0, 100);
    }

    public void doTtsTimerTask() {
        ttsTimerTask = new TimerTask() {
            public void run() {
                handler.post(() -> {
                    if (!player.isPlaying()) {
//                        ttsPlayingProgress.postValue(0);
                        stopTimerTask(ttsTimer);
                    } else {
                        ttsPlayingProgress.postValue(player.getCurrentPosition());
                        Log.d("timer", "run: " + player.getCurrentPosition());
                    }
                });
            }
        };
    }

    /* **********************************************************************
     * Function - Speech to Text
     ********************************************************************** */
    public MutableLiveData<SpeechToText> getSpeechToText() {
        return stt;
    }

    public MutableLiveData<Boolean> getSttProgress() {
        return sttProgress;
    }

    public MutableLiveData<Boolean> getPlayingSttStatus() {
        return isPlayingStt;
    }

    public MutableLiveData<Integer> getSttPlayingDuration() {
        return sttPlayingDuration;
    }

    public MutableLiveData<Integer> getSttPlayingProgress() {
        return sttPlayingProgress;
    }

    private boolean checkPermission(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            boolean isGrant = Environment.isExternalStorageManager();
            if (!isGrant) {
                permissions.postValue(new ArrayList<String>() {{
                    add(Manifest.permission.MANAGE_EXTERNAL_STORAGE);
                }});
                sttProgress.postValue(false);
            }
            return isGrant;
        } else {
            boolean result = true;
            List<String> missingPermission = new ArrayList<>();
            for (String permission : requiredPermissions) {
                boolean isGrant = ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
                result &= isGrant;
                if (!isGrant) {
                    missingPermission.add(permission);
                }
            }
            if (missingPermission.size() > 0) {
                permissions.postValue(missingPermission);
                sttProgress.postValue(false);
            }
            return result;
        }
    }

    public void speechToText(Context context, String link) {
        sttProgress.postValue(true);
        VccAiRequestCallback callback = new VccAiRequestCallback() {
            @Override
            public void success(VccAiResult result) {
                sttProgress.postValue(false);
                try {
                    SpeechToText data = (SpeechToText) result.getData();
                    Log.d("tt", gson.toJson(data));
                    sttUrl = link;
                    stt.postValue(data);
                    message.postValue("Convert Success");
                } catch (Exception e) {
                    message.setValue(e.getMessage());
                }
            }

            @Override
            public void fail(int code, String msg) {
                sttProgress.postValue(false);
                message.postValue(String.format("Error[%s] : %s", code, msg));
            }
        };

        if (Utility.isUrl(link)) {
            manager.runBackground(() -> manager.speech().speechToText(callback, link));
        } else {
            if(checkPermission(context)){
                manager.runBackground(() -> manager.speech().speechToTextLocal(callback, link));
            }
        }
    }

    public void playStt() {
        play(sttUrl, SpeechMethod.SPEECH_TO_TEXT);
    }

    public void startSttTimer() {
        sttTimer = new Timer();
        doSttTimerTask();
        sttTimer.schedule(sttTimerTask, 0, 100);
    }

    public void doSttTimerTask() {
        sttTimerTask = new TimerTask() {
            public void run() {
                handler.post(() -> {
                    if (!player.isPlaying()) {
//                        sttPlayingProgress.postValue(0);
                        stopTimerTask(sttTimer);
                    } else {
                        sttPlayingProgress.postValue(player.getCurrentPosition());
                        Log.d("stt", "run: " + sttPlayingProgress.getValue());
                    }
                });
            }
        };
    }

    /* **********************************************************************
     * Function
     ********************************************************************** */
    public MutableLiveData<SpeechMethod> getType() {
        return type;
    }

    public MutableLiveData<String> getMessage() {
        return message;
    }
    public MutableLiveData<List<String>> getPermissions() {
        return permissions;
    }

    public void changeType() {
        if (type.getValue() == SpeechMethod.SPEECH_TO_TEXT) {
            stopTimerTask(ttsTimer);
            type.setValue(SpeechMethod.TEXT_TO_SPEECH);
        } else {
            stopTimerTask(sttTimer);
            type.setValue(SpeechMethod.SPEECH_TO_TEXT);
        }

        player.reset();

        isPlayingTts.postValue(false);
        isPlayingStt.postValue(false);
    }

    private void play(String url, SpeechMethod method) {
        try {
            if (TextUtils.isEmpty(url)) {
                message.postValue("link not found");
                return;
            }

            executor.execute(() -> {
                try {
                    if (player.isPlaying()) {
                        player.pause();
                        switch (method) {
                            case SPEECH_TO_TEXT:
                                isPlayingStt.postValue(false);
                                break;
                            case TEXT_TO_SPEECH:
                                isPlayingTts.postValue(false);
                                break;
                        }
                        return;
                    } else if (!player.isPlaying() && player.getCurrentPosition() > 1) {
                        player.start();
                    } else {
                        player.reset();
                        player.setDataSource(url);
                        player.prepare();
                        player.start();
                    }

                    switch (method) {
                        case SPEECH_TO_TEXT:
                            isPlayingStt.postValue(true);
                            sttPlayingDuration.postValue(player.getDuration());
                            startSttTimer();
                            break;
                        case TEXT_TO_SPEECH:
                            isPlayingTts.postValue(true);
                            ttsPlayingDuration.postValue(player.getDuration());
                            startTtsTimer();
                            break;
                    }

                    player.setOnCompletionListener(mediaPlayer -> {
                        player.reset();
                        switch (method) {
                            case SPEECH_TO_TEXT:
                                isPlayingStt.postValue(false);
                                break;
                            case TEXT_TO_SPEECH:
                                isPlayingTts.postValue(false);
                                break;
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            message.postValue(e.getMessage());
        }
    }

    public void stopTimerTask(Timer timer) {
        if (timer != null) {
            timer.cancel();
        }
    }

    /* **********************************************************************
     * Inner Class
     ********************************************************************** */
    public enum SpeechMethod {SPEECH_TO_TEXT, TEXT_TO_SPEECH}
}
