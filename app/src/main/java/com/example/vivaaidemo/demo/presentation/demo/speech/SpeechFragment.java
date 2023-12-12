package com.example.vivaaidemo.demo.presentation.demo.speech;

import static android.app.Activity.RESULT_OK;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.ListPopupWindow;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;
import com.example.vivaaidemo.BuildConfig;
import com.example.vivaaidemo.R;
import com.example.vivaaidemo.databinding.FragmentSpeechBinding;
import com.example.vivaaidemo.databinding.LayoutSpeechSttResultBinding;
import com.example.vivaaidemo.demo.common.BaseFragment;
import com.example.vivaaidemo.demo.common.FileUtil;
import com.example.vivaaidemo.demo.common.Utility;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.GsonBuilder;
import java.util.ArrayList;
import java.util.List;
import vcc.viva.ai.bin.entity.speech.SpeechToText;
import vcc.viva.ai.bin.entity.speech.TtsOutput;
import vcc.viva.ai.bin.entity.speech.TtsRate;
import vcc.viva.ai.bin.entity.speech.TtsVoice;

public class SpeechFragment extends BaseFragment<FragmentSpeechBinding> {
    /* **********************************************************************
     * Variable
     ********************************************************************** */
    private final int STT_REQ_CODE = 100;
    private final int STT_REQ_PERMISSION_BELOW_11_CODE = 300;
    private final int STT_REQ_PERMISSION_CODE = 301;
    private final String STT_AUDIO_TYPE = "audio/*";
    private final int logVisibility = BuildConfig.DEBUG ? View.VISIBLE : View.GONE;
    private SpeechViewModel viewModel;

    /* **********************************************************************
     * Constructor
     ********************************************************************** */
    public SpeechFragment() {
        super(FragmentSpeechBinding::inflate);
    }

    /* **********************************************************************
     * Lifecycle
     ********************************************************************** */
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        binding.swap.setOnClickListener(v -> viewModel.changeType());

