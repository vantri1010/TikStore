package com.coremedia.iso;

import com.coremedia.iso.boxes.Box;
import com.litesuits.orm.db.assit.SQLBuilder;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.Enumeration;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PropertyBoxParserImpl extends AbstractBoxParser {
    static String[] EMPTY_STRING_ARRAY = new String[0];
    StringBuilder buildLookupStrings = new StringBuilder();
    String clazzName;
    Pattern constuctorPattern = Pattern.compile("(.*)\\((.*?)\\)");
    Properties mapping;
    String[] param;

    public PropertyBoxParserImpl(String... customProperties) {
        InputStream customIS;
        InputStream is = getClass().getResourceAsStream("/isoparser-default.properties");
        try {
            Properties properties = new Properties();
            this.mapping = properties;
            try {
                properties.load(is);
                ClassLoader cl = Thread.currentThread().getContextClassLoader();
                Enumeration<URL> enumeration = (cl == null ? ClassLoader.getSystemClassLoader() : cl).getResources("isoparser-custom.properties");
                while (enumeration.hasMoreElements()) {
                    customIS = enumeration.nextElement().openStream();
                    this.mapping.load(customIS);
                    customIS.close();
                }
                for (String customProperty : customProperties) {
                    this.mapping.load(getClass().getResourceAsStream(customProperty));
                }
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (IOException e2) {
                throw new RuntimeException(e2);
            } catch (Throwable th) {
                customIS.close();
                throw th;
            }
        } catch (Throwable e3) {
            try {
                is.close();
            } catch (IOException e4) {
                e4.printStackTrace();
            }
            throw e3;
        }
    }

    public PropertyBoxParserImpl(Properties mapping2) {
        this.mapping = mapping2;
    }

    public Box createBox(String type, byte[] userType, String parent) {
        invoke(type, userType, parent);
        try {
            Class<?> cls = Class.forName(this.clazzName);
            if (this.param.length <= 0) {
                return (Box) cls.newInstance();
            }
            Class[] constructorArgsClazz = new Class[this.param.length];
            Object[] constructorArgs = new Object[this.param.length];
            for (int i = 0; i < this.param.length; i++) {
                if ("userType".equals(this.param[i])) {
                    constructorArgs[i] = userType;
                    constructorArgsClazz[i] = byte[].class;
                } else if ("type".equals(this.param[i])) {
                    constructorArgs[i] = type;
                    constructorArgsClazz[i] = String.class;
                } else if ("parent".equals(this.param[i])) {
                    constructorArgs[i] = parent;
                    constructorArgsClazz[i] = String.class;
                } else {
                    throw new InternalError("No such param: " + this.param[i]);
                }
            }
            return (Box) cls.getConstructor(constructorArgsClazz).newInstance(constructorArgs);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        } catch (InstantiationException e2) {
            throw new RuntimeException(e2);
        } catch (IllegalAccessException e3) {
            throw new RuntimeException(e3);
        } catch (InvocationTargetException e4) {
            throw new RuntimeException(e4);
        } catch (NoSuchMethodException e5) {
            throw new RuntimeException(e5);
        }
    }

    public void invoke(String type, byte[] userType, String parent) {
        String constructor;
        if (userType == null) {
            constructor = this.mapping.getProperty(type);
            if (constructor == null) {
                StringBuilder sb = this.buildLookupStrings;
                sb.append(parent);
                sb.append('-');
                sb.append(type);
                String lookup = sb.toString();
                this.buildLookupStrings.setLength(0);
                constructor = this.mapping.getProperty(lookup);
            }
        } else if ("uuid".equals(type)) {
            Properties properties = this.mapping;
            constructor = properties.getProperty("uuid[" + Hex.encodeHex(userType).toUpperCase() + "]");
            if (constructor == null) {
                Properties properties2 = this.mapping;
                constructor = properties2.getProperty(String.valueOf(parent) + "-uuid[" + Hex.encodeHex(userType).toUpperCase() + "]");
            }
            if (constructor == null) {
                constructor = this.mapping.getProperty("uuid");
            }
        } else {
            throw new RuntimeException("we have a userType but no uuid box type. Something's wrong");
        }
        if (constructor == null) {
            constructor = this.mapping.getProperty("default");
        }
        if (constructor == null) {
            throw new RuntimeException("No box object found for " + type);
        } else if (!constructor.endsWith(SQLBuilder.PARENTHESES_RIGHT)) {
            this.param = EMPTY_STRING_ARRAY;
            this.clazzName = constructor;
        } else {
            Matcher m = this.constuctorPattern.matcher(constructor);
            if (m.matches()) {
                this.clazzName = m.group(1);
                if (m.group(2).length() == 0) {
                    this.param = EMPTY_STRING_ARRAY;
                } else {
                    this.param = m.group(2).length() > 0 ? m.group(2).split(",") : new String[0];
                }
            } else {
                throw new RuntimeException("Cannot work with that constructor: " + constructor);
            }
        }
    }
}
