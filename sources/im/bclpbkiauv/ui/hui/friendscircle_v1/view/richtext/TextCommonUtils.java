package im.bclpbkiauv.ui.hui.friendscircle_v1.view.richtext;

import android.content.Context;
import android.net.Uri;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.style.StyleSpan;
import android.text.style.URLSpan;
import com.bjz.comm.net.bean.FCEntitysResponse;
import com.bjz.comm.net.bean.TopicBean;
import com.blankj.utilcode.constant.RegexConstants;
import im.bclpbkiauv.messenger.FileLog;
import im.bclpbkiauv.ui.hui.friendscircle_v1.view.LinkMovementClickMethod;
import im.bclpbkiauv.ui.hui.friendscircle_v1.view.edittext.listener.ITextViewShow;
import im.bclpbkiauv.ui.hui.friendscircle_v1.view.edittext.listener.SpanAtUserCallBack;
import im.bclpbkiauv.ui.hui.friendscircle_v1.view.edittext.listener.SpanTopicCallBack;
import im.bclpbkiauv.ui.hui.friendscircle_v1.view.edittext.listener.SpanUrlCallBack;
import im.bclpbkiauv.ui.hui.friendscircle_v1.view.edittext.span.ClickAtUserSpan;
import im.bclpbkiauv.ui.hui.friendscircle_v1.view.edittext.span.ClickTopicSpan;
import im.bclpbkiauv.ui.hui.friendscircle_v1.view.edittext.span.LinkSpan;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextCommonUtils {
    public static final String urlPatternStr = "((http|ftp|https|rtsp)://)?(([a-zA-Z0-9\\._-]+\\.[a-zA-Z]{2,6})|([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}))(:[0-9]{1,4})?(/[a-zA-Z0-9\\&\\%_\\./-~-]*)?(\\?([一-龥0-9a-zA-Z\\&\\%\\.\\,_!~*'();?:@=+$#-]+\\=[一-龥0-9a-zA-Z\\&\\%\\.\\,_!~*'();?:@=+$#-]+\\&?)+)?";

    public static void setEmojiText(Context context, String text, ITextViewShow tv) {
        if (TextUtils.isEmpty(text)) {
            tv.setText("");
        }
        tv.setText(SmileUtils.unicodeToEmojiName(context, text));
    }

    public static Spannable getEmojiText(Context context, String text, int size) {
        return getEmojiText(context, text, size, 0);
    }

    public static Spannable getEmojiText(Context context, String text, int size, int verticalAlignment) {
        if (TextUtils.isEmpty(text)) {
            return new SpannableString("");
        }
        return SmileUtils.unicodeToEmojiName(context, text, size, verticalAlignment);
    }

    public static Spannable getEmojiText(Context context, String text) {
        return getEmojiText(context, text, -1);
    }

    public static Spannable getUrlEmojiText(Context context, String text, ITextViewShow textView, int color, boolean needNum, SpanAtUserCallBack spanAtUserCallBack, SpanUrlCallBack spanUrlCallBack) {
        if (!TextUtils.isEmpty(text)) {
            return getUrlSmileText(context, text, (List<FCEntitysResponse>) null, textView, color, 0, needNum, spanAtUserCallBack, spanUrlCallBack);
        }
        return new SpannableString(" ");
    }

    public static void setUrlSmileText(Context context, String string, List<FCEntitysResponse> listUser, ITextViewShow textView, int color, boolean needNum, SpanAtUserCallBack spanAtUserCallBack, SpanUrlCallBack spanUrlCallBack) {
        ITextViewShow iTextViewShow = textView;
        textView.setText(getUrlSmileText(context, string, listUser, textView, color, 0, needNum, spanAtUserCallBack, spanUrlCallBack));
    }

    public static Spannable getAtText(Context context, List<FCEntitysResponse> listUser, List<TopicBean> listTopic, String content, ITextViewShow textView, boolean clickable, int color, int topicColor, SpanAtUserCallBack spanAtUserCallBack, SpanTopicCallBack spanTopicCallBack) {
        int lenght;
        boolean hadHighLine;
        Context context2 = context;
        List<FCEntitysResponse> list = listUser;
        String str = content;
        ITextViewShow iTextViewShow = textView;
        int i = color;
        SpanAtUserCallBack spanAtUserCallBack2 = spanAtUserCallBack;
        Spannable spannable = null;
        if (listTopic != null && listTopic.size() > 0) {
            spannable = getTopicText(context, listTopic, content, textView, clickable, topicColor, spanTopicCallBack);
        }
        if ((list == null || listUser.size() <= 0) && spannable == null) {
            return getEmojiText(context2, str, textView.emojiSize());
        }
        Spannable spannableString = new SpannableString(spannable == null ? str : spannable);
        int lenght2 = content.length();
        boolean hadHighLine2 = false;
        int i2 = 0;
        while (i2 < listUser.size()) {
            FCEntitysResponse fcEntitysResponse = list.get(i2);
            if (fcEntitysResponse != null) {
                String userName = fcEntitysResponse.getUserName();
                int start = fcEntitysResponse.getUOffset();
                int end = fcEntitysResponse.getULimit() + start;
                if (start < 0 || end <= start || end > lenght2) {
                    lenght = lenght2;
                } else if (TextUtils.equals(str.substring(start, end), userName)) {
                    ClickAtUserSpan clickAtUserSpan = null;
                    if (iTextViewShow != null) {
                        lenght = lenght2;
                        clickAtUserSpan = iTextViewShow.getCustomClickAtUserSpan(context2, list.get(i2), i, spanAtUserCallBack2);
                    } else {
                        lenght = lenght2;
                    }
                    if (clickAtUserSpan == null) {
                        hadHighLine = true;
                        clickAtUserSpan = new ClickAtUserSpan(list.get(i2), i, spanAtUserCallBack2);
                    } else {
                        hadHighLine = true;
                    }
                    spannableString.setSpan(clickAtUserSpan, start, end, 18);
                    hadHighLine2 = hadHighLine;
                } else {
                    lenght = lenght2;
                }
            } else {
                lenght = lenght2;
            }
            i2++;
            lenght2 = lenght;
        }
        SmileUtils.addSmiles(context2, textView.emojiSize(), textView.verticalAlignment(), spannableString);
        if (clickable && hadHighLine2) {
            iTextViewShow.setMovementMethod(LinkMovementClickMethod.getInstance());
        }
        return spannableString;
    }

    public static Spannable getTopicText(Context context, List<TopicBean> listTopic, String content, ITextViewShow textView, boolean clickable, int color, SpanTopicCallBack spanTopicCallBack) {
        Map<String, String> map;
        int indexStart;
        ClickTopicSpan clickTopicSpan;
        int tmpIndexStart;
        List<TopicBean> list = listTopic;
        String str = content;
        ITextViewShow iTextViewShow = textView;
        int i = color;
        SpanTopicCallBack spanTopicCallBack2 = spanTopicCallBack;
        if (list == null) {
            Context context2 = context;
        } else if (listTopic.size() <= 0) {
            Context context3 = context;
        } else {
            Spannable spannableString = new SpannableString(str);
            int indexStart2 = 0;
            int lenght = content.length();
            boolean hadHighLine = false;
            Map<String, String> map2 = new HashMap<>();
            int i2 = 0;
            while (i2 < listTopic.size()) {
                int index = str.indexOf("#" + list.get(i2).getTopicName() + "#", indexStart2) + 1;
                if (index < 0 && indexStart2 > 0) {
                    index = str.indexOf(list.get(i2).getTopicName());
                    if (map2.containsKey("" + index)) {
                        if (indexStart2 < lenght) {
                            tmpIndexStart = Integer.parseInt(map2.get("" + index));
                        } else {
                            tmpIndexStart = lenght - 1;
                        }
                        if (tmpIndexStart != indexStart2) {
                            indexStart2 = tmpIndexStart;
                            i2--;
                            map = map2;
                            Context context4 = context;
                            i2++;
                            list = listTopic;
                            i = color;
                            map2 = map;
                        }
                    }
                }
                if (index > 0) {
                    map2.put(index + "", index + "");
                    int mathStart = index + -1;
                    int indexEnd = list.get(i2).getTopicName().length() + index;
                    int matchEnd = indexEnd + 1;
                    if (!("#".equals(str.substring(mathStart, index)) && "#".equals(str.substring(indexEnd, indexEnd + 1)))) {
                        map = map2;
                        Context context5 = context;
                    } else if (matchEnd <= lenght || indexEnd == lenght) {
                        if (indexEnd > indexStart2) {
                            indexStart2 = indexEnd;
                        }
                        hadHighLine = true;
                        ClickTopicSpan clickTopicSpan2 = null;
                        if (iTextViewShow != null) {
                            indexStart = indexStart2;
                            map = map2;
                            clickTopicSpan2 = iTextViewShow.getCustomClickTopicSpan(context, list.get(i2), i, spanTopicCallBack2);
                        } else {
                            indexStart = indexStart2;
                            map = map2;
                            Context context6 = context;
                        }
                        if (clickTopicSpan2 == null) {
                            clickTopicSpan = new ClickTopicSpan(list.get(i2), i, spanTopicCallBack2);
                        } else {
                            clickTopicSpan = clickTopicSpan2;
                        }
                        spannableString.setSpan(clickTopicSpan, mathStart, indexEnd == lenght ? lenght : matchEnd, 18);
                        indexStart2 = indexStart;
                    } else {
                        map = map2;
                        Context context7 = context;
                    }
                } else {
                    map = map2;
                    Context context8 = context;
                }
                i2++;
                list = listTopic;
                i = color;
                map2 = map;
            }
            Context context9 = context;
            if (clickable && hadHighLine) {
                iTextViewShow.setMovementMethod(LinkMovementClickMethod.getInstance());
            }
            return spannableString;
        }
        return new SpannableString(str);
    }

    public static Spannable getUrlSmileText(Context context, String string, List<FCEntitysResponse> listUser, ITextViewShow textView, int colorAt, int colorLink, boolean needNum, SpanAtUserCallBack spanAtUserCallBack, SpanUrlCallBack spanUrlCallBack) {
        return getAllSpanText(context, string, listUser, (List<TopicBean>) null, textView, colorAt, colorLink, 0, needNum, true, spanAtUserCallBack, spanUrlCallBack, (SpanTopicCallBack) null);
    }

    public static Spannable getAllSpanText(Context context, String string, List<FCEntitysResponse> listUser, List<TopicBean> listTopic, ITextViewShow textView, int colorAt, int colorLink, int colorTopic, boolean needNum, boolean needUrl, SpanAtUserCallBack spanAtUserCallBack, SpanUrlCallBack spanUrlCallBack, SpanTopicCallBack spanTopicCallBack) {
        ITextViewShow iTextViewShow = textView;
        if (needUrl || needNum) {
            textView.setAutoLinkMask(7);
        }
        if (TextUtils.isEmpty(string)) {
            return new SpannableString(" ");
        }
        Spannable spannable = getAtText(context, listUser, listTopic, string, textView, true, colorAt, colorTopic, spanAtUserCallBack, spanTopicCallBack);
        textView.setText(spannable);
        if (needUrl || needNum) {
            return resolveUrlLogic(context, textView, spannable, listUser, colorLink, needNum, needUrl, spanUrlCallBack);
        }
        return spannable;
    }

    private static Spannable resolveUrlLogic(Context context, ITextViewShow textView, Spannable spannable, List<FCEntitysResponse> listUser, int color, boolean needNum, boolean needUrl, SpanUrlCallBack spanUrlCallBack) {
        Iterator<FCEntitysResponse> it;
        CharSequence charSequence;
        URLSpan[] urls;
        Context context2 = context;
        ITextViewShow iTextViewShow = textView;
        int i = color;
        SpanUrlCallBack spanUrlCallBack2 = spanUrlCallBack;
        CharSequence charSequence2 = textView.getText();
        if (charSequence2 instanceof Spannable) {
            int end = charSequence2.length();
            Spannable sp = (Spannable) textView.getText();
            URLSpan[] urls2 = (URLSpan[]) sp.getSpans(0, end, URLSpan.class);
            ClickAtUserSpan[] atSpan = (ClickAtUserSpan[]) sp.getSpans(0, end, ClickAtUserSpan.class);
            if (!TextUtils.isEmpty(charSequence2)) {
                SpannableStringBuilder style = new SpannableStringBuilder(charSequence2);
                style.clearSpans();
                if (urls2.length > 0) {
                    if (needNum) {
                        int length = urls2.length;
                        int i2 = 0;
                        while (i2 < length) {
                            URLSpan url = urls2[i2];
                            String urlString = url.getURL();
                            FileLog.e("urlString == " + urlString);
                            int end2 = end;
                            if (!isNumeric(urlString.replace("tel:", "")) || !isMobileSimple(urlString.replace("tel:", ""))) {
                                urls = urls2;
                                style.setSpan(new StyleSpan(0), sp.getSpanStart(url), sp.getSpanEnd(url), 34);
                            } else {
                                LinkSpan linkSpan = null;
                                if (iTextViewShow != null) {
                                    linkSpan = iTextViewShow.getCustomLinkSpan(context2, url.getURL(), i, spanUrlCallBack2);
                                }
                                if (linkSpan == null) {
                                    LinkSpan linkSpan2 = linkSpan;
                                    linkSpan = new LinkSpan(url.getURL(), i, spanUrlCallBack2);
                                } else {
                                    LinkSpan linkSpan3 = linkSpan;
                                }
                                String str = urlString;
                                urls = urls2;
                                style.setSpan(linkSpan, sp.getSpanStart(url), sp.getSpanEnd(url), 33);
                            }
                            i2++;
                            end = end2;
                            urls2 = urls;
                        }
                        URLSpan[] uRLSpanArr = urls2;
                    } else {
                        URLSpan[] uRLSpanArr2 = urls2;
                    }
                    if (needUrl) {
                        Pattern pattern = Pattern.compile("((http|ftp|https|rtsp)://)?(([a-zA-Z0-9\\._-]+\\.[a-zA-Z]{2,6})|([0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}))(:[0-9]{1,4})?(/[a-zA-Z0-9\\&\\%_\\./-~-]*)?(\\?([一-龥0-9a-zA-Z\\&\\%\\.\\,_!~*'();?:@=+$#-]+\\=[一-龥0-9a-zA-Z\\&\\%\\.\\,_!~*'();?:@=+$#-]+\\&?)+)?", 2);
                        Matcher matcher = pattern.matcher(charSequence2);
                        while (matcher.find()) {
                            int urlStart = matcher.start();
                            int urlEnd = matcher.end();
                            String url2 = matcher.group();
                            FileLog.e("urlString == " + url2);
                            Uri parse = Uri.parse(url2);
                            StringBuilder sb = new StringBuilder();
                            Pattern pattern2 = pattern;
                            sb.append("urlString parse== ");
                            sb.append(parse.getHost());
                            FileLog.e(sb.toString());
                            LinkSpan linkSpan4 = iTextViewShow.getCustomLinkSpan(context2, url2, i, spanUrlCallBack2);
                            if (linkSpan4 == null) {
                                linkSpan4 = new LinkSpan(url2, i, spanUrlCallBack2);
                            }
                            style.setSpan(linkSpan4, urlStart, urlEnd, 33);
                            pattern = pattern2;
                        }
                        CharSequence charSequence3 = charSequence2;
                    } else {
                        CharSequence charSequence4 = charSequence2;
                    }
                } else {
                    URLSpan[] uRLSpanArr3 = urls2;
                    if (!needUrl || listUser == null) {
                    } else {
                        Iterator<FCEntitysResponse> it2 = listUser.iterator();
                        while (it2.hasNext()) {
                            FCEntitysResponse bean = it2.next();
                            int type = bean.getType();
                            int offset = bean.getUOffset();
                            int limit = bean.getULimit();
                            if (offset < 0 || limit <= 0 || offset + limit > charSequence2.length()) {
                                charSequence = charSequence2;
                                it = it2;
                            } else if (type == 2) {
                                LinkSpan linkSpan5 = null;
                                URLSpan urlSpan = new URLSpan(charSequence2.toString().substring(offset, offset + limit));
                                if (iTextViewShow != null) {
                                    charSequence = charSequence2;
                                    linkSpan5 = iTextViewShow.getCustomLinkSpan(context2, urlSpan.getURL(), i, spanUrlCallBack2);
                                } else {
                                    charSequence = charSequence2;
                                }
                                if (linkSpan5 == null) {
                                    it = it2;
                                    linkSpan5 = new LinkSpan(urlSpan.getURL(), i, spanUrlCallBack2);
                                } else {
                                    it = it2;
                                }
                                style.setSpan(linkSpan5, offset, offset + limit, 33);
                            } else {
                                charSequence = charSequence2;
                                it = it2;
                                style.setSpan(new StyleSpan(0), offset, offset + limit, 34);
                            }
                            charSequence2 = charSequence;
                            it2 = it;
                        }
                    }
                }
                for (ClickAtUserSpan atUserSpan : atSpan) {
                    LinkSpan[] removeUrls = (LinkSpan[]) style.getSpans(sp.getSpanStart(atUserSpan), sp.getSpanEnd(atUserSpan), LinkSpan.class);
                    if (removeUrls != null && removeUrls.length > 0) {
                        for (LinkSpan linkSpan6 : removeUrls) {
                            style.removeSpan(linkSpan6);
                        }
                    }
                    style.setSpan(atUserSpan, sp.getSpanStart(atUserSpan), sp.getSpanEnd(atUserSpan), 18);
                }
                SmileUtils.addSmiles(context2, textView.emojiSize(), textView.verticalAlignment(), style);
                iTextViewShow.setAutoLinkMask(0);
                return style;
            }
            iTextViewShow.setAutoLinkMask(0);
            return spannable;
        }
        iTextViewShow.setAutoLinkMask(0);
        return spannable;
    }

    private static boolean isTopURL(String str) {
        if (str.split("\\.").length < 2) {
            return false;
        }
        return true;
    }

    private static boolean isNumeric(String str) {
        if (!Pattern.compile("[0-9]*").matcher(str).matches()) {
            return false;
        }
        return true;
    }

    private static boolean isMobileSimple(String string) {
        return !TextUtils.isEmpty(string) && Pattern.matches(RegexConstants.REGEX_MOBILE_SIMPLE, string);
    }
}