        viewModel = new ViewModelProvider(requireActivity()).get(SpeechViewModel.class);
        viewModel.getMessage().observe(requireActivity(), message -> Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_SHORT).show());
        viewModel.getType().observe(requireActivity(), type -> {
            switch (type) {
                case SPEECH_TO_TEXT:
                    binding.from.setText(getResources().getString(R.string.speech));
                    binding.to.setText(getResources().getString(R.string.text));

                    binding.speechToText.getRoot().setVisibility(View.VISIBLE);
                    binding.textToSpeech.getRoot().setVisibility(View.INVISIBLE);
                    break;
                case TEXT_TO_SPEECH:
                default:
                    binding.from.setText(getResources().getString(R.string.text));
                    binding.to.setText(getResources().getString(R.string.speech));

                    binding.textToSpeech.getRoot().setVisibility(View.VISIBLE);
                    binding.speechToText.getRoot().setVisibility(View.INVISIBLE);
                    break;
            }
        });

        initTextToSpeech();
        initSpeechToText();
    }

    /* **********************************************************************
     * Function
     ********************************************************************** */
    private void initTextToSpeech() {
        binding.textToSpeech.textContent.setText("Bây giờ, là, 19 giờ");
        binding.textToSpeech.voiceContent.setText(TtsVoice.HN_MINH_PHUONG.getShowName());
        binding.textToSpeech.rateContent.setText(String.valueOf(TtsRate.RATE_22050.getValue()));
        binding.textToSpeech.outputContent.setText(TtsOutput.MP3.getTag());
        binding.textToSpeech.log.setVisibility(logVisibility);
        binding.textToSpeech.logTitle.setVisibility(logVisibility);

        List<String> voiceOption = new ArrayList<>();
        List<String> rateOption = new ArrayList<>();
        List<String> outputOption = new ArrayList<>();
        for (TtsVoice ttsVoice : TtsVoice.values()) {
            voiceOption.add(requireContext().getResources().getString(ttsVoice.getShowName()));
        }
        for (TtsRate ttsRate : TtsRate.values()) {
            rateOption.add(String.valueOf(ttsRate.getValue()));
        }
        for (TtsOutput ttsOutput : TtsOutput.values()) {
            outputOption.add(ttsOutput.getTag());
        }

        initSpinner(voiceOption, binding.textToSpeech.voiceContent, requireContext());
        initSpinner(rateOption, binding.textToSpeech.rateContent, requireContext());
        initSpinner(outputOption, binding.textToSpeech.outputContent, requireContext());

        binding.textToSpeech.convert.setOnClickListener(view -> {
            try {
                Utility.hideKeyboard(requireActivity());
                String text = binding.textToSpeech.textContent.getText().toString();
                String voice = binding.textToSpeech.voiceContent.getText().toString();
                String rate = binding.textToSpeech.rateContent.getText().toString();
                String output = binding.textToSpeech.outputContent.getText().toString();
                viewModel.textToSpeech(requireContext(),text, voice, rate, output);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        binding.textToSpeech.content.mediaPlay.setOnClickListener(view -> viewModel.playTts());

        viewModel.getPlayingTtsStatus().observe(requireActivity(), isPlaying -> {
            if (isPlaying) {
                binding.textToSpeech.content.mediaPlay.setImageResource(R.drawable.ic_pause);
            } else {
                binding.textToSpeech.content.mediaPlay.setImageResource(R.drawable.ic_play);
            }
        });

        viewModel.getTtsPlayingDuration().observe(requireActivity(), duration -> {
            binding.textToSpeech.content.mediaProgress.setMax(duration);
            Log.d("tts", "duration: " + duration);
        });

        viewModel.getTtsPlayingProgress().observe(requireActivity(), progress -> {
            binding.textToSpeech.content.mediaProgress.setProgress(progress);
            Log.d("tts", "progress: " + progress);
        });

        viewModel.getTtsProgress().observe(requireActivity(), isShow -> {
            int visibility = isShow ? View.VISIBLE : View.INVISIBLE;
            binding.textToSpeech.progress.setVisibility(visibility);
            binding.textToSpeech.convert.setEnabled(!isShow);
        });
        viewModel.getTextToSpeech().observe(requireActivity(), data -> {
            String log = new GsonBuilder().setPrettyPrinting().create().toJson(data);
            binding.textToSpeech.log.setText(log);
        });
    }

    private void initSpeechToText() {
        binding.speechToText.textContent.setText("https://pega-audio.mediacdn.vn/audio_generated/audio_2023_11_17_15_37_59_1700210279.31988.mp3");
        binding.speechToText.log.setVisibility(logVisibility);
        binding.speechToText.logTitle.setVisibility(logVisibility);

        binding.speechToText.file.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setType(STT_AUDIO_TYPE);
            this.startActivityForResult(intent, STT_REQ_CODE);
        });

        binding.speechToText.convert.setOnClickListener(view -> {
            try {
                String text = binding.speechToText.textContent.getText().toString();
                viewModel.speechToText(requireContext(), text);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        binding.speechToText.contentPreview.mediaPlay.setOnClickListener(view -> viewModel.playStt());

        viewModel.getPlayingSttStatus().observe(requireActivity(), isPlaying -> {
            if (isPlaying) {
                binding.speechToText.contentPreview.mediaPlay.setImageResource(R.drawable.ic_pause);
            } else {
                binding.speechToText.contentPreview.mediaPlay.setImageResource(R.drawable.ic_play);
            }
        });

        viewModel.getSttPlayingDuration().observe(requireActivity(), duration -> {
            binding.speechToText.contentPreview.mediaProgress.setMax(duration);
            Log.d("stt", "duration: " + duration);
        });
        viewModel.getSttPlayingProgress().observe(requireActivity(), progress -> {
            binding.speechToText.contentPreview.mediaProgress.setProgress(progress);
            Log.d("stt", "progress: " + progress);
        });

        viewModel.getSttProgress().observe(requireActivity(), isShow -> {
            int visibility = isShow ? View.VISIBLE : View.INVISIBLE;
            binding.speechToText.progress.setVisibility(visibility);
            binding.speechToText.convert.setEnabled(!isShow);
        });
        viewModel.getPermissions().observe(requireActivity(), permissions -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                try {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                    intent.addCategory("android.intent.category.DEFAULT");
                    intent.setData(Uri.parse(String.format("package:%s", requireActivity().getPackageName())));
                    startActivityForResult(intent, STT_REQ_PERMISSION_CODE);
                } catch (Exception e) {
                    Intent intent = new Intent();
                    intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                    startActivityForResult(intent, STT_REQ_PERMISSION_CODE);
                }
            } else {
                String[] array = new String[permissions.size()];
                permissions.toArray(array);
                ActivityCompat.requestPermissions(getActivity(), array, STT_REQ_PERMISSION_BELOW_11_CODE);
            }
        });
        viewModel.getSpeechToText().observe(requireActivity(), data -> {
            if (data == null) return;

            String log = new GsonBuilder().setPrettyPrinting().create().toJson(data);
            binding.speechToText.log.setText(log);

            int visibility = data.result.text.size() > 0 ? View.VISIBLE : View.INVISIBLE;
            binding.speechToText.endTag.setVisibility(visibility);
            binding.speechToText.textByTime.removeAllViews();
            for (SpeechToText.Text item : data.result.text) {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                LayoutSpeechSttResultBinding itemBinding = LayoutSpeechSttResultBinding.inflate(inflater, null, false);
                itemBinding.time.setText(item.start);
                itemBinding.text.setText(item.text);

                ViewGroup.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                binding.speechToText.textByTime.addView(itemBinding.getRoot(), params);
            }
        });
    }

    private void initSpinner(List<String> list, TextInputEditText text, Context context){
        ArrayAdapter adapter = new ArrayAdapter(requireContext(), R.layout.dropdown_item, list);
        ListPopupWindow popup = new ListPopupWindow(context);
        popup.setAdapter(adapter);
        popup.setAnchorView(text);
        popup.setOnItemClickListener((adapterView, view, i, l) -> {
            text.setText(list.get(i));
            popup.dismiss();
        });
        text.setOnClickListener(v -> popup.show());
    }

    @RequiresApi(api = Build.VERSION_CODES.Q)
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == STT_REQ_CODE && resultCode == RESULT_OK) {
            try {
                Uri fileUri = data.getData();
                String path = new FileUtil(requireContext(), requireActivity()).getPath(fileUri);
                binding.speechToText.textContent.setText(path);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == STT_REQ_PERMISSION_CODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    // perform action when allow permission success
                } else {
                    Snackbar.make(binding.getRoot(), "Allow permission for storage access!", Snackbar.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case STT_REQ_PERMISSION_BELOW_11_CODE:
                if (grantResults.length > 0) {
                    boolean READ_EXTERNAL_STORAGE = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean WRITE_EXTERNAL_STORAGE = grantResults[1] == PackageManager.PERMISSION_GRANTED;

                    if (READ_EXTERNAL_STORAGE && WRITE_EXTERNAL_STORAGE) {
                        // perform action when allow permission success
                    } else {
                        Snackbar.make(binding.getRoot(), "Allow permission for storage access!", Snackbar.LENGTH_SHORT).show();
                    }
                }
                break;
        }
    }
}