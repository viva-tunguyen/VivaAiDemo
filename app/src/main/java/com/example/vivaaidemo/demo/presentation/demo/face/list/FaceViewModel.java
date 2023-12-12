package com.example.vivaaidemo.demo.presentation.demo.face.list;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.MutableLiveData;

import com.example.vivaaidemo.R;
import com.example.vivaaidemo.demo.common.BaseViewModel;
import com.example.vivaaidemo.demo.presentation.common.BaseTab;
import com.example.vivaaidemo.demo.presentation.demo.face.detail.checkin.filter.CheckInFilterFragment;
import com.example.vivaaidemo.demo.presentation.demo.face.detail.checkin.list.CheckInFragment;
import com.example.vivaaidemo.demo.presentation.demo.face.detail.compare.FaceCompareFragment;
import com.example.vivaaidemo.demo.presentation.demo.face.detail.detect.FaceDetectFragment;
import com.example.vivaaidemo.demo.presentation.demo.face.detail.fas.FasDetectFragment;
import com.example.vivaaidemo.demo.presentation.demo.face.detail.register.FaceRegisterFragment;
import com.example.vivaaidemo.demo.presentation.demo.face.detail.userinfo.get.GetInfoFragment;
import com.example.vivaaidemo.demo.presentation.demo.face.detail.userinfo.update.UpdateInfoFragment;

import java.util.ArrayList;
import java.util.List;


public class FaceViewModel extends BaseViewModel {
    /* **********************************************************************
     * Variable
     ********************************************************************** */
    private final MutableLiveData<List<FaceDetail>> tabs;

    /* **********************************************************************
     * Constructor
     ********************************************************************** */
    public FaceViewModel() {
        List<FaceDetail> tabs = new ArrayList<FaceDetail>() {{
            add(new FaceDetail(0, Type.CHECK_IN_HISTORY_LIST, R.string.checkin_history, R.drawable.ic_check_in, null, CheckInFragment.class));
            add(new FaceDetail(1, Type.CHECK_IN_HISTORY_FILTER, R.string.checkin_history_filter, R.drawable.ic_check_in, null, CheckInFilterFragment.class));
            add(new FaceDetail(2, Type.FACE_COMPARE, R.string.face_compare, R.drawable.ic_face, null, FaceCompareFragment.class));
            add(new FaceDetail(3, Type.FACE_DETECT, R.string.face_predict, R.drawable.ic_face, null, FaceDetectFragment.class));
            add(new FaceDetail(4, Type.FAS_DETECT, R.string.fas_predict, R.drawable.ic_face, null, FasDetectFragment.class));
            add(new FaceDetail(5, Type.FACE_REGISTER, R.string.face_register, R.drawable.ic_face, null, FaceRegisterFragment.class));
            add(new FaceDetail(6, Type.USER_INFO, R.string.get_info, R.drawable.ic_user_info, null, GetInfoFragment.class));
            add(new FaceDetail(7, Type.USER_INFO_UPDATE, R.string.update_info, R.drawable.ic_user_info, null, UpdateInfoFragment.class));
        }};
        this.tabs = new MutableLiveData<>(tabs);
    }

    /* **********************************************************************
     * Function
     ********************************************************************** */
    public MutableLiveData<List<FaceDetail>> getTabs() {
        return tabs;
    }

    /* **********************************************************************
     * Class
     ********************************************************************** */
    public enum Type {
        FACE_REGISTER, FACE_DETECT, FAS_DETECT, FACE_COMPARE,
        CHECK_IN_HISTORY_LIST, CHECK_IN_HISTORY_FILTER, USER_INFO, USER_INFO_UPDATE
    }

    public static class FaceDetail extends BaseTab<Type> {
        public FaceDetail(int index, Type type, int title, int icon, Fragment fragment, Class<?> clazz) {
            super(index, type, title, icon, fragment, clazz);
        }
    }
}
