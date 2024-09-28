package im.bclpbkiauv.ui.dialogs;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import im.bclpbkiauv.messenger.AndroidUtilities;
import im.bclpbkiauv.messenger.R;
import java.util.ArrayList;
import java.util.List;

public class PayPasswordDialog extends BottomSheetDialog {
    /* access modifiers changed from: private */
    public Context mContext;
    private GridView mGvBtn;
    private ImageView mIvBack;
    /* access modifiers changed from: private */
    public List<Integer> mNumbers = new ArrayList();
    private OnForgotClickListener mOnForgotClickListener;
    private OnInputDoneListener mOnInputDoneListener;
    private TextView mTvForgotPassword;
    private TextView[] mTvPasswords = new TextView[6];
    private int notEmptyTvCount;

    public interface OnForgotClickListener {
        void onClick(View view);
    }

    public interface OnInputDoneListener {
        void onCompleteDelegate(String str);
    }

    public PayPasswordDialog(Context context) {
        super(context);
        this.mContext = context;
        init(context);
    }

    private void init(Context context) {
        setContentView(LayoutInflater.from(context).inflate(R.layout.layout_pay_password, (ViewGroup) null));
        setCancelable(false);
        Window window = getWindow();
        window.findViewById(R.id.design_bottom_sheet).setBackgroundResource(17170445);
        window.setGravity(80);
        Display display = ((Activity) context).getWindowManager().getDefaultDisplay();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = display.getWidth();
        window.setAttributes(lp);
        initView(window);
        initData();
    }

    private void initView(Window window) {
        ImageView imageView = (ImageView) window.findViewById(R.id.iv_back);
        this.mIvBack = imageView;
        imageView.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                PayPasswordDialog.this.lambda$initView$0$PayPasswordDialog(view);
            }
        });
        ((TextView) window.findViewById(R.id.tv_title)).setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        TextView textView = (TextView) window.findViewById(R.id.tv_forgot_password);
        this.mTvForgotPassword = textView;
        textView.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
        this.mTvForgotPassword.setOnClickListener(new View.OnClickListener() {
            public final void onClick(View view) {
                PayPasswordDialog.this.lambda$initView$1$PayPasswordDialog(view);
            }
        });
        this.mTvPasswords[0] = (TextView) window.findViewById(R.id.tv_password_1);
        this.mTvPasswords[1] = (TextView) window.findViewById(R.id.tv_password_2);
        this.mTvPasswords[2] = (TextView) window.findViewById(R.id.tv_password_3);
        this.mTvPasswords[3] = (TextView) window.findViewById(R.id.tv_password_4);
        this.mTvPasswords[4] = (TextView) window.findViewById(R.id.tv_password_5);
        this.mTvPasswords[5] = (TextView) window.findViewById(R.id.tv_password_6);
        GridView gridView = (GridView) window.findViewById(R.id.gv_btn);
        this.mGvBtn = gridView;
        gridView.setAdapter(new MyAdapter());
        this.mGvBtn.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public final void onItemClick(AdapterView adapterView, View view, int i, long j) {
                PayPasswordDialog.this.lambda$initView$2$PayPasswordDialog(adapterView, view, i, j);
            }
        });
    }

    public /* synthetic */ void lambda$initView$0$PayPasswordDialog(View v) {
        dismiss();
    }

    public /* synthetic */ void lambda$initView$1$PayPasswordDialog(View v) {
        OnForgotClickListener onForgotClickListener = this.mOnForgotClickListener;
        if (onForgotClickListener != null) {
            onForgotClickListener.onClick(v);
        }
    }

    public /* synthetic */ void lambda$initView$2$PayPasswordDialog(AdapterView parent, View view, int position, long id) {
        if (position < 9 || position == 10) {
            int i = this.notEmptyTvCount;
            TextView[] textViewArr = this.mTvPasswords;
            if (i != textViewArr.length) {
                int length = textViewArr.length;
                int i2 = 0;
                while (true) {
                    if (i2 >= length) {
                        break;
                    }
                    TextView textView = textViewArr[i2];
                    if (TextUtils.isEmpty(textView.getText())) {
                        textView.setText(String.valueOf(this.mNumbers.get(position)));
                        this.notEmptyTvCount++;
                        break;
                    }
                    i2++;
                }
                if (this.notEmptyTvCount == this.mTvPasswords.length) {
                    StringBuilder password = new StringBuilder();
                    for (TextView textView2 : this.mTvPasswords) {
                        String text = textView2.getText().toString();
                        if (!TextUtils.isEmpty(text)) {
                            password.append(text);
                        }
                    }
                    OnInputDoneListener onInputDoneListener = this.mOnInputDoneListener;
                    if (onInputDoneListener != null) {
                        onInputDoneListener.onCompleteDelegate(password.toString());
                    }
                }
            }
        } else if (position == 11 && this.notEmptyTvCount != 0) {
            for (int i3 = this.mTvPasswords.length - 1; i3 >= 0; i3--) {
                if (!TextUtils.isEmpty(this.mTvPasswords[i3].getText())) {
                    this.mTvPasswords[i3].setText((CharSequence) null);
                    this.notEmptyTvCount--;
                    return;
                }
            }
        }
    }

    private void initData() {
        for (int i = 1; i < 10; i++) {
            this.mNumbers.add(Integer.valueOf(i));
        }
        this.mNumbers.add(-10);
        this.mNumbers.add(0);
        this.mNumbers.add(-11);
    }

    public void setOnInputDoneListener(OnInputDoneListener listener) {
        this.mOnInputDoneListener = listener;
    }

    public void setOnForgotClickListener(OnForgotClickListener listener) {
        this.mOnForgotClickListener = listener;
    }

    private class MyAdapter extends BaseAdapter {
        private MyAdapter() {
        }

        public int getCount() {
            return PayPasswordDialog.this.mNumbers.size();
        }

        public Integer getItem(int position) {
            return (Integer) PayPasswordDialog.this.mNumbers.get(position);
        }

        public long getItemId(int position) {
            return (long) position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = View.inflate(PayPasswordDialog.this.mContext, R.layout.item_password_number, (ViewGroup) null);
                holder = new ViewHolder();
                holder.tvNumber = (TextView) convertView.findViewById(R.id.btn_number);
                holder.ivDelete = (ImageView) convertView.findViewById(R.id.iv_delete);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            if (position < 9 || position == 10) {
                holder.tvNumber.setText(String.valueOf(PayPasswordDialog.this.mNumbers.get(position)));
                holder.tvNumber.setTypeface(AndroidUtilities.getTypeface("fonts/rmedium.ttf"));
            } else if (position == 9) {
                holder.tvNumber.setVisibility(4);
            } else if (position == 11) {
                holder.tvNumber.setVisibility(4);
                holder.ivDelete.setVisibility(0);
            }
            return convertView;
        }
    }

    class ViewHolder {
        ImageView ivDelete;
        TextView tvNumber;

        ViewHolder() {
        }
    }
}
