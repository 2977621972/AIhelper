package cst.hqu.edu.android.yulianghuang.com.aihelper;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class Tlakhome extends Fragment implements View.OnClickListener{

    public static EditText homeinputet;
    private Button homesendbtn;
    private Button homevoicebtn;
    private Button talkoutbtn;
    public static TextView hometalktv;

    public static Tlakhome newInstance() {
        Tlakhome homeFragment = new Tlakhome();
        return homeFragment;
    }
    public Tlakhome(){

    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.activity_tlakhome,container,false);
        MainActivity activity= (MainActivity) getActivity();
        homeinputet = (EditText)view.findViewById(R.id.hometalket);
        hometalktv=view.findViewById(R.id.hometalktv);
        homesendbtn = (Button)view.findViewById(R.id.homesendbtn);
        homesendbtn.setOnClickListener(this);
        homevoicebtn=(Button)view.findViewById(R.id.homevoicebtn);
        homevoicebtn.setOnClickListener(this);
        return view;
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final MainActivity activity = (MainActivity) getActivity();
        Button button = (Button) activity.findViewById(R.id.talkoutbtn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.replaceFragment();
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.homesendbtn:
                String content = homeinputet.getText().toString();
                sendTalkMessege(content);
                break;
            case R.id.homevoicebtn:
                MainActivity activity = (MainActivity) getActivity();
                activity.btnvoice(1);
                break;
            case R.id.v_learningicon:

        }
    }

    public void sendTalkMessege(String content) {
        if(!"".equals(content)) {
            MainActivity activity = (MainActivity) getActivity();
            //activity.sendRequestWithOkhttp(content);
            activity.sendRequestToTuling(content);
            Msg msg = new Msg(content, Msg.TYPE_SEND);
            TalkFragment.msgList.add(msg);
            TalkFragment.msglv.setSelection(TalkFragment.msgList.size());
            TalkFragment.msgadapter.notifyDataSetChanged();
            homeinputet.setText("");
        }
    }
}