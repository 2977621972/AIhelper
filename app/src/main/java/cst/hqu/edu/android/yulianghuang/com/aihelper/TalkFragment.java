package cst.hqu.edu.android.yulianghuang.com.aihelper;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class TalkFragment extends Fragment implements View.OnClickListener{
    public static ListView msglv;
    public static EditText inputet;
    public Button sendbtn;
    public Button voicebtn;
    public static MsgAdapter msgadapter;
    public static List<Msg> msgList = new ArrayList<Msg>();
    public static TalkFragment newInstance(){
        TalkFragment talkFragment=new TalkFragment();
        return talkFragment;
    }
    public TalkFragment(){

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.activity_talk_fragment,container,false);
        msgadapter = new MsgAdapter(getActivity(), R.layout.view_talk, msgList);
        inputet = (EditText)view.findViewById(R.id.talket);
        sendbtn = (Button)view.findViewById(R.id.sendbtn);
        sendbtn.setOnClickListener(this);
        voicebtn=(Button)view.findViewById(R.id.voicebtn);
        voicebtn.setOnClickListener(this);
        msglv = (ListView)view.findViewById(R.id.talklv);
        msglv.setAdapter(msgadapter);
        //initMsgs();
        return view;
    }

    private void initMsgs() {//初始化对话
        Msg msg3 = new Msg("你好,我是小七，有什么问题都可以问我哦!", Msg.TYPE_RECEIVED);
        msgList.add(msg3);
    }
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final MainActivity activity = (MainActivity) getActivity();
        Button button = (Button) activity.findViewById(R.id.talkinbtn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                activity.replaceHomeFragment();
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.sendbtn:
                //发送输入框中内容访问问答库并获取答案
                String content = inputet.getText().toString();
                sendTalkMessege(content);
                MainActivity activity1 = (MainActivity) getActivity();
                activity1.hideSoft();//隐藏输入法
                break;
            case R.id.voicebtn:
                MainActivity activity = (MainActivity) getActivity();
                activity.btnvoice(0);
                break;
        }
    }

    public void sendTalkMessege(String content) {
        if(!"".equals(content)) {
            MainActivity activity = (MainActivity) getActivity();
            //activity.sendRequestWithOkhttp(content);
            activity.sendRequestToTuling(content);      //调用图灵
            Msg msg = new Msg(content, Msg.TYPE_SEND);
            msgList.add(msg);
            msglv.setSelection(msgList.size());
            msgadapter.notifyDataSetChanged();
            inputet.setText("");//清空输入框
        }
    }


}