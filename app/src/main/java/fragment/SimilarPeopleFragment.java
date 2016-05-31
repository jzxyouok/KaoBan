package fragment;


import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.dezhou.lsy.projectdezhoureal.R;

import org.apache.http.impl.client.DefaultHttpClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import adapter.SimilarPeopleAdapter;
import bean.SimplePeopleBean;
import kaobanxml.XmlParser;
import kongjian.PullToRefreshView;
import kongjian.SelfActivity;
import network.MyHttpClient;
import tempvalue.Exam;
import tempvalue.UserNum;
import utils.URLSet;

/**
 * A simple {@link Fragment} subclass.
 */
public class SimilarPeopleFragment extends Fragment implements PullToRefreshView.OnHeaderRefreshListener, PullToRefreshView.OnFooterRefreshListener {

    private ListView simpleList;
    private static Map<String, String> simpleFlag = new HashMap<String, String>();
    View v;
    List<SimplePeopleBean> simple_list;
    PullToRefreshView mPullToRefreshView;
    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0x121:
                    Bundle bundle = msg.getData();
                    String simpleString = bundle.getString("simpleString");
                    Log.d("simpleString", simpleString);
                    if (simpleString == null || simpleString.equals("")) {
                        Toast.makeText(getActivity(), "无相似的人", Toast.LENGTH_SHORT).show();
                    } else {

                        simple_list = XmlParser.xmlSimpleGet(simpleString);
                        SimilarPeopleAdapter adapter = new SimilarPeopleAdapter(getActivity(), simple_list);
                        simpleList.setAdapter(adapter);

                    }
                    break;
            }
        }
    };

    public SimilarPeopleFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_similar_people, container, false);
        initView();
        return v;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initNetWork();
    }

    public void initView() {

        simpleList = (ListView) v.findViewById(R.id.simple_list);
        simpleList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getActivity(), SelfActivity.class);
                intent.putExtra("userNum", simple_list.get(i).getNumber());
                startActivity(intent);
            }
        });
        mPullToRefreshView = (PullToRefreshView) v.findViewById(R.id.similar_refresh);
        mPullToRefreshView.setOnHeaderRefreshListener(this);
        mPullToRefreshView.setOnFooterRefreshListener(this);
        ImageView shaixuan = (ImageView) v.findViewById(R.id.similar_people_shaixuan);
        shaixuan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
//                builder.setIcon(R.drawable.dynamic_tag);
//                builder.setTitle("选择一个标签");
                //    指定下拉列表的显示数据
                if (UserNum.exams != null && UserNum.exams.size() != 0) {
                    List<Exam> exams = UserNum.exams;
                    final String[] examsNameArray = new String[exams.size()];
                    final String[] examsRealNameArray = new String[exams.size()];
                    for (int j = 0; j < exams.size(); j++) {
                        examsRealNameArray[j] = exams.get(j).getExam();
                        examsNameArray[j] = exams.get(j).getExam().split("/")[1];
                    }
                    //    设置一个下拉的列表选择项
                    builder.setItems(examsNameArray, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            initNetWork(examsRealNameArray[which]);
                        }
                    });
                    builder.show();
                }
            }
        });
    }

    public void initNetWork() {
        if (UserNum.userNum != null) {
            simpleFlag.put("userNum", UserNum.userNum);

        } else {
            simpleFlag.put("userNum", "");
        }

        netWorkThread(URLSet.getSimplePeople, simpleFlag, 0x121);
    }

    public void initNetWork(String exam) {
        if (UserNum.userNum != null) {
            simpleFlag.put("userNum", UserNum.userNum);

        } else {
            simpleFlag.put("userNum", "");
        }
        simpleFlag.put("userExam", exam);
        netWorkThread(URLSet.getSimplePeople, simpleFlag, 0x121);
    }

    private void netWorkThread(final String url, final Map<String, String> mapTmp, final int msgWhat) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                DefaultHttpClient client = MyHttpClient.getHttpClient();
                String simpleString = MyHttpClient.doGet(client, url, mapTmp, true);
                Message message = new Message();
                message.what = msgWhat;
                Bundle bundle = new Bundle();
                if (simpleString == null)
                    simpleString = "";
                bundle.putString("simpleString", simpleString);
                message.setData(bundle);
                handler.sendMessage(message);
            }
        }).start();
    }


    @Override
    public void onFooterRefresh(PullToRefreshView view) {
        mPullToRefreshView.postDelayed(new Runnable() {

            @Override
            public void run() {

                mPullToRefreshView.onHeaderRefreshComplete();
//                netWorkThread(URLSet.getClassificationURL, 0x122);
                initNetWork();
            }
        }, 2000);
    }

    @Override
    public void onHeaderRefresh(PullToRefreshView view) {
        mPullToRefreshView.postDelayed(new Runnable() {

            @Override
            public void run() {

                mPullToRefreshView.onHeaderRefreshComplete();
//                netWorkThread(URLSet.getClassificationURL, 0x122);
                initNetWork();
            }
        }, 2000);
    }
}
