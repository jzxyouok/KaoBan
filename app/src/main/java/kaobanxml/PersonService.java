package kaobanxml;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import tempvalue.Exam;

public class PersonService {
    public static List<Exam> getExam(String xml) throws IOException{
        List<Exam> exams = null;
        Exam exam = null;
        XmlPullParser pullParser = Xml.newPullParser();
        try {
            pullParser.setInput(new ByteArrayInputStream(xml.getBytes("UTF-8")),"UTF-8");
            int event = pullParser.getEventType();
            while(event!=XmlPullParser.END_DOCUMENT){
                switch (event) {
                    case XmlPullParser.START_DOCUMENT:
                        exams = new ArrayList<Exam>();
                        break;
                    case XmlPullParser.START_TAG:

                        if("exam".equals(pullParser.getName())){
                            String name = (pullParser.getAttributeValue(0));
                            exam = new Exam();
                            exam.setName(name);
                        }
                        if("name".equals(pullParser.getName())){
                            String rtime = pullParser.nextText();
                            exam.setRtime(rtime);
                        }
                        if("age".equals(pullParser.getName())){
                            String etime = pullParser.nextText();
                            exam.setEtime(etime);
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if("exam".equals(pullParser.getName())){
                            exams.add(exam);
                            exam = null;
                        }
                        break;
                }
                event = pullParser.next();
            }

        } catch (XmlPullParserException e) {
            e.printStackTrace();
        }
        return exams;
    }
}