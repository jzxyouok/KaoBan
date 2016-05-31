package com.Lc;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import com.bumptech.glide.Glide;
import com.com.easemob.chatuidemo.Constant;
import com.com.easemob.chatuidemo.DemoHXSDKHelper;
import com.com.easemob.chatuidemo.utils.SmileUtils;
import com.dezhou.lsy.projectdezhoureal.R;
import com.easemob.applib.controller.HXSDKHelper;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.ImageMessageBody;
import com.easemob.chat.TextMessageBody;
import com.easemob.util.DateUtils;
import com.easemob.util.EMLog;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.apache.http.Header;
import org.xmlpull.v1.XmlPullParser;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import bean.UserInfoBean;
import utils.URLSet;

/**
 * 显示所有聊天记录adpater
 *
 */
public class ChatAllHistoryAdapter extends ArrayAdapter<EMConversation> {

    private static final String TAG = "ChatAllHistoryAdapter";
    private LayoutInflater inflater;
    private List<EMConversation> conversationList;
    private List<EMConversation> copyConversationList;
    private ConversationFilter conversationFilter;
    private boolean notiyfyByFilter;
    private UserInfoBean userInf;
    private HashMap<String,SoftReference<Bitmap>> imageCache=null;
    BitmapFactory.Options option = new BitmapFactory.Options();
    public Bitmap bitmap;
    private String ava_username,imagepath;
    private Context context;
    public ChatAllHistoryAdapter(Context context, int textViewResourceId, List<EMConversation> objects) {
        super(context, textViewResourceId, objects);
        this.conversationList = objects;
        this.context=context;
        copyConversationList = new ArrayList<EMConversation>();
        copyConversationList.addAll(objects);
        inflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.row_chat_history, parent, false);
        }
        ViewHolder holder = (ViewHolder) convertView.getTag();
        if (holder == null) {
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.unreadLabel = (TextView) convertView.findViewById(R.id.unread_msg_number);
            holder.message = (TextView) convertView.findViewById(R.id.message);
            holder.time = (TextView) convertView.findViewById(R.id.time);
            holder.avatar = (ImageView) convertView.findViewById(R.id.avatar);
            holder.msgState = convertView.findViewById(R.id.msg_state);
            holder.list_item_layout = (RelativeLayout) convertView.findViewById(R.id.list_item_layout);
            convertView.setTag(holder);
        }
        if (position % 2 == 0) {
            holder.list_item_layout.setBackgroundResource(R.drawable.mm_listitem);
        } else {
            holder.list_item_layout.setBackgroundResource(R.drawable.mm_listitem_grey);
        }

        // 获取与此用户/群组的会话
        EMConversation conversation = getItem(position);
        // 获取用户username或者群组groupid
        String username = conversation.getUserName();
        getUserInfoHttp(username,holder.name,holder.avatar);


        if (conversation.getUnreadMsgCount() > 0) {
            // 显示与此用户的消息未读数
            holder.unreadLabel.setText(String.valueOf(conversation.getUnreadMsgCount()));
            holder.unreadLabel.setVisibility(View.VISIBLE);
        } else {
            holder.unreadLabel.setVisibility(View.INVISIBLE);
        }

        if (conversation.getMsgCount() != 0) {
            // 把最后一条消息的内容作为item的message内容
            EMMessage lastMessage = conversation.getLastMessage();
            holder.message.setText(SmileUtils.getSmiledText(getContext(), getMessageDigest(lastMessage, (this.getContext()))),
                    BufferType.SPANNABLE);

            holder.time.setText(DateUtils.getTimestampString(new Date(lastMessage.getMsgTime())));
            if (lastMessage.direct == EMMessage.Direct.SEND && lastMessage.status == EMMessage.Status.FAIL) {
                holder.msgState.setVisibility(View.VISIBLE);
            } else {
                holder.msgState.setVisibility(View.GONE);
            }
        }

        return convertView;
    }

    private void getUserInfoHttp(String get_username, final TextView name, final ImageView imageView) {
        String url = URLSet.serviceUrl+"/kaoban/userInformation/?userNum="+get_username;
        AsyncHttpClient client = new AsyncHttpClient();
        client.get(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                if (statusCode == 200) {
                    InputStream xml = new ByteArrayInputStream(responseBody);
                    try {
                        userInf = getUserInf(xml);
                        ava_username=userInf.getName();
                        imagepath=userInf.getHeadImg();
                        name.setText(ava_username);
                        String url1 = URLSet.serviceUrl+ imagepath;
                        String str2="";
                        str2 = url1.replaceAll(" ", "");
                        URL url2 = new URL(str2);
                        Glide.with(context).load(url2).into(imageView);
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
     * 根据消息内容和消息类型获取消息内容提示
     *
     * @param message
     * @param context
     * @return
     */
    private String getMessageDigest(EMMessage message, Context context) {
        String digest = "";
        switch (message.getType()) {
            case LOCATION: // 位置消息
                if (message.direct == EMMessage.Direct.RECEIVE) {
                    // 从sdk中提到了ui中，使用更简单不犯错的获取string的方法
                    // digest = EasyUtils.getAppResourceString(context,
                    // "location_recv");
                    digest = getStrng(context, R.string.location_recv);
                    digest = String.format(digest, message.getFrom());
                    return digest;
                } else {
                    // digest = EasyUtils.getAppResourceString(context,
                    // "location_prefix");
                    digest = getStrng(context, R.string.location_prefix);
                }
                break;
            case IMAGE: // 图片消息
                ImageMessageBody imageBody = (ImageMessageBody) message.getBody();
                digest = getStrng(context, R.string.picture) + imageBody.getFileName();
                break;
            case VOICE:// 语音消息
                digest = getStrng(context, R.string.voice);
                break;
            case VIDEO: // 视频消息
                digest = getStrng(context, R.string.video);
                break;
            case TXT: // 文本消息

                if(((DemoHXSDKHelper)HXSDKHelper.getInstance()).isRobotMenuMessage(message)){
                    digest = ((DemoHXSDKHelper)HXSDKHelper.getInstance()).getRobotMenuMessageDigest(message);
                }else if(message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VOICE_CALL,false)){
                    TextMessageBody txtBody = (TextMessageBody) message.getBody();
                    digest = getStrng(context, R.string.voice_call) + txtBody.getMessage();
                }else{
                    TextMessageBody txtBody = (TextMessageBody) message.getBody();
                    digest = txtBody.getMessage();
                }
                break;
            case FILE: // 普通文件消息
                digest = getStrng(context, R.string.file);
                break;
            default:
                EMLog.e(TAG, "unknow type");
                return "";
        }

        return digest;
    }

    private static class ViewHolder {
        /** 和谁的聊天记录 */
        TextView name;
        /** 消息未读数 */
        TextView unreadLabel;
        /** 最后一条消息的内容 */
        TextView message;
        /** 最后一条消息的时间 */
        TextView time;
        /** 用户头像 */
        ImageView avatar;
        /** 最后一条消息的发送状态 */
        View msgState;
        /** 整个list中每一行总布局 */
        RelativeLayout list_item_layout;

    }
    String getStrng(Context context, int resId) {
        return context.getResources().getString(resId);
    }



    @Override
    public Filter getFilter() {
        if (conversationFilter == null) {
            conversationFilter = new ConversationFilter(conversationList);
        }
        return conversationFilter;
    }

    private class ConversationFilter extends Filter {
        List<EMConversation> mOriginalValues = null;

        public ConversationFilter(List<EMConversation> mList) {
            mOriginalValues = mList;
        }

        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();

            if (mOriginalValues == null) {
                mOriginalValues = new ArrayList<EMConversation>();
            }
            if (prefix == null || prefix.length() == 0) {
                results.values = copyConversationList;
                results.count = copyConversationList.size();
            } else {
                String prefixString = prefix.toString();
                final int count = mOriginalValues.size();
                final ArrayList<EMConversation> newValues = new ArrayList<EMConversation>();

                for (int i = 0; i < count; i++) {
                    final EMConversation value = mOriginalValues.get(i);
                    String username = value.getUserName();

                    EMGroup group = EMGroupManager.getInstance().getGroup(username);
                    if(group != null){
                        username = group.getGroupName();
                    }

                    // First match against the whole ,non-splitted value
                    if (username.startsWith(prefixString)) {
                        newValues.add(value);
                    } else{
                        final String[] words = username.split(" ");
                        final int wordCount = words.length;

                        // Start at index 0, in case valueText starts with space(s)
                        for (int k = 0; k < wordCount; k++) {
                            if (words[k].startsWith(prefixString)) {
                                newValues.add(value);
                                break;
                            }
                        }
                    }
                }

                results.values = newValues;
                results.count = newValues.size();
            }
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            conversationList.clear();
            conversationList.addAll((List<EMConversation>) results.values);
            if (results.count > 0) {
                notiyfyByFilter = true;
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }

        }

    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        if(!notiyfyByFilter){
            copyConversationList.clear();
            copyConversationList.addAll(conversationList);
            notiyfyByFilter = false;
        }
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
//						String str=parser.getAttributeValue(0);
//						int index=str.indexOf(",");
//						str=str.substring(index+1);
//						byte[] decode = Base64.decode(str.toString(), Base64.DEFAULT);
//						Bitmap bitmap = BitmapFactory.decodeByteArray(decode, 0, decode.length);
//						headImg.setImageBitmap(bitmap);
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


}

