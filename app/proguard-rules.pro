# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /home/zy/Android/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}
-keep class com.easemob.** {*;}
-keep class org.jivesoftware.** {*;}
-keep class org.apache.** {*;}
-dontwarn  com.easemob.**
#2.0.9��Ĳ���Ҫ���������keep
#-keep class org.xbill.DNS.** {*;}
#���⣬demo�з��ͱ����ʱ��ʹ�õ����䣬��Ҫkeep SmileUtils,ע��ǰ��İ�����
#��ҪSmileUtils���Ƶ��Լ�����Ŀ��keep��ʱ����д��demo��İ���
-keep class com.easemob.chatuidemo.utils.SmileUtils {*;}

#2.0.9���������ͨ�����ܣ�����ʹ�ô˹��ܵ�api����������keep
-dontwarn ch.imvs.**
-dontwarn org.slf4j.**
-keep class org.ice4j.** {*;}
-keep class net.java.sip.** {*;}
-keep class org.webrtc.voiceengine.** {*;}
-keep class org.bitlet.** {*;}
-keep class org.slf4j.** {*;}
-keep class ch.imvs.** {*;}