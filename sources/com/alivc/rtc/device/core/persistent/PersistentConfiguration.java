package com.alivc.rtc.device.core.persistent;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import com.alivc.rtc.device.core.persistent.MySharedPreferences;
import com.alivc.rtc.device.utils.StringUtils;
import java.io.File;
import java.util.Map;

public class PersistentConfiguration {
    private static final String KEY_TIMESTAMP = "t";
    private static final String KEY_TIMESTAMP2 = "t2";
    private boolean mCanRead = false;
    private boolean mCanWrite = false;
    private String mConfigName = "";
    private Context mContext = null;
    private SharedPreferences.Editor mEditor = null;
    private String mFolderName = "";
    private boolean mIsLessMode = false;
    private boolean mIsSafety = false;
    private MySharedPreferences.MyEditor mMyEditor = null;
    private MySharedPreferences mMySP = null;
    private SharedPreferences mSp = null;
    private TransactionXMLFile mTxf = null;

    public PersistentConfiguration(Context context, String folderName, String configName, boolean isSafety, boolean isLessMode) {
        Context context2 = context;
        String str = folderName;
        String str2 = configName;
        boolean z = isLessMode;
        this.mIsSafety = isSafety;
        this.mIsLessMode = z;
        this.mConfigName = str2;
        this.mFolderName = str;
        this.mContext = context2;
        long spT = 0;
        long mySPT = 0;
        if (context2 != null) {
            SharedPreferences sharedPreferences = context2.getSharedPreferences(str2, 0);
            this.mSp = sharedPreferences;
            spT = sharedPreferences.getLong(KEY_TIMESTAMP, 0);
        }
        String mountedProperty = null;
        try {
            mountedProperty = Environment.getExternalStorageState();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (StringUtils.isEmpty(mountedProperty)) {
            this.mCanWrite = false;
            this.mCanRead = false;
        } else if (mountedProperty.equals("mounted")) {
            this.mCanWrite = true;
            this.mCanRead = true;
        } else if (mountedProperty.equals("mounted_ro")) {
            this.mCanRead = true;
            this.mCanWrite = false;
        } else {
            this.mCanWrite = false;
            this.mCanRead = false;
        }
        if ((this.mCanRead || this.mCanWrite) && context2 != null && !StringUtils.isEmpty(folderName)) {
            TransactionXMLFile transactionXMLFile = getTransactionXMLFile(str);
            this.mTxf = transactionXMLFile;
            if (transactionXMLFile != null) {
                try {
                    MySharedPreferences mySharedPreferences = transactionXMLFile.getMySharedPreferences(str2, 0);
                    this.mMySP = mySharedPreferences;
                    long mySPT2 = mySharedPreferences.getLong(KEY_TIMESTAMP, 0);
                    if (z) {
                        spT = this.mSp.getLong(KEY_TIMESTAMP2, 0);
                        mySPT2 = this.mMySP.getLong(KEY_TIMESTAMP2, 0);
                        if (spT < mySPT2 && spT > 0) {
                            copySPToMySP(this.mSp, this.mMySP);
                            this.mMySP = this.mTxf.getMySharedPreferences(str2, 0);
                        } else if (spT > mySPT2 && mySPT2 > 0) {
                            copyMySPToSP(this.mMySP, this.mSp);
                            this.mSp = context2.getSharedPreferences(str2, 0);
                        } else if (spT == 0 && mySPT2 > 0) {
                            copyMySPToSP(this.mMySP, this.mSp);
                            this.mSp = context2.getSharedPreferences(str2, 0);
                        } else if (mySPT2 == 0 && spT > 0) {
                            copySPToMySP(this.mSp, this.mMySP);
                            this.mMySP = this.mTxf.getMySharedPreferences(str2, 0);
                        } else if (spT == mySPT2) {
                            copySPToMySP(this.mSp, this.mMySP);
                            this.mMySP = this.mTxf.getMySharedPreferences(str2, 0);
                        }
                    } else if (spT > mySPT2) {
                        try {
                            copySPToMySP(this.mSp, this.mMySP);
                            this.mMySP = this.mTxf.getMySharedPreferences(str2, 0);
                        } catch (Exception e2) {
                            mySPT = mySPT2;
                        }
                    } else if (spT < mySPT2) {
                        copyMySPToSP(this.mMySP, this.mSp);
                        this.mSp = context2.getSharedPreferences(str2, 0);
                    } else if (spT == mySPT2) {
                        copySPToMySP(this.mSp, this.mMySP);
                        this.mMySP = this.mTxf.getMySharedPreferences(str2, 0);
                    }
                    mySPT = mySPT2;
                } catch (Exception e3) {
                }
            }
        }
        if (spT != mySPT || (spT == 0 && mySPT == 0)) {
            long timestamp = System.currentTimeMillis();
            boolean z2 = this.mIsLessMode;
            if (!z2 || (z2 && spT == 0 && mySPT == 0)) {
                SharedPreferences sharedPreferences2 = this.mSp;
                if (sharedPreferences2 != null) {
                    SharedPreferences.Editor editorTmp = sharedPreferences2.edit();
                    editorTmp.putLong(KEY_TIMESTAMP2, timestamp);
                    editorTmp.commit();
                }
                try {
                    if (this.mMySP != null) {
                        MySharedPreferences.MyEditor myEditorTmp = this.mMySP.edit();
                        myEditorTmp.putLong(KEY_TIMESTAMP2, timestamp);
                        myEditorTmp.commit();
                    }
                } catch (Exception e4) {
                }
            }
        }
    }

    private TransactionXMLFile getTransactionXMLFile(String folderName) {
        File rootFolder = getRootFolder(folderName);
        if (rootFolder == null) {
            return null;
        }
        TransactionXMLFile transactionXMLFile = new TransactionXMLFile(rootFolder.getAbsolutePath());
        this.mTxf = transactionXMLFile;
        return transactionXMLFile;
    }

    private File getRootFolder(String folderName) {
        File sdCardFile = Environment.getExternalStorageDirectory();
        if (sdCardFile == null) {
            return null;
        }
        File rootFolder = new File(String.format("%s%s%s", new Object[]{sdCardFile.getAbsolutePath(), File.separator, folderName}));
        if (!rootFolder.exists()) {
            rootFolder.mkdirs();
        }
        return rootFolder;
    }

    private void copySPToMySP(SharedPreferences sp1, MySharedPreferences sp2) {
        MySharedPreferences.MyEditor myEditorTmp;
        if (sp1 != null && sp2 != null && (myEditorTmp = sp2.edit()) != null) {
            myEditorTmp.clear();
            for (Map.Entry<String, ?> e : sp1.getAll().entrySet()) {
                String key = e.getKey();
                Object value = e.getValue();
                if (value instanceof String) {
                    myEditorTmp.putString(key, (String) value);
                } else if (value instanceof Integer) {
                    myEditorTmp.putInt(key, ((Integer) value).intValue());
                } else if (value instanceof Long) {
                    myEditorTmp.putLong(key, ((Long) value).longValue());
                } else if (value instanceof Float) {
                    myEditorTmp.putFloat(key, ((Float) value).floatValue());
                } else if (value instanceof Boolean) {
                    myEditorTmp.putBoolean(key, ((Boolean) value).booleanValue());
                }
            }
            try {
                myEditorTmp.commit();
            } catch (Exception e2) {
            }
        }
    }

    private void copyMySPToSP(MySharedPreferences sp1, SharedPreferences sp2) {
        SharedPreferences.Editor editorTmp;
        if (sp1 != null && sp2 != null && (editorTmp = sp2.edit()) != null) {
            editorTmp.clear();
            for (Map.Entry<String, ?> e : sp1.getAll().entrySet()) {
                String key = e.getKey();
                Object value = e.getValue();
                if (value instanceof String) {
                    editorTmp.putString(key, (String) value);
                } else if (value instanceof Integer) {
                    editorTmp.putInt(key, ((Integer) value).intValue());
                } else if (value instanceof Long) {
                    editorTmp.putLong(key, ((Long) value).longValue());
                } else if (value instanceof Float) {
                    editorTmp.putFloat(key, ((Float) value).floatValue());
                } else if (value instanceof Boolean) {
                    editorTmp.putBoolean(key, ((Boolean) value).booleanValue());
                }
            }
            editorTmp.commit();
        }
    }

    private boolean checkSDCardXMLFile() {
        MySharedPreferences mySharedPreferences = this.mMySP;
        if (mySharedPreferences == null) {
            return false;
        }
        boolean isExist = mySharedPreferences.checkFile();
        if (!isExist) {
            commit();
        }
        return isExist;
    }

    private void initEditor() {
        MySharedPreferences mySharedPreferences;
        SharedPreferences sharedPreferences;
        if (this.mEditor == null && (sharedPreferences = this.mSp) != null) {
            this.mEditor = sharedPreferences.edit();
        }
        if (this.mCanWrite && this.mMyEditor == null && (mySharedPreferences = this.mMySP) != null) {
            this.mMyEditor = mySharedPreferences.edit();
        }
        checkSDCardXMLFile();
    }

    public void putInt(String key, int value) {
        if (!StringUtils.isEmpty(key) && !key.equals(KEY_TIMESTAMP)) {
            initEditor();
            SharedPreferences.Editor editor = this.mEditor;
            if (editor != null) {
                editor.putInt(key, value);
            }
            MySharedPreferences.MyEditor myEditor = this.mMyEditor;
            if (myEditor != null) {
                myEditor.putInt(key, value);
            }
        }
    }

    public void putLong(String key, long value) {
        if (!StringUtils.isEmpty(key) && !key.equals(KEY_TIMESTAMP)) {
            initEditor();
            SharedPreferences.Editor editor = this.mEditor;
            if (editor != null) {
                editor.putLong(key, value);
            }
            MySharedPreferences.MyEditor myEditor = this.mMyEditor;
            if (myEditor != null) {
                myEditor.putLong(key, value);
            }
        }
    }

    public void putBoolean(String key, boolean value) {
        if (!StringUtils.isEmpty(key) && !key.equals(KEY_TIMESTAMP)) {
            initEditor();
            SharedPreferences.Editor editor = this.mEditor;
            if (editor != null) {
                editor.putBoolean(key, value);
            }
            MySharedPreferences.MyEditor myEditor = this.mMyEditor;
            if (myEditor != null) {
                myEditor.putBoolean(key, value);
            }
        }
    }

    public void putFloat(String key, float value) {
        if (!StringUtils.isEmpty(key) && !key.equals(KEY_TIMESTAMP)) {
            initEditor();
            SharedPreferences.Editor editor = this.mEditor;
            if (editor != null) {
                editor.putFloat(key, value);
            }
            MySharedPreferences.MyEditor myEditor = this.mMyEditor;
            if (myEditor != null) {
                myEditor.putFloat(key, value);
            }
        }
    }

    public void putString(String key, String value) {
        if (!StringUtils.isEmpty(key) && !key.equals(KEY_TIMESTAMP)) {
            initEditor();
            SharedPreferences.Editor editor = this.mEditor;
            if (editor != null) {
                editor.putString(key, value);
            }
            MySharedPreferences.MyEditor myEditor = this.mMyEditor;
            if (myEditor != null) {
                myEditor.putString(key, value);
            }
        }
    }

    public void remove(String key) {
        if (!StringUtils.isEmpty(key) && !key.equals(KEY_TIMESTAMP)) {
            initEditor();
            SharedPreferences.Editor editor = this.mEditor;
            if (editor != null) {
                editor.remove(key);
            }
            MySharedPreferences.MyEditor myEditor = this.mMyEditor;
            if (myEditor != null) {
                myEditor.remove(key);
            }
        }
    }

    public void reload() {
        Context context;
        if (!(this.mSp == null || (context = this.mContext) == null)) {
            this.mSp = context.getSharedPreferences(this.mConfigName, 0);
        }
        String mountedProperty = null;
        try {
            mountedProperty = Environment.getExternalStorageState();
        } catch (Exception var4) {
            var4.printStackTrace();
        }
        if (StringUtils.isEmpty(mountedProperty)) {
            return;
        }
        if (mountedProperty.equals("mounted") || (mountedProperty.equals("mounted_ro") && this.mMySP != null)) {
            try {
                if (this.mTxf != null) {
                    this.mMySP = this.mTxf.getMySharedPreferences(this.mConfigName, 0);
                }
            } catch (Exception e) {
            }
        }
    }

    public void clear() {
        initEditor();
        long t = System.currentTimeMillis();
        SharedPreferences.Editor editor = this.mEditor;
        if (editor != null) {
            editor.clear();
            this.mEditor.putLong(KEY_TIMESTAMP, t);
        }
        MySharedPreferences.MyEditor myEditor = this.mMyEditor;
        if (myEditor != null) {
            myEditor.clear();
            this.mMyEditor.putLong(KEY_TIMESTAMP, t);
        }
    }

    public boolean commit() {
        Context context;
        boolean result = true;
        long t = System.currentTimeMillis();
        SharedPreferences.Editor editor = this.mEditor;
        if (editor != null) {
            if (!this.mIsLessMode && this.mSp != null) {
                editor.putLong(KEY_TIMESTAMP, t);
            }
            if (!this.mEditor.commit()) {
                result = false;
            }
        }
        if (!(this.mSp == null || (context = this.mContext) == null)) {
            this.mSp = context.getSharedPreferences(this.mConfigName, 0);
        }
        String mountedProperty = null;
        try {
            mountedProperty = Environment.getExternalStorageState();
        } catch (Exception var8) {
            var8.printStackTrace();
        }
        if (!StringUtils.isEmpty(mountedProperty)) {
            if (mountedProperty.equals("mounted")) {
                if (this.mMySP == null) {
                    TransactionXMLFile txf = getTransactionXMLFile(this.mFolderName);
                    if (txf != null) {
                        MySharedPreferences mySharedPreferences = txf.getMySharedPreferences(this.mConfigName, 0);
                        this.mMySP = mySharedPreferences;
                        if (!this.mIsLessMode) {
                            copySPToMySP(this.mSp, mySharedPreferences);
                        } else {
                            copyMySPToSP(mySharedPreferences, this.mSp);
                        }
                        this.mMyEditor = this.mMySP.edit();
                    }
                } else {
                    try {
                        if (this.mMyEditor != null && !this.mMyEditor.commit()) {
                            result = false;
                        }
                    } catch (Exception e) {
                        result = false;
                    }
                }
            }
            if (mountedProperty.equals("mounted") || (mountedProperty.equals("mounted_ro") && this.mMySP != null)) {
                try {
                    if (this.mTxf != null) {
                        this.mMySP = this.mTxf.getMySharedPreferences(this.mConfigName, 0);
                    }
                } catch (Exception e2) {
                }
            }
        }
        return result;
    }

    public String getString(String key) {
        checkSDCardXMLFile();
        SharedPreferences sharedPreferences = this.mSp;
        if (sharedPreferences != null) {
            String value = sharedPreferences.getString(key, "");
            if (!StringUtils.isEmpty(value)) {
                return value;
            }
        }
        MySharedPreferences mySharedPreferences = this.mMySP;
        if (mySharedPreferences != null) {
            return mySharedPreferences.getString(key, "");
        }
        return "";
    }

    public int getInt(String key) {
        checkSDCardXMLFile();
        SharedPreferences sharedPreferences = this.mSp;
        if (sharedPreferences != null) {
            return sharedPreferences.getInt(key, 0);
        }
        MySharedPreferences mySharedPreferences = this.mMySP;
        if (mySharedPreferences != null) {
            return mySharedPreferences.getInt(key, 0);
        }
        return 0;
    }

    public long getLong(String key) {
        checkSDCardXMLFile();
        SharedPreferences sharedPreferences = this.mSp;
        if (sharedPreferences != null) {
            return sharedPreferences.getLong(key, 0);
        }
        MySharedPreferences mySharedPreferences = this.mMySP;
        if (mySharedPreferences != null) {
            return mySharedPreferences.getLong(key, 0);
        }
        return 0;
    }

    public float getFloat(String key) {
        checkSDCardXMLFile();
        SharedPreferences sharedPreferences = this.mSp;
        if (sharedPreferences != null) {
            return sharedPreferences.getFloat(key, 0.0f);
        }
        MySharedPreferences mySharedPreferences = this.mMySP;
        if (mySharedPreferences != null) {
            return mySharedPreferences.getFloat(key, 0.0f);
        }
        return 0.0f;
    }

    public boolean getBoolean(String key) {
        checkSDCardXMLFile();
        SharedPreferences sharedPreferences = this.mSp;
        if (sharedPreferences != null) {
            return sharedPreferences.getBoolean(key, false);
        }
        MySharedPreferences mySharedPreferences = this.mMySP;
        if (mySharedPreferences != null) {
            return mySharedPreferences.getBoolean(key, false);
        }
        return false;
    }

    public Map<String, ?> getAll() {
        checkSDCardXMLFile();
        SharedPreferences sharedPreferences = this.mSp;
        if (sharedPreferences != null) {
            return sharedPreferences.getAll();
        }
        MySharedPreferences mySharedPreferences = this.mMySP;
        if (mySharedPreferences != null) {
            return mySharedPreferences.getAll();
        }
        return null;
    }
}
