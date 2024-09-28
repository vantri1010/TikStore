package com.socks.library.klog;

import android.util.Log;
import com.socks.library.KLog;
import com.socks.library.Util;
import java.io.StringReader;
import java.io.StringWriter;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

public class XmlLog {
    public static void printXml(String tag, String xml, String headString) {
        String xml2;
        if (xml != null) {
            xml2 = headString + "\n" + formatXML(xml);
        } else {
            xml2 = headString + KLog.NULL_TIPS;
        }
        Util.printLine(tag, true);
        for (String line : xml2.split(KLog.LINE_SEPARATOR)) {
            if (!Util.isEmpty(line)) {
                Log.d(tag, "║ " + line);
            }
        }
        Util.printLine(tag, false);
    }

    public static String formatXML(String inputXML) {
        try {
            Source xmlInput = new StreamSource(new StringReader(inputXML));
            StreamResult xmlOutput = new StreamResult(new StringWriter());
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty("indent", "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            transformer.transform(xmlInput, xmlOutput);
            return xmlOutput.getWriter().toString().replaceFirst(">", ">\n");
        } catch (Exception e) {
            e.printStackTrace();
            return inputXML;
        }
    }
}
