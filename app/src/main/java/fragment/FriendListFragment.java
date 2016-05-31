package fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.Lc.FriendInvited;
import com.bumptech.glide.Glide;
import com.dezhou.lsy.projectdezhoureal.ChatMessage;
import com.dezhou.lsy.projectdezhoureal.R;
import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContactManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMGroupManager;
import com.easemob.exceptions.EaseMobException;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import net.tsz.afinal.FinalBitmap;

import org.apache.http.Header;
import org.xmlpull.v1.XmlPullParser;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bean.UserInfoBean;
import kongjian.PullToRefreshView;
import tempvalue.UnReadMessage;
import tempvalue.UserNum;
import utils.URLSet;


/**
 *
 */
public class FriendListFragment extends Fragment implements PullToRefreshView.OnHeaderRefreshListener, PullToRefreshView.OnFooterRefreshListener{


    private ListView listView;
    private FriendListAdapter adapter;
    private static SharedPreferences.Editor editor;
    public LinearLayout re;
    private UserInfoBean userInf;
    private String str2 = "";
    private TextView name;
    private String username="";
    private ImageView imagehead;
    private TextView unread;
    private HashMap<String,SoftReference<Bitmap>> imageCache=null;
    private List<String> names=new ArrayList<String>();
    private ArrayList<String> send_username=new ArrayList<String>();
    BitmapFactory.Options option = new BitmapFactory.Options();
    FinalBitmap finalBitmap =null;
    PullToRefreshView mPullToRefreshView;
    Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1002:
                    Bundle bundle = new Bundle();
                    bundle = msg.getData();
                    ArrayList<String> names = bundle.getStringArrayList("names");
                    adapter = new FriendListAdapter(names);
                    listView.setAdapter(adapter);
                    setListViewHeight(listView);
                    UnReadMessage.flag=false;
                    break;
            }
        }
    };




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // 设置标题
        View v = inflater.inflate(R.layout.message, null);
        mPullToRefreshView = (PullToRefreshView)v.findViewById(R.id.main_pull_refresh_view);
        mPullToRefreshView.setOnHeaderRefreshListener(this);
        mPullToRefreshView.setOnFooterRefreshListener(this);
        listView = (ListView) v.findViewById(R.id.listmeaasge);
        re=(LinearLayout)v.findViewById(R.id.invite);
        unread=(TextView)v.findViewById(R.id.unread_msg_number);
        return v;

    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        loginHuanxin(UserNum.userNum, "admin");
        re.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(getActivity(), FriendInvited.class);
                in.putExtra("username",UnReadMessage.username);
                in.putExtra("reason",UnReadMessage.reason);
                startActivity(in);
                unread.setVisibility(View.GONE);

            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String usm = adapter.getItem(position).toString();
                Intent intent = new Intent(getActivity(), ChatMessage.class);
                intent.putExtra("userId", usm);
                startActivity(intent);

            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {
                new AlertDialog.Builder(getActivity())
                        .setTitle("删除好友")
                        .setItems(R.array.arrcontent,
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        String[] PK = getResources()
                                                .getStringArray(
                                                        R.array.arrcontent);
                                        Toast.makeText(
                                                getActivity(),
                                                PK[which], Toast.LENGTH_LONG)
                                                .show();
                                        if (PK[which].equals("删除")) {
                                            // 按照这种方式做删除操作，这个if内的代码有bug，实际代码中按需操作

                                            new Thread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    try {

                                                        EMContactManager.getInstance().deleteContact(names.get(i));
                                                        getHuanxin();
                                                    } catch (EaseMobException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            }).start();
//                                            names.remove(i);
//                                            adapter = (FriendListAdapter) listView
//                                                    .getAdapter();
//                                            if (!adapter.isEmpty()) {
//                                                adapter.notifyDataSetChanged(); // 实现数据的实时刷新
//                                            }
                                        }
                                    }
                                })
                        .setNegativeButton("取消",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        // TODO Auto-generated method stub

                                    }
                                }).show();
                return true;
            }
        });

    }

    public void setListViewHeight(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null) {
            return;
        }
        int totalHeight = 0;
        for (int i = 0, len = listAdapter.getCount(); i < len; i++) {
            View listItem = listAdapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (listAdapter.getCount() - 1));
        listView.setLayoutParams(params);
    }
    public void loginHuanxin(final String userName, final String password) {
        EMChatManager.getInstance().login(userName, password, new EMCallBack() {
            @Override
            public void onSuccess() {
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {

                        EMGroupManager.getInstance().loadAllGroups();
                        EMChatManager.getInstance().loadAllConversations();

                        getHuanxin();
                    }

                });
            }

            public void onProgress(int progress, String status) {
            }

            public void onError(int code, String message) {
                Log.d("main", "");
            }
        });
    }


    public void getHuanxin(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    names = EMContactManager.getInstance().getContactUserNames();
                    Message msg = new Message();
                    msg.what = 1002;
                    Bundle bundle = new Bundle();
                    bundle.putStringArrayList("names", (ArrayList<String>) names);
                    msg.setData(bundle);
                    handler.sendMessage(msg);
                } catch (EaseMobException e) {
                    e.printStackTrace();
                }

            }
        }).start();
    }

    @Override
    public void onFooterRefresh(PullToRefreshView view) {
        mPullToRefreshView.postDelayed(new Runnable() {

            @Override
            public void run() {
                mPullToRefreshView.onFooterRefreshComplete();
                loginHuanxin(UserNum.userNum, "admin");
            }
        }, 3000);
    }

    @Override
    public void onHeaderRefresh(PullToRefreshView view) {
        mPullToRefreshView.postDelayed(new Runnable() {

            @Override
            public void run() {

                mPullToRefreshView.onHeaderRefreshComplete();
                loginHuanxin(UserNum.userNum, "admin");
            }
        }, 3000);

    }

    public class FriendListAdapter extends BaseAdapter{
        ArrayList<String> names;
        LayoutInflater inflater;
        public FriendListAdapter(ArrayList<String>names){
            this.names=names;
            inflater=LayoutInflater.from(getActivity());
        }
        @Override
        public int getCount() {
            return names.size();
        }

        @Override
        public Object getItem(int position) {
            if(names.size()>0){
                return names.get(position);
            }
            return null;
        }
        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            view=inflater.inflate(R.layout.friend_list_item,null);
            name= (TextView)view.findViewById(R.id.friend_list_item_username);
            imagehead=(ImageView)view.findViewById(R.id.friend_avatar);
            EMConversation conversation = EMChatManager.getInstance().getConversation(names.get(position));
            String username=conversation.getUserName();
            getUserInfoHttp(username,name,imagehead);
            return view;
        }
    }

    private void getUserInfoHttp(String username, final TextView name, final ImageView imagehead) {
        String url = URLSet.serviceUrl+"/kaoban/userInformation/?userNum="+username;
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                if (statusCode == 200) {
                    InputStream xml = new ByteArrayInputStream(responseBody);
                    try {
                        userInf = getUserInf(xml);
                        String ava_username=userInf.getName();
                        String imagepath=userInf.getHeadImg();
                        name.setText(ava_username);
                        String url1 = URLSet.serviceUrl+ imagepath;
                        String str2="";
                        str2 = url1.replaceAll(" ", "");
                        URL url2= new URL(str2);
                        Glide.with(getActivity()).load(url2).into(imagehead);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }


            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

                System.out.println("---->error");
            }
        });
    }



    /**
     * 用户信息解析
     * @param xml  InputStream
     * @return UserInfoBean
     * @throws Exception
     */
    private UserInfoBean getUserInf(InputStream xml) throws Exception {

        UserInfoBean userInf = null;
        XmlPullParser parser = Xml.newPullParser(); // 利用Android的Xml工具类获取xmlPull解析器
        parser.setInput(xml, "UTF-8"); // 解析文件，设置字符集
        int event = parser.getEventType(); // 获取解析状态，返回的是int型数字状态
        while (event != XmlPullParser.END_DOCUMENT) {
            switch (event) {

                case XmlPullParser.START_TAG:
                    if ("Document".equals(parser.getName())) {
                        userInf = new UserInfoBean();
                    } else if ("name".equals(parser.getName())) {
                        userInf.setName(parser.nextText());
                    } else if ("number".equals(parser.getName())) {
                        userInf.setNumber(parser.nextText());
                    } else if ("sex".equals(parser.getName())) {
                        userInf.setSex(parser.nextText());
                    } else if ("birthday".equals(parser.getName())) {
                        userInf.setBirthday(parser.nextText());
                    } else if ("school".equals(parser.getName())) {
                        userInf.setSchool(parser.nextText());
                    } else if ("hometown".equals(parser.getName())) {
                        userInf.setHomeTown(parser.nextText());
                    } else if ("province".equals(parser.getName())) {
                        userInf.setProvince(parser.nextText());
                    } else if ("habit".equals(parser.getName())) {
                        userInf.setHabit(parser.nextText());
                    } else if ("job".equals(parser.getName())) {
                        userInf.setJob(parser.nextText());
                    } else if ("headImg".equals(parser.getName())) {
                        userInf.setHeadImg(parser.nextText());
                    }else if("photos".equals(parser.getName())){
                        userInf.setPhotos(parser.nextText());
                    }
                    break;
                case XmlPullParser.END_TAG:
                    break;
            }
            event = parser.next(); // 让指针指向下一个节点
        }
        return userInf;
    }

    public void UnReadMessage(){
        unread.setVisibility(View.VISIBLE);
        unread.setText("1");
        UnReadMessage.flag=false;
    }



}
