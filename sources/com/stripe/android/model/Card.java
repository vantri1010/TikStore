package com.stripe.android.model;

import com.stripe.android.util.DateUtils;
import com.stripe.android.util.StripeTextUtils;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class Card {
    public static final String AMERICAN_EXPRESS = "American Express";
    public static final String DINERS_CLUB = "Diners Club";
    public static final String DISCOVER = "Discover";
    public static final String FUNDING_CREDIT = "credit";
    public static final String FUNDING_DEBIT = "debit";
    public static final String FUNDING_PREPAID = "prepaid";
    public static final String FUNDING_UNKNOWN = "unknown";
    public static final String JCB = "JCB";
    public static final String MASTERCARD = "MasterCard";
    public static final int MAX_LENGTH_AMERICAN_EXPRESS = 15;
    public static final int MAX_LENGTH_DINERS_CLUB = 14;
    public static final int MAX_LENGTH_STANDARD = 16;
    public static final String[] PREFIXES_AMERICAN_EXPRESS = {"34", "37"};
    public static final String[] PREFIXES_DINERS_CLUB = {"300", "301", "302", "303", "304", "305", "309", "36", "38", "39"};
    public static final String[] PREFIXES_DISCOVER = {"60", "62", "64", "65"};
    public static final String[] PREFIXES_JCB = {"35"};
    public static final String[] PREFIXES_MASTERCARD = {"2221", "2222", "2223", "2224", "2225", "2226", "2227", "2228", "2229", "223", "224", "225", "226", "227", "228", "229", "23", "24", "25", "26", "270", "271", "2720", "50", "51", "52", "53", "54", "55"};
    public static final String[] PREFIXES_VISA = {"4"};
    public static final String UNKNOWN = "Unknown";
    public static final String VISA = "Visa";
    private String addressCity;
    private String addressCountry;
    private String addressLine1;
    private String addressLine2;
    private String addressState;
    private String addressZip;
    private String brand;
    private String country;
    private String currency;
    private String cvc;
    private Integer expMonth;
    private Integer expYear;
    private String fingerprint;
    private String funding;
    private String last4;
    private String name;
    private String number;

    @Retention(RetentionPolicy.SOURCE)
    public @interface CardBrand {
    }

    @Retention(RetentionPolicy.SOURCE)
    public @interface FundingType {
    }

    public static class Builder {
        /* access modifiers changed from: private */
        public String addressCity;
        /* access modifiers changed from: private */
        public String addressCountry;
        /* access modifiers changed from: private */
        public String addressLine1;
        /* access modifiers changed from: private */
        public String addressLine2;
        /* access modifiers changed from: private */
        public String addressState;
        /* access modifiers changed from: private */
        public String addressZip;
        /* access modifiers changed from: private */
        public String brand;
        /* access modifiers changed from: private */
        public String country;
        /* access modifiers changed from: private */
        public String currency;
        /* access modifiers changed from: private */
        public final String cvc;
        /* access modifiers changed from: private */
        public final Integer expMonth;
        /* access modifiers changed from: private */
        public final Integer expYear;
        /* access modifiers changed from: private */
        public String fingerprint;
        /* access modifiers changed from: private */
        public String funding;
        /* access modifiers changed from: private */
        public String last4;
        /* access modifiers changed from: private */
        public String name;
        /* access modifiers changed from: private */
        public final String number;

        public Builder(String number2, Integer expMonth2, Integer expYear2, String cvc2) {
            this.number = number2;
            this.expMonth = expMonth2;
            this.expYear = expYear2;
            this.cvc = cvc2;
        }

        public Builder name(String name2) {
            this.name = name2;
            return this;
        }

        public Builder addressLine1(String address) {
            this.addressLine1 = address;
            return this;
        }

        public Builder addressLine2(String address) {
            this.addressLine2 = address;
            return this;
        }

        public Builder addressCity(String city) {
            this.addressCity = city;
            return this;
        }

        public Builder addressState(String state) {
            this.addressState = state;
            return this;
        }

        public Builder addressZip(String zip) {
            this.addressZip = zip;
            return this;
        }

        public Builder addressCountry(String country2) {
            this.addressCountry = country2;
            return this;
        }

        public Builder brand(String brand2) {
            this.brand = brand2;
            return this;
        }

        public Builder fingerprint(String fingerprint2) {
            this.fingerprint = fingerprint2;
            return this;
        }

        public Builder funding(String funding2) {
            this.funding = funding2;
            return this;
        }

        public Builder country(String country2) {
            this.country = country2;
            return this;
        }

        public Builder currency(String currency2) {
            this.currency = currency2;
            return this;
        }

        public Builder last4(String last42) {
            this.last4 = last42;
            return this;
        }

        public Card build() {
            return new Card(this);
        }
    }

    public Card(String number2, Integer expMonth2, Integer expYear2, String cvc2, String name2, String addressLine12, String addressLine22, String addressCity2, String addressState2, String addressZip2, String addressCountry2, String brand2, String last42, String fingerprint2, String funding2, String country2, String currency2) {
        this.number = StripeTextUtils.nullIfBlank(normalizeCardNumber(number2));
        this.expMonth = expMonth2;
        this.expYear = expYear2;
        this.cvc = StripeTextUtils.nullIfBlank(cvc2);
        this.name = StripeTextUtils.nullIfBlank(name2);
        this.addressLine1 = StripeTextUtils.nullIfBlank(addressLine12);
        this.addressLine2 = StripeTextUtils.nullIfBlank(addressLine22);
        this.addressCity = StripeTextUtils.nullIfBlank(addressCity2);
        this.addressState = StripeTextUtils.nullIfBlank(addressState2);
        this.addressZip = StripeTextUtils.nullIfBlank(addressZip2);
        this.addressCountry = StripeTextUtils.nullIfBlank(addressCountry2);
        this.brand = StripeTextUtils.asCardBrand(brand2) == null ? getBrand() : brand2;
        this.last4 = StripeTextUtils.nullIfBlank(last42) == null ? getLast4() : last42;
        this.fingerprint = StripeTextUtils.nullIfBlank(fingerprint2);
        this.funding = StripeTextUtils.asFundingType(funding2);
        this.country = StripeTextUtils.nullIfBlank(country2);
        this.currency = StripeTextUtils.nullIfBlank(currency2);
    }

    public Card(String number2, Integer expMonth2, Integer expYear2, String cvc2, String name2, String addressLine12, String addressLine22, String addressCity2, String addressState2, String addressZip2, String addressCountry2, String currency2) {
        this(number2, expMonth2, expYear2, cvc2, name2, addressLine12, addressLine22, addressCity2, addressState2, addressZip2, addressCountry2, (String) null, (String) null, (String) null, (String) null, (String) null, currency2);
    }

    public Card(String number2, Integer expMonth2, Integer expYear2, String cvc2) {
        this(number2, expMonth2, expYear2, cvc2, (String) null, (String) null, (String) null, (String) null, (String) null, (String) null, (String) null, (String) null, (String) null, (String) null, (String) null, (String) null, (String) null);
    }

    public boolean validateCard() {
        if (this.cvc == null) {
            if (!validateNumber() || !validateExpiryDate()) {
                return false;
            }
            return true;
        } else if (!validateNumber() || !validateExpiryDate() || !validateCVC()) {
            return false;
        } else {
            return true;
        }
    }

    public boolean validateNumber() {
        if (StripeTextUtils.isBlank(this.number)) {
            return false;
        }
        String rawNumber = this.number.trim().replaceAll("\\s+|-", "");
        if (StripeTextUtils.isBlank(rawNumber) || !StripeTextUtils.isWholePositiveNumber(rawNumber) || !isValidLuhnNumber(rawNumber)) {
            return false;
        }
        String updatedType = getBrand();
        if (AMERICAN_EXPRESS.equals(updatedType)) {
            if (rawNumber.length() == 15) {
                return true;
            }
            return false;
        } else if (DINERS_CLUB.equals(updatedType)) {
            if (rawNumber.length() == 14) {
                return true;
            }
            return false;
        } else if (rawNumber.length() == 16) {
            return true;
        } else {
            return false;
        }
    }

    public boolean validateExpiryDate() {
        if (validateExpMonth() && validateExpYear()) {
            return !DateUtils.hasMonthPassed(this.expYear.intValue(), this.expMonth.intValue());
        }
        return false;
    }

    public boolean validateCVC() {
        if (StripeTextUtils.isBlank(this.cvc)) {
            return false;
        }
        String cvcValue = this.cvc.trim();
        String updatedType = getBrand();
        boolean validLength = (updatedType == null && cvcValue.length() >= 3 && cvcValue.length() <= 4) || (AMERICAN_EXPRESS.equals(updatedType) && cvcValue.length() == 4) || cvcValue.length() == 3;
        if (!StripeTextUtils.isWholePositiveNumber(cvcValue) || !validLength) {
            return false;
        }
        return true;
    }

    public boolean validateExpMonth() {
        Integer num = this.expMonth;
        return num != null && num.intValue() >= 1 && this.expMonth.intValue() <= 12;
    }

    public boolean validateExpYear() {
        Integer num = this.expYear;
        return num != null && !DateUtils.hasYearPassed(num.intValue());
    }

    public String getNumber() {
        return this.number;
    }

    @Deprecated
    public void setNumber(String number2) {
        this.number = number2;
        this.brand = null;
        this.last4 = null;
    }

    public String getCVC() {
        return this.cvc;
    }

    @Deprecated
    public void setCVC(String cvc2) {
        this.cvc = cvc2;
    }

    public Integer getExpMonth() {
        return this.expMonth;
    }

    @Deprecated
    public void setExpMonth(Integer expMonth2) {
        this.expMonth = expMonth2;
    }

    public Integer getExpYear() {
        return this.expYear;
    }

    @Deprecated
    public void setExpYear(Integer expYear2) {
        this.expYear = expYear2;
    }

    public String getName() {
        return this.name;
    }

    @Deprecated
    public void setName(String name2) {
        this.name = name2;
    }

    public String getAddressLine1() {
        return this.addressLine1;
    }

    @Deprecated
    public void setAddressLine1(String addressLine12) {
        this.addressLine1 = addressLine12;
    }

    public String getAddressLine2() {
        return this.addressLine2;
    }

    @Deprecated
    public void setAddressLine2(String addressLine22) {
        this.addressLine2 = addressLine22;
    }

    public String getAddressCity() {
        return this.addressCity;
    }

    @Deprecated
    public void setAddressCity(String addressCity2) {
        this.addressCity = addressCity2;
    }

    public String getAddressZip() {
        return this.addressZip;
    }

    @Deprecated
    public void setAddressZip(String addressZip2) {
        this.addressZip = addressZip2;
    }

    public String getAddressState() {
        return this.addressState;
    }

    @Deprecated
    public void setAddressState(String addressState2) {
        this.addressState = addressState2;
    }

    public String getAddressCountry() {
        return this.addressCountry;
    }

    @Deprecated
    public void setAddressCountry(String addressCountry2) {
        this.addressCountry = addressCountry2;
    }

    public String getCurrency() {
        return this.currency;
    }

    @Deprecated
    public void setCurrency(String currency2) {
        this.currency = currency2;
    }

    public String getLast4() {
        if (!StripeTextUtils.isBlank(this.last4)) {
            return this.last4;
        }
        String str = this.number;
        if (str == null || str.length() <= 4) {
            return null;
        }
        String str2 = this.number;
        String substring = str2.substring(str2.length() - 4, this.number.length());
        this.last4 = substring;
        return substring;
    }

    @Deprecated
    public String getType() {
        return getBrand();
    }

    public String getBrand() {
        String evaluatedType;
        if (StripeTextUtils.isBlank(this.brand) && !StripeTextUtils.isBlank(this.number)) {
            if (StripeTextUtils.hasAnyPrefix(this.number, PREFIXES_AMERICAN_EXPRESS)) {
                evaluatedType = AMERICAN_EXPRESS;
            } else if (StripeTextUtils.hasAnyPrefix(this.number, PREFIXES_DISCOVER)) {
                evaluatedType = DISCOVER;
            } else if (StripeTextUtils.hasAnyPrefix(this.number, PREFIXES_JCB)) {
                evaluatedType = JCB;
            } else if (StripeTextUtils.hasAnyPrefix(this.number, PREFIXES_DINERS_CLUB)) {
                evaluatedType = DINERS_CLUB;
            } else if (StripeTextUtils.hasAnyPrefix(this.number, PREFIXES_VISA)) {
                evaluatedType = VISA;
            } else if (StripeTextUtils.hasAnyPrefix(this.number, PREFIXES_MASTERCARD)) {
                evaluatedType = MASTERCARD;
            } else {
                evaluatedType = UNKNOWN;
            }
            this.brand = evaluatedType;
        }
        return this.brand;
    }

    public String getFingerprint() {
        return this.fingerprint;
    }

    public String getFunding() {
        return this.funding;
    }

    public String getCountry() {
        return this.country;
    }

    private Card(Builder builder) {
        String str;
        String str2;
        this.number = StripeTextUtils.nullIfBlank(normalizeCardNumber(builder.number));
        this.expMonth = builder.expMonth;
        this.expYear = builder.expYear;
        this.cvc = StripeTextUtils.nullIfBlank(builder.cvc);
        this.name = StripeTextUtils.nullIfBlank(builder.name);
        this.addressLine1 = StripeTextUtils.nullIfBlank(builder.addressLine1);
        this.addressLine2 = StripeTextUtils.nullIfBlank(builder.addressLine2);
        this.addressCity = StripeTextUtils.nullIfBlank(builder.addressCity);
        this.addressState = StripeTextUtils.nullIfBlank(builder.addressState);
        this.addressZip = StripeTextUtils.nullIfBlank(builder.addressZip);
        this.addressCountry = StripeTextUtils.nullIfBlank(builder.addressCountry);
        if (StripeTextUtils.nullIfBlank(builder.last4) == null) {
            str = getLast4();
        } else {
            str = builder.last4;
        }
        this.last4 = str;
        if (StripeTextUtils.asCardBrand(builder.brand) == null) {
            str2 = getBrand();
        } else {
            str2 = builder.brand;
        }
        this.brand = str2;
        this.fingerprint = StripeTextUtils.nullIfBlank(builder.fingerprint);
        this.funding = StripeTextUtils.asFundingType(builder.funding);
        this.country = StripeTextUtils.nullIfBlank(builder.country);
        this.currency = StripeTextUtils.nullIfBlank(builder.currency);
    }

    private boolean isValidLuhnNumber(String number2) {
        boolean isOdd = true;
        int sum = 0;
        int index = number2.length() - 1;
        while (true) {
            boolean z = false;
            if (index >= 0) {
                char c = number2.charAt(index);
                if (!Character.isDigit(c)) {
                    return false;
                }
                int digitInteger = Integer.parseInt("" + c);
                if (!isOdd) {
                    z = true;
                }
                isOdd = z;
                if (isOdd) {
                    digitInteger *= 2;
                }
                if (digitInteger > 9) {
                    digitInteger -= 9;
                }
                sum += digitInteger;
                index--;
            } else if (sum % 10 == 0) {
                return true;
            } else {
                return false;
            }
        }
    }

    private String normalizeCardNumber(String number2) {
        if (number2 == null) {
            return null;
        }
        return number2.trim().replaceAll("\\s+|-", "");
    }
}
