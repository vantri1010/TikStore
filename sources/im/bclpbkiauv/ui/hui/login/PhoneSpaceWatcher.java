package im.bclpbkiauv.ui.hui.login;

import android.text.Editable;
import android.text.TextWatcher;

public class PhoneSpaceWatcher implements TextWatcher {
    private int actionPosition;
    private int characterAction = -1;
    private boolean ignoreOnPhoneChange;
    private String phoneNumberFormat;
    private int start;

    public PhoneSpaceWatcher(String phoneNumberFormat2) {
        this.phoneNumberFormat = phoneNumberFormat2;
    }

    public void beforeTextChanged(CharSequence s, int start2, int count, int after) {
        if (count == 0 && after == 1) {
            this.characterAction = 1;
        } else if (count != 1 || after != 0) {
            this.characterAction = -1;
        } else if (s.charAt(start2) != ' ' || start2 <= 0) {
            this.characterAction = 2;
        } else {
            this.characterAction = 3;
            this.actionPosition = start2 - 1;
        }
    }

    public void onTextChanged(CharSequence s, int start2, int before, int count) {
        this.start = start2;
    }

    public void afterTextChanged(Editable s) {
        int i;
        int i2;
        if (!this.ignoreOnPhoneChange) {
            int start2 = this.start;
            String str = s.toString();
            if (this.characterAction == 3) {
                str = str.substring(0, this.actionPosition) + str.substring(this.actionPosition + 1);
                start2--;
            }
            StringBuilder builder = new StringBuilder(str.length());
            for (int a = 0; a < str.length(); a++) {
                String ch = str.substring(a, a + 1);
                if ("0123456789".contains(ch)) {
                    builder.append(ch);
                }
            }
            this.ignoreOnPhoneChange = true;
            if (this.phoneNumberFormat != null) {
                int a2 = 0;
                while (true) {
                    if (a2 >= builder.length()) {
                        break;
                    } else if (a2 < this.phoneNumberFormat.length()) {
                        if (this.phoneNumberFormat.charAt(a2) == ' ') {
                            builder.insert(a2, ' ');
                            a2++;
                            if (!(start2 != a2 || (i2 = this.characterAction) == 2 || i2 == 3)) {
                                start2++;
                            }
                        }
                        a2++;
                    } else {
                        builder.insert(a2, ' ');
                        if (start2 == a2 + 1 && (i = this.characterAction) != 2 && i != 3) {
                            int start3 = start2 + 1;
                        }
                    }
                }
            }
            s.replace(0, s.length(), builder);
            this.ignoreOnPhoneChange = false;
        }
    }

    public void setPhoneNumberFormat(String phoneNumberFormat2) {
        this.phoneNumberFormat = phoneNumberFormat2;
    }
}
