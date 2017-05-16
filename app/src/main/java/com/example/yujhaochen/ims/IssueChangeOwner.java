package com.example.yujhaochen.ims;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.zfdang.multiple_images_selector.ImagesSelectorActivity;
import com.zfdang.multiple_images_selector.SelectorSettings;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public class IssueChangeOwner extends AppCompatActivity {

    static final int REQUEST_VIDEO_CAPTURE = 1;

    static final int REQUEST_IMAGE_CAPTURE = 2;

    static final int REQUEST_Voice_CAPTURE = 3;

    static final int REQUEST_Photo_CAPTURE = 4;

    private static final String WRITE_EXTERNAL_STORAGE = "android.permission.WRITE_EXTERNAL_STORAGE";
    private static final String READ_EXTERNAL_STORAGE = "android.permission.READ_EXTERNAL_STORAGE";
    private static final int REQUEST_EXTERNAL_STORAGE = 1;

    private File ImageFile;

    private File VideoFile;

    private File VoiceFile;

    private NewIssueFileAdapter mListAdapter;

    private Context mContext;

    private String IssueID;

    private String Author;

    private RequestQueue mQueue;

    private String SelectValue;

    private String AuthorNameCN;

    private String AuthorNameEN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_issue_change_priority);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        mContext = this;

        Bundle Bundle = getIntent().getExtras();

        IssueID = Bundle.getString("IssueID");

        Author = Bundle.getString("Author");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        toolbar.setTitle("Priority");

        final EditText txt_Reason = (EditText) findViewById(R.id.txt_Reason);

        TextView txt_Close_Finish = (TextView) findViewById(R.id.txt_Close_Finish);

        txt_Close_Finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                v.setVisibility(View.GONE);

                Change_Issue_Owner(IssueID, SelectValue);

                String CommentTitle = "@Issue Owner Change";

                String CommentText = "◎Issue Owner Change： 『" + AuthorNameCN + " " + AuthorNameEN + "』change to 『" + SelectValue + "』";

                CommentText += "Reason: 『" + txt_Reason.getText().toString() + "』\n";

                C_Comment_Insert(CommentText, IssueID);

                List<String> WorkID_List = new ArrayList<String>();

                WorkID_List.add(0, SelectValue);

                WorkID_List.add(1, Author);

                AppClass.Send_Notification(WorkID_List, CommentTitle, CommentText, "IssueInfo", "IssueInfo", IssueID, mContext);
            }
        });


    }

    private void Set_CheckButton(String Value) {
        SelectValue = Value;


    }


    private void Change_Issue_Owner(final String IssueID, final String WorkID) {


        if (!TextUtils.isEmpty(IssueID) && !TextUtils.isEmpty(WorkID)) {

            if (mQueue == null) {
                mQueue = Volley.newRequestQueue(mContext);
            }

            Map<String, String> map = new HashMap<String, String>();
            map.put("IssueID", IssueID);
            map.put("WorkID", WorkID);

            String Path = GetServiceData.ServicePath + "/Change_Issue_Owner";

            GetServiceData.SendPostRequest(Path, mQueue, new GetServiceData.VolleyStringCallback() {
                @Override
                public void onSendRequestSuccess(String result) {

                    AppClass.AlertMessage("Change Owner Success", mContext);
                }

                @Override
                public void onSendRequestError(String result) {
                    Log.w("NotificationSuccess", result);
                }

            }, map);


        } else {
            AppClass.AlertMessage("Change Owner Error", mContext);
        }


    }


    private void C_Comment_Insert(String Comment, String IssueID) {


        String WorkID = UserData.WorkID;

        if (!TextUtils.isEmpty(Comment)) {

            Map<String, String> map = new HashMap<String, String>();
            map.put("F_Keyin", WorkID);
            map.put("F_Master_Table", "C_Issue");
            map.put("F_Master_ID", IssueID);
            map.put("F_Comment", Comment);

            if (mQueue == null) {
                mQueue = Volley.newRequestQueue(mContext);
            }


            String Path = GetServiceData.ServicePath + "/C_Comment_Insert";


            GetServiceData.SendPostRequest(Path, mQueue, new GetServiceData.VolleyStringCallback() {
                @Override
                public void onSendRequestSuccess(String result) {


                    try {

                        JSONObject obj = new JSONObject(result);

                        JSONArray UserArray = new JSONArray(obj.getString("Key"));

                        if (UserArray.length() > 0) {

                            JSONObject IssueData = UserArray.getJSONObject(0);

                            String CommentNo = String.valueOf(IssueData.getInt("CommentNo"));
                        }

                    } catch (Throwable t) {

                    }


                }

                @Override
                public void onSendRequestError(String result) {

                }

            }, map);

        }
    }


    public String GetReason() {
        EditText txt_Reason = (EditText) findViewById(R.id.txt_Reason);

        return txt_Reason.getText().toString();
    }


    private TextWatcher filterTextWatcher = new TextWatcher() {

        public void afterTextChanged(Editable s) {
        }

        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            //adapter.getFilter().filter(s);
        }
    };


}