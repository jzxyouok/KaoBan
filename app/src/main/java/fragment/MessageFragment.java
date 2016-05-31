package fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Pair;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.Lc.ChatAllHistoryAdapter;
import com.com.easemob.chatuidemo.DemoApplication;
import com.com.easemob.chatuidemo.DemoHXSDKHelper;
import com.com.easemob.chatuidemo.adapter.ChatHistoryAdapter;
import com.dezhou.lsy.projectdezhoureal.ChatMessage;
import com.dezhou.lsy.projectdezhoureal.MainActivity;
import com.dezhou.lsy.projectdezhoureal.R;
import com.easemob.EMEventListener;
import com.easemob.EMNotifierEvent;
import com.easemob.applib.controller.HXSDKHelper;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContact;
import com.easemob.chat.EMContactManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.easemob.chat.EMMessage;
import com.easemob.chatuidemo.db.InviteMessgeDao;
import com.easemob.chatuidemo.domain.User;
import com.easemob.exceptions.EaseMobException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;


public class MessageFragment extends Fragment{
    private List<EMConversation> conversationList = new ArrayList<EMConversation>();
    private ChatAllHistoryAdapter adapter;
    private ListView listView;
    private boolean hidden;
    private InputMethodManager inputMethodManager;
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // 设置标题
        View v = inflater.inflate(R.layout.contactrecent, null);
        listView=(ListView)v.findViewById(R.id.listcontact);
        return v;

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        conversationList.addAll(loadConversationsWithRecentChat());
        adapter = new ChatAllHistoryAdapter(getActivity(), 1, conversationList);
        // 设置adapter
        listView.setAdapter(adapter);
        final String st2 = getResources().getString(R.string.Cant_chat_with_yourself);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                EMConversation conversation = adapter.getItem(position);
                String username = conversation.getUserName();
                if (username.equals(DemoApplication.getInstance().getUserName()))
                    Toast.makeText(getActivity(), st2, Toast.LENGTH_LONG).show();
                else {
                    // 进入聊天页面
                    Intent intent = new Intent(getActivity(), ChatMessage.class);

                    // it is single chat
                    intent.putExtra("userId", username);
                    intent.putExtra("username", username);
                    startActivity(intent);
                }
            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, final int i, long l) {
                new AlertDialog.Builder(getActivity())

                        .setTitle("删除消息")
                        .setItems(R.array.convaction,

                                new DialogInterface.OnClickListener() {
                                    EMConversation conversation = adapter.getItem(i);
                                    String user_name = conversation.getUserName();
                                    public void onClick(DialogInterface dialog,
                                                        int which) {


                                        String[] PK = getResources()
                                                .getStringArray(
                                                        R.array.convaction);
                                        Toast.makeText(
                                                getActivity(),
                                                PK[which], Toast.LENGTH_LONG)
                                                .show();

                                        if (PK[which].equals("删除会话")) {
                                            //删除和某个user的整个的聊天记录(包括本地)
                                            EMChatManager.getInstance().deleteConversation(user_name);
                                            adapter.remove(conversation);
                                            adapter.notifyDataSetChanged();
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
                return false;
            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        // if(((AdapterContextMenuInfo)menuInfo).position > 0){ m,
        getActivity().getMenuInflater().inflate(R.menu.delete_message, menu);
        // }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        boolean handled = false;
        boolean deleteMessage = false;
        if (item.getItemId() == R.id.delete_message) {
            deleteMessage = true;
            handled = true;
        } else if (item.getItemId() == R.id.delete_conversation) {
            deleteMessage = false;
            handled = true;
        }
        EMConversation tobeDeleteCons = adapter.getItem(((AdapterView.AdapterContextMenuInfo) item.getMenuInfo()).position);
        // 删除此会话
        EMChatManager.getInstance().deleteConversation(tobeDeleteCons.getUserName(), tobeDeleteCons.isGroup(), deleteMessage);
        InviteMessgeDao inviteMessgeDao = new InviteMessgeDao(getActivity());
        inviteMessgeDao.deleteMessage(tobeDeleteCons.getUserName());
        adapter.remove(tobeDeleteCons);
        adapter.notifyDataSetChanged();

//        // 更新消息未读数
//        ((MainActivity) getActivity()).updateUnreadLabel();

        return handled ? true : super.onContextItemSelected(item);
    }
    /**
     * 刷新页面
     */
    public void refresh() {
        conversationList.clear();
        conversationList.addAll(loadConversationsWithRecentChat());
        if(adapter != null)
            adapter.notifyDataSetChanged();
    }
    /**
     * 获取所有会话
     *
     * @param
     * @return
    +	 */
    private List<EMConversation> loadConversationsWithRecentChat() {
        // 获取所有会话，包括陌生人
        Hashtable<String, EMConversation> conversations = EMChatManager.getInstance().getAllConversations();
        // 过滤掉messages size为0的conversation
        /**
         * 如果在排序过程中有新消息收到，lastMsgTime会发生变化
         * 影响排序过程，Collection.sort会产生异常
         * 保证Conversation在Sort过程中最后一条消息的时间不变
         * 避免并发问题
         */
        List<Pair<Long, EMConversation>> sortList = new ArrayList<Pair<Long, EMConversation>>();
        synchronized (conversations) {
            for (EMConversation conversation : conversations.values()) {
                if (conversation.getAllMessages().size() != 0) {
                    //if(conversation.getType() != EMConversationType.ChatRoom){
                    sortList.add(new Pair<Long, EMConversation>(conversation.getLastMessage().getMsgTime(), conversation));
                    //}
                }
            }
        }
        try {
            // Internal is TimSort algorithm, has bug
            sortConversationByLastChatTime(sortList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<EMConversation> list = new ArrayList<EMConversation>();
        for (Pair<Long, EMConversation> sortItem : sortList) {
            list.add(sortItem.second);
        }
        return list;
    }

    /**
     * 根据最后一条消息的时间排序
     *
     * @param
     */
    private void sortConversationByLastChatTime(List<Pair<Long, EMConversation>> conversationList) {
        Collections.sort(conversationList, new Comparator<Pair<Long, EMConversation>>() {
            @Override
            public int compare(final Pair<Long, EMConversation> con1, final Pair<Long, EMConversation> con2) {

                if (con1.first == con2.first) {
                    return 0;
                } else if (con2.first > con1.first) {
                    return 1;
                } else {
                    return -1;
                }
            }

        });
    }
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        this.hidden = hidden;
        if (!hidden) {
            refresh();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!hidden) {
            refresh();
        }
    }


}