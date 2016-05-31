package kaobanxml;

import android.util.Log;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import bean.ArticalBean;
import bean.Classification;
import bean.Comment;
import bean.Item;
import bean.SecondComment;
import bean.SimplePeopleBean;
import bean.Topic;
import bean.VersionInfo;
import network.FileDown;
import network.Friend;
import network.GalleryBean;
import network.UserInfoGet;
import tempvalue.Exam;
import tempvalue.HighExamItem;
import tempvalue.ItemContent;
import utils.URLSet;

/**
 * Created by lsy on 15-2-23.
 */
public class XmlParser {
    // SAX解析相关
    private static SAXParserFactory spFactory = null;
    private static SAXParser sParser = null;

    private static XMLUserInfoHandler xmlUserInfoHandler;

    private static XMLFriend xmlFri;
    //    private static XMLDocInfo xmlDocInfo;
    private static XmlExamGetHandler xmlExamGetHandler;
    private static XmlSimpleGetHandler xmlSimpleGetHandler;
    private static XmlClassificationGetHandler xmlClassificationGetHandler;
    private static XmlTopicGetHandler xmlTopicGetHandler;
    private static XmlArticalsGetHandler xmlArticalsGetHandler;
    private static XmlSchoolGetHandler xmlSchoolGetHandler;
    private static XmlGetResultHandler xmlGetResultHandler;
    private static XmlGetNewTopicImagesHandler xmlGetNewTopicImagesHandler;
    private static XmlArticalCommentsGetHandler xmlArticalCommentsGetHandler;
    // PULL解析相关
    private static XmlPullParserFactory factory;
    private static XmlPullParser xmlPullParser;
    private static xmlVersionInfoHandler versionInfoHandler;
    public static VersionInfo xmlVersionInfo(String versionInfo){
        if (spFactory == null) {
            spFactory = SAXParserFactory.newInstance();
        }
        try {
            if (sParser == null) {
                sParser = spFactory.newSAXParser();
            }
            versionInfoHandler = new xmlVersionInfoHandler();
            ByteArrayInputStream inputStream = new ByteArrayInputStream(versionInfo.getBytes("UTF-8"));
            sParser.parse(inputStream, versionInfoHandler); //　通过UTF-8方式
            inputStream.close();
            return versionInfoHandler.getVersionInfo();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            return null;
        } catch (SAXException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 简单ｘｍｌ信息的解析方法
     * 解析格式：
     * 成功：<?xml version="1.0" encoding="utf-8" ?>
     * <result>success</result>
     * 失败：<?xml version="1.0" encoding="utf-8" ?>
     * <result>fail</result>
     * @param content 需要解析的文本
     * return　String　返回解析到的内容
     */
    public static String xmlParserGet(String content){
        String tmp = null;
        try {
            factory = XmlPullParserFactory.newInstance();
            // 实例化解析器
            xmlPullParser = factory.newPullParser();
            xmlPullParser.setInput(new StringReader(content));
            int event = xmlPullParser.getEventType();
            while(event != xmlPullParser.END_DOCUMENT){
                if(event == XmlPullParser.TEXT){
                    tmp = xmlPullParser.getText();
                    Log.d("TestApp", "解析所得内容为：" + tmp);
                }
                event = xmlPullParser.next();
            }
        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return tmp;
    }
    private  static class xmlVersionInfoHandler extends DefaultHandler{

        private StringBuilder builder;
        private VersionInfo versionInfo;


        public VersionInfo getVersionInfo() {
            return versionInfo;
        }

        @Override
        public void startDocument() throws SAXException {
            super.startDocument();
            builder = new StringBuilder();
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            super.startElement(uri, localName, qName, attributes);
            if ("Document".equals(localName)) {
                Log.d("TestApp", "开始解析");
                versionInfo=new VersionInfo();
                builder.setLength(0);
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            super.characters(ch, start, length);
            builder.append(ch, start, length);
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            super.endElement(uri, localName, qName);

            if("name".equals(localName)){
                versionInfo.setVersionCode(Integer.parseInt(builder.toString().trim()));
                builder.setLength(0);
            }
            if("version".equals(localName)){
                versionInfo.setVersionName(builder.toString().trim());
                builder.setLength(0);
            }
            if("path".equals(localName)){
                versionInfo.setNewVersionPath(URLSet.serviceUrl+builder.toString().trim());
                builder.setLength(0);
            }
            if("new".equals(localName)){
                builder.setLength(0);
            }
            if("Document".equals(localName)){
//                classifications.add(classification);

            }

        }

        @Override
        public void endDocument() throws SAXException {
            super.endDocument();
            builder = null;
        }










    }





   /* *//**
     * ｘｍｌ解析的方法,解析用户信息获取时使用
     * @param xmlInfo　需要解析的ｘｍｌ文本
     */
    public static List<UserInfoGet> xmlUserInfoParserGet(String xmlInfo){
        if (spFactory == null) {
            spFactory = SAXParserFactory.newInstance();
        }
        try {
            if (sParser == null) {
                sParser = spFactory.newSAXParser();
            }
            xmlUserInfoHandler = new XMLUserInfoHandler();
            ByteArrayInputStream inputStream = new ByteArrayInputStream(xmlInfo.getBytes("UTF-8"));
            sParser.parse(inputStream,xmlUserInfoHandler); //　通过UTF-8方式
            inputStream.close();
            return xmlUserInfoHandler.getUserInfoList();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            return null;
        } catch (SAXException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static class XMLUserInfoHandler extends DefaultHandler {

        private static UserInfoGet userInfoGet = null;
        private static List<UserInfoGet> listUserInfoGet;
        private StringBuilder builder;

        public List<UserInfoGet> getUserInfoList(){
            return listUserInfoGet;
        }

        @Override
        public void startDocument() throws SAXException {
            // 初始化工作
            listUserInfoGet = new ArrayList<UserInfoGet>();
            builder = new StringBuilder();
            Log.d("TestApp","初始化解析器");
        }

        @Override
        public void startElement(String uri, String localName,
                                 String qName, Attributes attributes) throws SAXException {
            if("Document".equals(localName)){
                userInfoGet = new UserInfoGet();
                Log.d("TestApp","开始解析Ｄｏｕｃｕｍｅｎｔ");
            } else if("headImg".equals(localName)){
                userInfoGet.setHeadImg(attributes.getValue("src"));
            } else if("retsult".equals(localName)){
                // 获取数据失败
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            builder.append(ch, start, length);
            Log.d("TestApp", "内容获取" + builder.toString().trim());
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            Log.d("TestApp", "开始分配读入信息！..." + localName + "...builder+" + builder.toString());
            if("name".equals(localName)){
                userInfoGet.setName(builder.toString().trim());
                builder.setLength(0);
            } else if("number".equals(localName)){
                userInfoGet.setNumber(builder.toString().trim());
                builder.setLength(0);
            } else if("sex".equals(localName)){
                userInfoGet.setSex(builder.toString().trim());
                builder.setLength(0);
            } else if("birthday".equals(localName)){
                userInfoGet.setBirthday(builder.toString().trim());
                builder.setLength(0);
            } else if("school".equals(localName)){
                userInfoGet.setSchool(builder.toString().trim());
                builder.setLength(0);
            } else if("hometown".equals(localName)){
                userInfoGet.setHometown(builder.toString().trim());
                builder.setLength(0);
            } else if("province".equals(localName)){
                userInfoGet.setProvince(builder.toString().trim());
                builder.setLength(0);
            } else if("habit".equals(localName)){
                userInfoGet.setHabit(builder.toString().trim());
                builder.setLength(0);
            } else if("job".equals(localName)){
                userInfoGet.setJob(builder.toString().trim());
                builder.setLength(0);
            } else if("Document".equals(localName)){
                Log.d("TestApp", "最终添加到Ｌｉｓｔ.." + userInfoGet.toString());
                listUserInfoGet.add(userInfoGet);
                Log.d("TestApp", "最终完成.." + listUserInfoGet.toString());
            } else {
                Log.d("TestApp","停止！");
            }
        }

        @Override
        public void endDocument() throws SAXException {
            //listUserInfoGet.clear();
            userInfoGet = null;
            builder = null;
        }
    }

    public static ArrayList<FileDown> xmlFileDown(String xmlInfo) {
        if (spFactory == null) {
            spFactory = SAXParserFactory.newInstance();
        }
        try {
            if (sParser == null) {
                sParser = spFactory.newSAXParser();
            }
            XMLFileDown xmlFile = new XMLFileDown();
            ByteArrayInputStream inputStream = new ByteArrayInputStream(xmlInfo.getBytes("UTF-8"));
            sParser.parse(inputStream, xmlFile); //　通过UTF-8方式
            inputStream.close();
            return xmlFile.getFileDown();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            return null;
        } catch (SAXException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static class XMLFileDown extends DefaultHandler {
        private static ArrayList<FileDown> listFri = null;
        private static FileDown fri = null;
        private static StringBuilder builder;

        public ArrayList<FileDown> getFileDown() {
            return listFri;
        }

        @Override
        public void startDocument() throws SAXException {
            super.startDocument();
            listFri = new ArrayList<FileDown>();
            builder = new StringBuilder();
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            super.startElement(uri, localName, qName, attributes);
            if ("file".equals(localName)) {
                fri = new FileDown();
//                builder.setLength(0);
            }
//            else if ("headImg".equals(localName)) {
//                fri.setHeadImg(attributes.getValue("src"));
//            }
            else {
                Log.d("TestApp", "获取到其他标签!" + localName);
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            super.characters(ch, start, length);
            builder.append(ch, start, length);
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            super.endElement(uri, localName, qName);
            if ("userName".equals(localName)) {
                fri.setUserName(builder.toString().trim());
                builder.setLength(0);
            } else if ("headImg".equals(localName)) {
                fri.setHeadImg(builder.toString().trim());
                builder.setLength(0);
            }
            else if ("userNum".equals(localName)) {
                fri.setUserNum(builder.toString().trim());
                builder.setLength(0);
            }
            else if ("title".equals(localName)) {
                fri.setTitle(builder.toString().trim());
                builder.setLength(0);
            }
            else if ("filepath".equals(localName)) {
                fri.setFile(builder.toString().trim());
                builder.setLength(0);
            }
            else if ("docNum".equals(localName)) {
                fri.setDocNum(builder.toString().trim());
                builder.setLength(0);
            }
            else if ("school".equals(localName)) {
                fri.setSchool(builder.toString().trim());
                builder.setLength(0);
            }
            else if ("date".equals(localName)) {
                fri.setDate(builder.toString().trim());
                builder.setLength(0);
            }
            else if ("file".equals(localName)) {
                listFri.add(fri);
            } else if ("Document".equals(localName)) {
                Log.d("TEstApp", "解析完毕");
            } else {
                Log.d("TEstApp", "未解析的标签");
            }
        }

        @Override
        public void endDocument() throws SAXException {
            super.endDocument();
            fri = null;
            builder = null;
        }
    }
    public static ArrayList<GalleryBean> xmlGallery(String xmlInfo) {
        if (spFactory == null) {
            spFactory = SAXParserFactory.newInstance();
        }
        try {
            if (sParser == null) {
                sParser = spFactory.newSAXParser();
            }
            XMLGallery xmlGallery = new XMLGallery();
            ByteArrayInputStream inputStream = new ByteArrayInputStream(xmlInfo.getBytes("UTF-8"));
            sParser.parse(inputStream, xmlGallery); //　通过UTF-8方式
            inputStream.close();
            return xmlGallery.getGallery();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            return null;
        } catch (SAXException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    private static class XMLGallery extends DefaultHandler {
        private static ArrayList<GalleryBean> listFri = null;
        private static GalleryBean fri = null;
        private static StringBuilder builder;

        public ArrayList<GalleryBean> getGallery() {
            return listFri;
        }

        @Override
        public void startDocument() throws SAXException {
            super.startDocument();
            listFri = new ArrayList<GalleryBean>();
            builder = new StringBuilder();
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            super.startElement(uri, localName, qName, attributes);
            if ("topic".equals(localName)) {
                fri = new GalleryBean();
//                builder.setLength(0);
            }
//            else if ("headImg".equals(localName)) {
//                fri.setHeadImg(attributes.getValue("src"));
//            }
            else {
                Log.d("TestApp", "获取到其他标签!" + localName);
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            super.characters(ch, start, length);
            builder.append(ch, start, length);
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            super.endElement(uri, localName, qName);
            if ("topicName".equals(localName)) {
                fri.setTopicName(builder.toString().trim());
                builder.setLength(0);
            } else if ("topicNum".equals(localName)) {
                fri.setTopicNum(builder.toString().trim());
                builder.setLength(0);
            }
            else if ("topicPic".equals(localName)) {
                fri.setTopicPic(builder.toString().trim());
                builder.setLength(0);
            }
            else if ("articalsNum".equals(localName)) {
                fri.setArticalsNum(builder.toString().trim());
                builder.setLength(0);
            }
            else if ("topic".equals(localName)) {
                listFri.add(fri);
            } else if ("Document".equals(localName)) {
                Log.d("TEstApp", "解析完毕");
            } else {
                Log.d("TEstApp", "未解析的标签");
            }
        }

        @Override
        public void endDocument() throws SAXException {
            super.endDocument();
            fri = null;
            builder = null;
        }
    }
   /* *//**
     * ｘｍｌ解析的方法,解析获取好友列表，关注的人列表以及我的粉丝列表时使用
     *
     * @param xmlInfo 　需要解析的ｘｍｌ文本
     */
    public static ArrayList<Friend> xmlFriParserGet(String xmlInfo) {
        if (spFactory == null) {
            spFactory = SAXParserFactory.newInstance();
        }
        try {
            if (sParser == null) {
                sParser = spFactory.newSAXParser();
            }
            xmlFri = new XMLFriend();
            ByteArrayInputStream inputStream = new ByteArrayInputStream(xmlInfo.getBytes("UTF-8"));
            sParser.parse(inputStream, xmlFri); //　通过UTF-8方式
            inputStream.close();
            return xmlFri.getMyFriends();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            return null;
        } catch (SAXException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static class XMLFriend extends DefaultHandler {
        private static ArrayList<Friend> listFri = null;
        private static Friend fri = null;
        private static StringBuilder builder;

        public ArrayList<Friend> getMyFriends() {
            return listFri;
        }

        @Override
        public void startDocument() throws SAXException {
            super.startDocument();
            listFri = new ArrayList<Friend>();
            builder = new StringBuilder();
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            super.startElement(uri, localName, qName, attributes);
            if ("friend".equals(localName)) {
                fri = new Friend();
                builder.setLength(0);
            } else if ("headImg".equals(localName)) {
                fri.setHeadImg(attributes.getValue("src"));
            } else {
                Log.d("TestApp", "获取到其他标签!" + localName);
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            super.characters(ch, start, length);
            builder.append(ch, start, length);
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            super.endElement(uri, localName, qName);
            if ("userNum".equals(localName)) {
                fri.setUserNum(builder.toString().trim());
                builder.setLength(0);
            } else if ("userName".equals(localName)) {
                fri.setUserName(builder.toString().trim());
                builder.setLength(0);
            } else if ("friend".equals(localName)) {
                listFri.add(fri);
            } else if ("Document".equals(localName)) {
                Log.d("TEstApp", "解析完毕");
            } else {
                Log.d("TEstApp", "未解析的标签");
            }
        }

        @Override
        public void endDocument() throws SAXException {
            super.endDocument();
            fri = null;
            builder = null;
        }
    }

   /* *//**
     * ｘｍｌ解析的方法,解析获取相关的考试名称
     *
     * @param xmlInfo 　需要解析的ｘｍｌ文本
     */
    public static ArrayList<Exam> xmlExamsGet(String xmlInfo) {
        if (spFactory == null) {
            spFactory = SAXParserFactory.newInstance();
        }
        try {
            if (sParser == null) {
                sParser = spFactory.newSAXParser();
            }
            xmlExamGetHandler = new XmlExamGetHandler();
            ByteArrayInputStream inputStream = new ByteArrayInputStream(xmlInfo.getBytes("UTF-8"));
            sParser.parse(inputStream, xmlExamGetHandler); //　通过UTF-8方式
            inputStream.close();
            return xmlExamGetHandler.getExams();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            return null;
        } catch (SAXException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

       /* *//**
     * ｘｍｌ解析的方法,解析获取相关的考试名称
     *
     * @param xmlInfo 　需要解析的ｘｍｌ文本
     */
    public static ArrayList<Exam> xmlMyExamsGet(String xmlInfo) {
        if (spFactory == null) {
            spFactory = SAXParserFactory.newInstance();
        }
        try {
            if (sParser == null) {
                sParser = spFactory.newSAXParser();
            }
            XmlMyExamGetHandler xmlExamGetHandler = new XmlMyExamGetHandler();
            ByteArrayInputStream inputStream = new ByteArrayInputStream(xmlInfo.getBytes("UTF-8"));
            sParser.parse(inputStream, xmlExamGetHandler); //　通过UTF-8方式
            inputStream.close();
            return xmlExamGetHandler.getExams();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            return null;
        } catch (SAXException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static ArrayList<Item> xmlSchoolsGet(String xmlInfo) {
        if (spFactory == null) {
            spFactory = SAXParserFactory.newInstance();
        }
        try {
            if (sParser == null) {
                sParser = spFactory.newSAXParser();
            }
            xmlSchoolGetHandler = new XmlSchoolGetHandler();
            ByteArrayInputStream inputStream = new ByteArrayInputStream(xmlInfo.getBytes("UTF-8"));
            sParser.parse(inputStream, xmlSchoolGetHandler); //　通过UTF-8方式
            inputStream.close();
            return xmlSchoolGetHandler.getItems();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            return null;
        } catch (SAXException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }



    public static List<SimplePeopleBean> xmlSimpleGet(String simpleString){
        if (spFactory == null) {
            spFactory = SAXParserFactory.newInstance();
        }
        try {
            if (sParser == null) {
                sParser = spFactory.newSAXParser();
            }
            xmlSimpleGetHandler = new XmlSimpleGetHandler();
            ByteArrayInputStream inputStream = new ByteArrayInputStream(simpleString.getBytes("UTF-8"));
            sParser.parse(inputStream, xmlSimpleGetHandler); //　通过UTF-8方式
            inputStream.close();
            return xmlSimpleGetHandler.getSimplePeoples();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            return null;
        } catch (SAXException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    public static List<Classification> xmlClassificationGet(String topicStr){
        if (spFactory == null) {
            spFactory = SAXParserFactory.newInstance();
        }
        try {
            if (sParser == null) {
                sParser = spFactory.newSAXParser();
            }
            xmlClassificationGetHandler = new XmlClassificationGetHandler();
            ByteArrayInputStream inputStream = new ByteArrayInputStream(topicStr.getBytes("UTF-8"));
            sParser.parse(inputStream, xmlClassificationGetHandler); //　通过UTF-8方式
            inputStream.close();
            return xmlClassificationGetHandler.getClassifications();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            return null;
        } catch (SAXException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
    public static List<Topic> xmlTopicsGet(String topicStr){
        if (spFactory == null) {
            spFactory = SAXParserFactory.newInstance();
        }
        try {
            if (sParser == null) {
                sParser = spFactory.newSAXParser();
            }
            xmlTopicGetHandler = new XmlTopicGetHandler();
            ByteArrayInputStream inputStream = new ByteArrayInputStream(topicStr.getBytes("UTF-8"));
            sParser.parse(inputStream, xmlTopicGetHandler); //　通过UTF-8方式
            inputStream.close();
            return xmlTopicGetHandler.getTopics();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            return null;
        } catch (SAXException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<ArticalBean> xmlArticalsGet(String topicStr){
        if (spFactory == null) {
            spFactory = SAXParserFactory.newInstance();
        }
        try {
            if (sParser == null) {
                sParser = spFactory.newSAXParser();
            }
            xmlArticalsGetHandler = new XmlArticalsGetHandler();
            ByteArrayInputStream inputStream = new ByteArrayInputStream(topicStr.getBytes("UTF-8"));
            sParser.parse(inputStream, xmlArticalsGetHandler); //　通过UTF-8方式
            inputStream.close();
            return xmlArticalsGetHandler.getArticals();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            return null;
        } catch (SAXException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static List<String> xmlImagesString(String newTopicImageStr){
        if (spFactory == null) {
            spFactory = SAXParserFactory.newInstance();
        }
        try {
            if (sParser == null) {
                sParser = spFactory.newSAXParser();
            }
            xmlGetNewTopicImagesHandler = new XmlGetNewTopicImagesHandler();
            ByteArrayInputStream inputStream = new ByteArrayInputStream(newTopicImageStr.getBytes("UTF-8"));
            sParser.parse(inputStream, xmlGetNewTopicImagesHandler); //　通过UTF-8方式
            inputStream.close();
            return xmlGetNewTopicImagesHandler.getList();
//            return xmlGetResultHandler.getResult();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            return null;
        } catch (SAXException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    };
    public static String xmlResultGet(String xmlGetResultStr){
        if (spFactory == null) {
            spFactory = SAXParserFactory.newInstance();
        }
        try {
            if (sParser == null) {
                sParser = spFactory.newSAXParser();
            }
            xmlGetResultHandler = new XmlGetResultHandler();
            ByteArrayInputStream inputStream = new ByteArrayInputStream(xmlGetResultStr.getBytes("UTF-8"));
            sParser.parse(inputStream, xmlGetResultHandler); //　通过UTF-8方式
            inputStream.close();
            return xmlGetResultHandler.getResult();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            return null;
        } catch (SAXException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }





    public static List<Comment> xmlAticalCommentsGet(String topicStr){
        if (spFactory == null) {
            spFactory = SAXParserFactory.newInstance();
        }
        try {
            if (sParser == null) {
                sParser = spFactory.newSAXParser();
            }
            xmlArticalCommentsGetHandler = new XmlArticalCommentsGetHandler();
            ByteArrayInputStream inputStream = new ByteArrayInputStream(topicStr.getBytes("UTF-8"));
            sParser.parse(inputStream, xmlArticalCommentsGetHandler); //　通过UTF-8方式
            inputStream.close();
            return xmlArticalCommentsGetHandler.getComments();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            return null;
        } catch (SAXException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static class XmlSchoolGetHandler extends DefaultHandler {
        private Item item = null;
        private ArrayList<Item> items = null;
        private StringBuilder builder;

        public ArrayList<Item> getItems() {
            return items;
        }

        @Override
        public void startDocument() throws SAXException {
            super.startDocument();
            items = new ArrayList<Item>();
            builder = new StringBuilder();
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            super.startElement(uri, localName, qName, attributes);
            if ("Document".equals(localName)) {
                Log.d("TestApp", "开始解析");
            } else if ("item".equals(localName)) {
                item = new Item();
                Log.d("TestApp", "开始解析" + localName);
            } else {

            }
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            super.characters(ch, start, length);
            builder.append(ch, start, length);
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            super.endElement(uri, localName, qName);
            if ("item".equals(localName)) {
                item.setItem(builder.toString().trim());
                builder.setLength(0);
                items.add(item);
                Log.d("TestApp", "解析" + localName + "..内容" + builder.toString());
            } else if ("Document".equals(localName)) {
                Log.d("TestApp", "停止解析" + localName);
            } else {

            }
        }

        @Override
        public void endDocument() throws SAXException {
            super.endDocument();
            item = null;
            builder = null;
        }
    }

    private static class XmlExamGetHandler extends DefaultHandler {
        private Exam exam = null;
        private ArrayList<Exam> exams = null;
        private StringBuilder builder;

        public ArrayList<Exam> getExams() {
            return exams;
        }

        @Override
        public void startDocument() throws SAXException {
            super.startDocument();
            exams = new ArrayList<>();
            builder = new StringBuilder();
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            super.startElement(uri, localName, qName, attributes);
            if ("Document".equals(localName)) {
                Log.d("TestApp", "开始解析");
            } else if ("exam".equals(localName)) {
                exam = new Exam();
                Log.d("TestApp", "开始解析" + localName);
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            super.characters(ch, start, length);
            builder.append(ch, start, length);
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            super.endElement(uri, localName, qName);
            if ("exam".equals(localName)) {
                exam.setExam(builder.toString().trim());
                builder.setLength(0);
                exams.add(exam);
                Log.d("TestApp", "解析" + localName + "..内容" + builder.toString());
            }

        }

        @Override
        public void endDocument() throws SAXException {
            super.endDocument();
            exam = null;
            builder = null;
        }
    }

    private static class XmlMyExamGetHandler extends DefaultHandler {
        private Exam exam = null;
        private ArrayList<Exam> exams = null;
        private StringBuilder builder;

        public ArrayList<Exam> getExams() {
            return exams;
        }

        @Override
        public void startDocument() throws SAXException {
            super.startDocument();
            exams = new ArrayList<>();
            builder = new StringBuilder();
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            super.startElement(uri, localName, qName, attributes);
            if ("Document".equals(localName)) {
                Log.d("TestApp", "开始解析");
            } else if ("exam".equals(localName)) {
                exam = new Exam();
                Log.d("TestApp", "开始解析" + localName);
            } else {

            }
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            super.characters(ch, start, length);
            builder.append(ch, start, length);
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            super.endElement(uri, localName, qName);
            if ("exam".equals(localName)) {
                builder.setLength(0);
                exams.add(exam);
            } else if ("etime".equals(localName)) {
                exam.setEtime(builder.toString().trim());
                builder.setLength(0);
            } else if ("rtime".equals(localName)){
                exam.setRtime(builder.toString().trim());
                builder.setLength(0);
            } else if ("name".equals(localName)){
                exam.setExam(builder.toString().trim());
                builder.setLength(0);
            }
        }

        @Override
        public void endDocument() throws SAXException {
            super.endDocument();
            exam = null;
            builder = null;
        }
    }

    private static class XmlSimpleGetHandler extends DefaultHandler {
        private SimplePeopleBean simplePeople = null;
        private ArrayList<SimplePeopleBean> simplePeoples = null;
        private StringBuilder builder;

        public ArrayList<SimplePeopleBean> getSimplePeoples() {
            return simplePeoples;
        }

        @Override
        public void startDocument() throws SAXException {
            super.startDocument();
            simplePeoples = new ArrayList<SimplePeopleBean>();
            builder = new StringBuilder();
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            super.startElement(uri, localName, qName, attributes);
            if ("Document".equals(localName)) {
                Log.d("TestApp", "开始解析");
            } else if ("user".equals(localName)) {
                simplePeople = new SimplePeopleBean();
                Log.d("TestApp", "开始解析" + localName);
            } else {

            }
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            super.characters(ch, start, length);
            builder.append(ch, start, length);
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            super.endElement(uri, localName, qName);
            if ("number".equals(localName)) {
                simplePeople.setNumber(builder.toString().trim());
                builder.setLength(0);

                Log.d("TestApp", "解析" + localName + "..内容" + builder.toString());
            }
            if ("name".equals(localName)) {
                simplePeople.setName(builder.toString().trim());
                builder.setLength(0);

                Log.d("TestApp", "解析" + localName + "..内容" + builder.toString());
            }
            if ("sex".equals(localName)) {
                simplePeople.setSex(builder.toString().trim());
                builder.setLength(0);

                Log.d("TestApp", "解析" + localName + "..内容" + builder.toString());
            }
            if ("school".equals(localName)) {
                simplePeople.setSchool(builder.toString().trim());
                builder.setLength(0);

                Log.d("TestApp", "解析" + localName + "..内容" + builder.toString());
            }
            if ("exam".equals(localName)) {
                simplePeople.setExam(builder.toString().trim());
                builder.setLength(0);

                Log.d("TestApp", "解析" + localName + "..内容" + builder.toString());
            }
            if ("sign".equals(localName)) {
                simplePeople.setSign(builder.toString().trim());
                builder.setLength(0);

                Log.d("TestApp", "解析" + localName + "..内容" + builder.toString());
            }
            if ("brithday".equals(localName)) {
                simplePeople.setBirthday(builder.toString().trim());
                builder.setLength(0);

                Log.d("TestApp", "解析" + localName + "..内容" + builder.toString());
            }
            if ("headimg".equals(localName)) {
                simplePeople.setHeadUrl(URLSet.serviceUrl+builder.toString().trim());
                builder.setLength(0);

                Log.d("TestApp", "解析" + localName + "..内容" + builder.toString());
            }
            if("user".equals(localName)){
                simplePeoples.add(simplePeople);
            }
            if ("Document".equals(localName)) {
//                simplePeoples.add(simplePeople);
                Log.d("TestApp", "停止解析" + localName);
            } else {

            }
        }

        @Override
        public void endDocument() throws SAXException {
            super.endDocument();
            simplePeople = null;
            builder = null;
        }
    }


    private static class XmlClassificationGetHandler extends DefaultHandler {
        private Classification classification = null;
        private ArrayList<Classification> classifications = null;
        private StringBuilder builder;
        private Topic topic;
        private ArrayList<Topic> topics;
        private int classificationSerial=0;

        public ArrayList<Classification> getClassifications() {
            return classifications;
        }

        @Override
        public void startDocument() throws SAXException {
            super.startDocument();
            classifications=new ArrayList<Classification>();

            builder = new StringBuilder();
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            super.startElement(uri, localName, qName, attributes);
            if ("Document".equals(localName)) {
                Log.d("TestApp", "开始解析");

            }
            if ("classification".equals(localName)) {
                topics=new ArrayList<Topic>();
                classification = new Classification();
                classification.setSerial(classificationSerial);
                classificationSerial++;
                Log.d("TestApp", "开始解析" + localName);
            }

            if("topic".equals(localName)){
                topic=new Topic();
                topic.setSerial(classificationSerial);
                classificationSerial++;
                Log.d("TestApp", "开始解析" + localName);
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            super.characters(ch, start, length);
            builder.append(ch, start, length);
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            super.endElement(uri, localName, qName);
            if("classificationName".equals(localName)){
                classification.setClassificationName(builder.toString().trim());

                builder.setLength(0);
            }
            if("topicPic".equals(localName)){
                topic.setTopicPic(URLSet.serviceUrl + builder.toString().trim());
                builder.setLength(0);
            }
            if("topicName".equals(localName)){
                topic.setTopicName(builder.toString().trim());
                builder.setLength(0);
            }
            if("topicNum".equals(localName)){
                topic.setTopicNum(builder.toString().trim());
                builder.setLength(0);
            }
            if("articalsNum".equals(localName)){
                topic.setContentNum(builder.toString().trim());
                builder.setLength(0);
            }
            if("topic".equals(localName)){
                topics.add(topic);
            }
            if("classification".equals(localName)){
                classification.setTopics(topics);
                classifications.add(classification);
                builder.setLength(0);
            }
            if("Document".equals(localName)){
//                classifications.add(classification);

            }

        }

        @Override
        public void endDocument() throws SAXException {
            super.endDocument();
            classification = null;
            builder = null;
        }
    }
    private static class XmlTopicGetHandler extends DefaultHandler {

        private StringBuilder builder;
        private Topic topic;
        private List<Topic> topics;
        private int classificationSerial=0;

        public List<Topic> getTopics() {
            return topics;
        }

        @Override
        public void startDocument() throws SAXException {
            super.startDocument();

            builder = new StringBuilder();
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            super.startElement(uri, localName, qName, attributes);
            if ("Document".equals(localName)) {
                Log.d("TestApp", "开始解析");
                topics=new ArrayList<Topic>();
            }


            if("topic".equals(localName)){
                topic=new Topic();

                Log.d("TestApp", "开始解析" + localName);
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            super.characters(ch, start, length);
            builder.append(ch, start, length);
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            super.endElement(uri, localName, qName);

            if("topicName".equals(localName)){
                topic.setTopicName(builder.toString().trim());
                builder.setLength(0);
            }
            if("topicNum".equals(localName)){
                topic.setTopicNum(builder.toString().trim());
                builder.setLength(0);
            }
            if("articalsNum".equals(localName)){
                topic.setContentNum(builder.toString().trim());
                builder.setLength(0);
            }
            if("topicPic".equals(localName)){
                topic.setTopicPic(URLSet.serviceUrl + builder.toString().trim());
                builder.setLength(0);
            }
            if("topic".equals(localName)){
                topics.add(topic);
            }

            if("Document".equals(localName)){
//                classifications.add(classification);

            }

        }

        @Override
        public void endDocument() throws SAXException {
            super.endDocument();
            topic=null;
            builder = null;
        }
    }



    private static class XmlArticalsGetHandler extends DefaultHandler {

        private StringBuilder builder;

        private List<ArticalBean> articals;
        private ArticalBean artical;


        public List<ArticalBean> getArticals() {
            return articals;
        }

        @Override
        public void startDocument() throws SAXException {
            super.startDocument();

            builder = new StringBuilder();
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            super.startElement(uri, localName, qName, attributes);
            if ("Document".equals(localName)) {
                Log.d("TestApp", "开始解析");
                articals=new ArrayList<ArticalBean>();
            }


            if("artical".equals(localName)){
                artical=new ArticalBean();

                Log.d("TestApp", "开始解析" + localName);
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            super.characters(ch, start, length);
            builder.append(ch, start, length);
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            super.endElement(uri, localName, qName);


            if("smimg".equals(localName)){
                artical.setSmimg(builder.toString().trim());
                builder.setLength(0);
            }
            if("store".equals(localName)){
                artical.setStoreNum(builder.toString().trim());
                builder.setLength(0);
            }
            if("like".equals(localName)){
                artical.setLikeNum(builder.toString().trim());
                builder.setLength(0);
            }
            if("commend".equals(localName)){
                artical.setCommendNum(builder.toString().trim());
                builder.setLength(0);
            }
            if("iflike".equals(localName)){
                artical.setIfLike(builder.toString().trim());
                builder.setLength(0);
            }
            if("ifstore".equals(localName)){
                artical.setIfStore(builder.toString().trim());
                builder.setLength(0);
            }

            if("userSchool".equals(localName)){
                artical.setUserSchool(builder.toString().trim());
                builder.setLength(0);
            }

            if("imgHeader".equals(localName)){
                artical.setImgHeader(URLSet.serviceUrl +builder.toString().trim());
                        builder.setLength(0);
            }
            if("userName".equals(localName)){
                artical.setUserName(builder.toString().trim());
                builder.setLength(0);
            }
            if("userNum".equals(localName)){
                artical.setUserNum(builder.toString().trim());
                builder.setLength(0);
            }
            if("articalsNum".equals(localName)){
                artical.setArticalsNum(builder.toString().trim());
                builder.setLength(0);
            }
            if("articalcontent".equals(localName)){
                artical.setArticalcontent(builder.toString().trim());
                builder.setLength(0);
            }
            if("articalTime".equals(localName)){
                artical.setArticalTime(builder.toString().trim());
                builder.setLength(0);
            }
            if ("postion".equals(localName)){
                artical.setPostion(builder.toString().trim());
                builder.setLength(0);
            }

            if("imgsize".equals(localName)){

                builder.setLength(0);
            }


            if("smimgsize".equals(localName)){

                builder.setLength(0);
            }

            if("img".equals(localName)){
                artical.setImg(builder.toString().trim());
                builder.setLength(0);
            }


            if("artical".equals(localName)){
                articals.add(artical);
                builder.setLength(0);
            }

            if("Document".equals(localName)){
//                classifications.add(classification);

            }

        }

        @Override
        public void endDocument() throws SAXException {
            super.endDocument();
            artical=null;
            builder = null;
        }
    }



    private static class XmlGetResultHandler extends DefaultHandler {

        private StringBuilder builder;

        private String result;


        public String getResult() {
            return result;
        }

        @Override
        public void startDocument() throws SAXException {
            super.startDocument();

            builder = new StringBuilder();
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            super.startElement(uri, localName, qName, attributes);
            if ("Document".equals(localName)) {
                Log.d("TestApp", "开始解析");

            }


            if("result".equals(localName)){


                Log.d("TestApp", "开始解析" + localName);
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            super.characters(ch, start, length);
            builder.append(ch, start, length);
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            super.endElement(uri, localName, qName);



            if("result".equals(localName)){
                result=(builder.toString().trim());
                builder.setLength(0);
            }


            if("Document".equals(localName)){
//                classifications.add(classification);

            }

        }

        @Override
        public void endDocument() throws SAXException {
            super.endDocument();

            builder = null;
        }
    }



    public static class XmlGetNewTopicImagesHandler extends DefaultHandler {

        private StringBuilder builder;
        private List<String> imageList;

        public List<String> getList() {
            return imageList;
        }

        @Override
        public void startDocument() throws SAXException {
            super.startDocument();

            builder = new StringBuilder();
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            super.startElement(uri, localName, qName, attributes);
            if ("Document".equals(localName)) {
                Log.d("TestApp", "开始解析");
                imageList=new ArrayList<String>();
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            super.characters(ch, start, length);
            builder.append(ch, start, length);
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            super.endElement(uri, localName, qName);



            if("pic".equals(localName)){
//                result=(builder.toString().trim());
                imageList.add(URLSet.serviceUrl+builder.toString().trim());
                builder.setLength(0);
            }


            if("Document".equals(localName)){
//                classifications.add(classification);

            }

        }

        @Override
        public void endDocument() throws SAXException {
            super.endDocument();

            builder = null;
        }
    }



    private static class XmlArticalCommentsGetHandler extends DefaultHandler {

        private StringBuilder builder;
        private List<Comment> comments;
        private List<SecondComment> secondComments;
        Comment comment;
        SecondComment secondComment;


        public List<Comment> getComments() {
            return comments;
        }

        @Override
        public void startDocument() throws SAXException {
            super.startDocument();

            builder = new StringBuilder();
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            super.startElement(uri, localName, qName, attributes);
            if ("Document".equals(localName)) {
                Log.d("TestApp", "开始解析");
                comments=new ArrayList<Comment>();
            }

            if("commend".equals(localName)){
                comment=new Comment();
            }
            if("secondCommends".equals(localName)){
                secondComments=new ArrayList<SecondComment>();
            }
            if("secondCommend".equals(localName)){
                secondComment=new SecondComment();
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            super.characters(ch, start, length);
            builder.append(ch, start, length);
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            super.endElement(uri, localName, qName);
            if("commendHeadImg".equals(localName)){
                comment.setHeadImg(URLSet.serviceUrl + builder.toString().trim());
                builder.setLength(0);
            }
            if("secondCommendToUserNum".equals(localName)){
                secondComment.setSecondCommendToUserNum(builder.toString().trim());
                builder.setLength(0);
            }
            if("secondCommendToUserName".equals(localName)){
                secondComment.setSecondCommendToUserName(builder.toString().trim());
                builder.setLength(0);
            }
            if("commendUserNum".equals(localName)){
                comment.setCommentUserNum(builder.toString().trim());
                builder.setLength(0);
            }
            if("commendID".equals(localName)){
                comment.setCommentID(builder.toString().trim());
                builder.setLength(0);
            }
            if("commendUserName".equals(localName)){
                comment.setCommendUserName(builder.toString().trim());
                builder.setLength(0);
            }
            if("commendContent".equals(localName)){
                comment.setCommendContent(builder.toString().trim());
                builder.setLength(0);
            }
            if("commendTime".equals(localName)){
                comment.setCommendTime(builder.toString().trim());
                builder.setLength(0);
            }
            if("secondCommendUserNum".equals(localName)){
                secondComment.setSecondCommendUserNum(builder.toString().trim());
                builder.setLength(0);
            }
            if("secondCommendHeadImg".equals(localName)){
                secondComment.setSecondCommendHeadImg(builder.toString().trim());
                builder.setLength(0);
            }
            if("secondCommendUserName".equals(localName)){
                secondComment.setSecondCommendUserName(builder.toString().trim());
                builder.setLength(0);
            }
            if("secondCommendContent".equals(localName)){
                secondComment.setSecondCommendContent(builder.toString().trim());
                builder.setLength(0);
            }
            if("secondCommendTime".equals(localName)){
                secondComment.setSecondCommendTime(builder.toString().trim());
                builder.setLength(0);
            }
            if("secondCommend".equals(localName)){
                secondComments.add(secondComment);
                builder.setLength(0);
            }
            if("secondCommends".equals(localName)){
                comment.setSecondComments(secondComments);
                builder.setLength(0);
            }
            if("commendImg".equals(localName)){
                comment.setCommentImg(builder.toString().trim());
                builder.setLength(0);
            }



            if("commendImgSize".equals(localName)){
                comment.setCommendImgSize(builder.toString().trim());
                builder.setLength(0);
            }
            if("commendSmimg".equals(localName)){
                comment.setCommendSmimg(builder.toString().trim());
                builder.setLength(0);
            }
            if("commendSmimgSize".equals(localName)){
                comment.setCommendSmimgSize(builder.toString().trim());
                builder.setLength(0);
            }


            if("commend".equals(localName)){
                comments.add(comment);
                builder.setLength(0);
            }


            if("Document".equals(localName)){
//                classifications.add(classification);

            }

        }

        @Override
        public void endDocument() throws SAXException {
            super.endDocument();
            comment=null;
            builder = null;
        }
    }


    public static List<HighExamItem> highExamParser(String info){

        if (spFactory == null){
            spFactory = SAXParserFactory.newInstance();
        }

        try {
            if (sParser == null) {
                sParser = spFactory.newSAXParser();
            }
            XmlHighExamHandler handler = new XmlHighExamHandler();
            ByteArrayInputStream inputStream = new ByteArrayInputStream(info.getBytes("utf-8"));

            sParser.parse(inputStream,handler);

            inputStream.close();

            return handler.getItem();
        }catch (Exception e){
            Log.d("exam","exam parser exception");
        }

        return null;
    }

    static class XmlHighExamHandler extends DefaultHandler{
        private StringBuilder builder;
        private List<HighExamItem> list;
        private HighExamItem item;

        @Override
        public void startDocument() throws SAXException {
            list = new ArrayList<>();
            builder = new StringBuilder();
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            builder.setLength(0);
            if ("item".equals(localName)){
                item = new HighExamItem();
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            builder.append(ch, start, length);
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            if ("name".equals(localName)){
                item.setName(builder.toString());
            }else if("id".equals(localName)){
                item.setId(builder.toString());
            }else if("item".equals(localName)){
                list.add(item);
            }
        }

        @Override
        public void endDocument() throws SAXException {

        }

        public List<HighExamItem> getItem(){
            return list;
        }
    }

    public static ItemContent examItemParser(String info){
        if (spFactory == null){
            spFactory = SAXParserFactory.newInstance();
        }
        try {
            if (sParser == null) {
                sParser = spFactory.newSAXParser();
            }
            XmlExamItemHandler handler = new XmlExamItemHandler();

            ByteArrayInputStream inputStream = new ByteArrayInputStream(info.getBytes("utf-8"));

            sParser.parse(inputStream,handler);
            inputStream.close();
            return handler.getContent();
        }catch (Exception e){

        }

        return null;
    }

    public static class XmlExamItemHandler extends DefaultHandler{
        private StringBuilder builder;
        private ItemContent content;

        @Override
        public void startDocument() throws SAXException {
            content = new ItemContent();
            builder = new StringBuilder();
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            builder.setLength(0);
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            builder.append(ch, start, length);
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            if ("content".equals(localName)){
                content.setContent(builder.toString());
            }else if("name".equals(localName)){
                content.setFileName(builder.toString());
            }else if("path".equals(localName)){
                content.setFilePath(builder.toString());
            }
        }

        @Override
        public void endDocument() throws SAXException {
            builder.setLength(0);
        }

        public ItemContent getContent(){
            return content;
        }
    }

    public static String firstExamParser(String data){

        if (spFactory == null){
            spFactory = SAXParserFactory.newInstance();
        }

        try {
            if (sParser == null) {
                sParser = spFactory.newSAXParser();
            }

            XmlFirstExamHandler handler = new XmlFirstExamHandler();
            ByteArrayInputStream inputStream = new ByteArrayInputStream(data.getBytes("utf-8"));
            sParser.parse(inputStream, handler);

            return handler.getFirst();
        }catch (Exception e){

        }
        return null;
    }

    public static class XmlFirstExamHandler extends DefaultHandler {
        private StringBuilder builder;
        private String first;

        @Override
        public void startDocument() throws SAXException {
            builder = new StringBuilder();
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            builder.setLength(0);
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            builder.append(ch,start,length);
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            if ("first".equals(localName)){
                first = builder.toString();
            }
        }

        @Override
        public void endDocument() throws SAXException {
            super.endDocument();
        }

        public String getFirst(){
            return first;
        }
    }

    public static List<Map<String,String>> gradeParser(String data){

        if (spFactory == null){
            spFactory = SAXParserFactory.newInstance();
        }
        try {
            if (sParser == null){
                sParser = spFactory.newSAXParser();
            }
            XmlGradeHandler handler = new XmlGradeHandler();
            ByteArrayInputStream inputStream = new ByteArrayInputStream(data.getBytes("utf-8"));

            sParser.parse(inputStream,handler);

            return handler.getGradeList();
        } catch (Exception e) {

        }

        return null;
    }

    public static class XmlGradeHandler extends DefaultHandler{

        StringBuilder builder;
        List<Map<String,String>> list;
        Map<String,String> map;

        @Override
        public void startDocument() throws SAXException {
            builder = new StringBuilder();

            list = new ArrayList<>();
        }

        @Override
        public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
            builder.setLength(0);
            if ("item".equals(localName)){
                map = new HashMap<>();
            }
        }

        @Override
        public void characters(char[] ch, int start, int length) throws SAXException {
            builder.append(ch, start, length);
        }

        @Override
        public void endElement(String uri, String localName, String qName) throws SAXException {
            if ("name".equals(localName)){
                map.put("name",builder.toString());
            }else if("content".equals(localName)){
                map.put("value",builder.toString());
            }else if("item".equals(localName)){
                list.add(map);
            }
        }

        @Override
        public void endDocument() throws SAXException {

        }

        public List<Map<String,String>> getGradeList(){
            return list;
        }
    }


}
