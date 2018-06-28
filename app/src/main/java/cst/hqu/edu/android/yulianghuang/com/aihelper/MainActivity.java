package cst.hqu.edu.android.yulianghuang.com.aihelper;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;


import com.githang.statusbar.StatusBarCompat;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity  {
    private Tlakhome homeFragment;
    private TalkFragment talkFragment;
    private Button talkbtn;
    private EditText talket;

    private long exitTime = 0;
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");
    private LinearLayout ll_main;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
        //       WindowManager.LayoutParams.FLAG_FULLSCREEN);  //全屏
        //Window window = getWindow();
        //window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);//透明
        /*getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                | View.SYSTEM_UI_FLAG_FULLSCREEN);*/
        setContentView(R.layout.activity_main);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.hide();//隐藏标题栏

        requestPermissions();
        ll_main = findViewById(R.id.ll_main);

        initView();//初始化fragemnt的view
        initBottomNavigationBar();//初始化底部导航栏
        //讯飞语音SDK初始化
        //SpeechUtility.createUtility(this, SpeechConstant.APPID + getString(R.string.APPID));
        SpeechUtility.createUtility(this, SpeechConstant.APPID +"=5b2b0e22");

    }

    private void initBottomNavigationBar() {
    }

    public void hideSoft(){//隐藏输入法
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0); // 隐藏

    }
    public void btnvoice(final int flag) {
        //启用语音输入根据flag的值的不同将识别结果输出到不同控件上

        RecognizerDialog dialog = new RecognizerDialog(this,null);

        dialog.setParameter(SpeechConstant.LANGUAGE, "zh_cn");

        dialog.setParameter(SpeechConstant.ACCENT, "mandarin");

        dialog.setListener(new RecognizerDialogListener() {

            @Override

            public void onResult(RecognizerResult recognizerResult, boolean b) {
                if(!b) {
                    printResult(recognizerResult, flag);
                }
            }

            @Override
            public void onError(SpeechError speechError) {

            }

        });

        dialog.show();

        Toast.makeText(this, "请开始说话", Toast.LENGTH_SHORT).show();

    }

    //回调结果：

    private void printResult(RecognizerResult results,int flag) {

        String text = parseIatResult(results.getResultString());

        // 自动填写地址

        if(flag == 0){
            talkFragment.sendTalkMessege(text);
        }else if(flag == 1){
            homeFragment.sendTalkMessege(text);
        }

        /*if(flag==0){//flag==0表示把内容输出给谈话记录
           EditText inputText=findViewById(R.id.talket);
           inputText.append(text);
        }
        else if (flag==1){//flag==1表示把内容输出给home页面
            EditText inputText=findViewById(R.id.hometalket);
            inputText.append(text);
        }*/

    }

    public static String parseIatResult(String json) {//解析语音识别结果的json文件

        StringBuffer ret = new StringBuffer();

        try {

            JSONTokener tokener = new JSONTokener(json);

            JSONObject joResult = new JSONObject(tokener);

            JSONArray words = joResult.getJSONArray("ws");

            for (int i = 0; i < words.length(); i++) {

                // 转写结果词，默认使用第一个结果

                JSONArray items = words.getJSONObject(i).getJSONArray("cw");

                JSONObject obj = items.getJSONObject(0);

                ret.append(obj.getString("w"));

            }

        } catch (Exception e) {

            e.printStackTrace();

        }

        return ret.toString();

    }



    private void showHomeFragment(){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        hideFragment(transaction);
        transaction.show(homeFragment);
        transaction.commit();
        StatusBarCompat.setStatusBarColor(this, Color.parseColor("#ad4759"));
    }
    private void showTalkFragment(){
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        hideFragment(transaction);
        transaction.show(talkFragment);
        transaction.commit();
        StatusBarCompat.setStatusBarColor(this, Color.parseColor("#2abfc0"));
    }
    private void setDefaultFragment() {
        android.support.v4.app.FragmentManager fm = getSupportFragmentManager();
        android.support.v4.app.FragmentTransaction transaction = fm.beginTransaction();
        if(homeFragment == null){
            homeFragment = homeFragment.newInstance();
            transaction.add(R.id.mainfl, homeFragment);
        }
        transaction.commit();
        showHomeFragment();
    }

    private void initView() {//初始化fragment中的view
        FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentTransaction fragmentTransaction=fragmentManager.beginTransaction();
        //talkFragment=talkFragment.newInstance();
        if(homeFragment == null){
            homeFragment = homeFragment.newInstance();
            fragmentTransaction.add(R.id.mainfl, homeFragment);
        }
        if (talkFragment == null) {
            talkFragment = talkFragment.newInstance();
            fragmentTransaction.add(R.id.mainfl, talkFragment);
        }
        setDefaultFragment();
        //fragmentTransaction.replace(R.id.mainfl,talkFragment).commit();
        //fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }



    public void replaceFragment() {//点击查看对话记录时调用方法
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (talkFragment == null) {
            //talkFragment = TalkFragment.newInstance();
            talkFragment = talkFragment.newInstance();
            fragmentTransaction.add(R.id.mainfl, talkFragment);
            fragmentTransaction.addToBackStack(null);
        }
        fragmentTransaction.commit();
        //getSupportActionBar().show();//显示标题栏
        //fragmentTransaction.add(R.id.mainfl, talkFragment);
        showTalkFragment();
    }

    public void replaceHomeFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (homeFragment == null) {
            homeFragment = Tlakhome.newInstance();
            fragmentTransaction.add(R.id.mainfl, homeFragment);
        }
        fragmentTransaction.commit();
        //getSupportActionBar().hide();//隐藏标题栏
        showHomeFragment();
        //fragmentTransaction.addToBackStack(null);
    }



    private void hideFragment(FragmentTransaction fragmentTransaction){
        if(homeFragment != null){
            fragmentTransaction.hide(homeFragment);
        }
        if(talkFragment != null){
            fragmentTransaction.hide(talkFragment);
        }
    }




    String answer = null;
    String answerByTuling = null;

    public void sendRequestToTuling(final String question){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    JSONObject jsonObject = new JSONObject();

                    jsonObject.put("key", "65dbc349956c4cd293f16c547f85ab2d");
                    jsonObject.put("info", question);

                    String questionJSON = jsonObject.toString();

                    OkHttpClient client = new OkHttpClient();
                    RequestBody body = RequestBody.create(JSON, questionJSON);
                    Request request = new Request.Builder()
                            .url("http://www.tuling123.com/openapi/api")
                            .post(body)
                            .build();
                    Response response = client.newCall(request).execute();
                    String responseData = response.body().string();
                    Log.d("responseData_1", responseData);
                    System.out.println();
                    System.out.println("responseData_1: " + responseData);
                    System.out.println();
                    parseJSONWithJsonObject(responseData);

                }catch (IOException e){
                    e.printStackTrace();
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void sendRequestWithOkhttp(final String quesstion) {
        //通过Okhttp发送问题给问答库
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    OkHttpClient okHttpClient = new OkHttpClient();
                    Request request = new Request.Builder()
                            .url("http://47.95.221.185:9999/getanswer?q=" + quesstion)
                            .build();
                    Response response = okHttpClient.newCall(request).execute();
                    String responseData = response.body().string();
                    parseJSONWithJsonObject(responseData);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    public void parseJSONWithJsonObject(String responseData) throws JSONException {
        //解析问答库返回的json文件
        Log.d("responseData_2", responseData);
        System.out.println();
        System.out.println("responseData_2: " + responseData);
        System.out.println();
        int status = 1;
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(responseData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            status = jsonObject.getInt("Status");
            System.out.println("状态：" + jsonObject.getString("Status"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            System.out.println("信息：" + jsonObject.getString("msg"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            answer = jsonObject.getString("answer");
            System.out.println("答案：" + jsonObject.getString("answer"));
            System.out.print(answer);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Message message = new Message();//由于非主线程无法更新UI所以需要使用Handler更新
        if (status == 0) {
            message.what = 1;
        }
        //-------------------------------------------
        int code = 5000;//无解析结果
        try{
            code = jsonObject.getInt("code");
            Log.d("code", code + "");
            System.out.println();
            System.out.println("code: " + code);
            System.out.println();
        }catch (JSONException e){
            e.printStackTrace();
        }
        try{
            answerByTuling = jsonObject.getString("text");
            Log.d("text", answerByTuling + "");
            System.out.println();
            System.out.println("text: " + answerByTuling);
            System.out.println();
        }catch (JSONException e){
            e.printStackTrace();
        }
        Message msgTuling = new Message();
        if(code == 100000){
            msgTuling.what = 2;
        }

        handler.sendMessage(message);
        handler.sendMessage(msgTuling);
    }


    private void requestPermissions(){
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                int permission = ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE);
                if(permission!= PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,new String[]
                            {Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                    Manifest.permission.LOCATION_HARDWARE,Manifest.permission.READ_PHONE_STATE,
                                    Manifest.permission.WRITE_SETTINGS,Manifest.permission.READ_EXTERNAL_STORAGE,
                                    Manifest.permission.RECORD_AUDIO,Manifest.permission.READ_CONTACTS},0x0010);
                }

                if(permission != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this,new String[] {
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION},0x0010);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public Handler handler = new Handler() {
        //将问答库答案更新到聊天记录上
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    Msg msg2 = new Msg(answer, Msg.TYPE_RECEIVED);
                    TalkFragment.msgList.add(msg2);
                    TalkFragment.msgadapter.notifyDataSetChanged();
                    Tlakhome.hometalktv.setText(answer);
                    Tlakhome.hometalktv.setVisibility(View.VISIBLE);
                    break;
                case 2:
                    playResponseAudio(answerByTuling);
                    Msg msgTuling = new Msg(answerByTuling, Msg.TYPE_RECEIVED);
                    TalkFragment.msgList.add(msgTuling);
                    TalkFragment.msgadapter.notifyDataSetChanged();
                    Tlakhome.hometalktv.setText(answerByTuling);
                    Tlakhome.hometalktv.setVisibility(View.VISIBLE);

            }
        }
    };

    public void playResponseAudio(String content){
        SpeechSynthesizer speechSynthesizer = SpeechSynthesizer.createSynthesizer(this, null);
        speechSynthesizer.setParameter(SpeechConstant.SPEED, "50");
        speechSynthesizer.setParameter(SpeechConstant.VOICE_NAME, "xiaoyan");
        speechSynthesizer.startSpeaking(content, new mSynthesizerListener());
    }

    //“再按一次退出”
    @Override
    public void onBackPressed(){
        /*if(pullableView.currentStatus == pullableView.STATUS_RELEASE){
            pullableView.reset();
        }else{

        }*/
        if (System.currentTimeMillis() - exitTime < 1000) {
            super.onBackPressed();
        } else {
            Toast.makeText(MainActivity.this, "再按一次退出", Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        }
    }

}

