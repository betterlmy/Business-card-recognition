
package com.example.demo.bean;

import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;
import java.util.List;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class WordsResult {

    @SerializedName("ADDR")
    private List<String> mADDR;
    @SerializedName("COMPANY")
    private List<String> mCOMPANY;
    @SerializedName("EMAIL")
    private List<String> mEMAIL;
    @SerializedName("FAX")
    private List<String> mFAX;
    @SerializedName("MOBILE")
    private List<String> mMOBILE;
    @SerializedName("NAME")
    private List<String> mNAME;
    @SerializedName("PC")
    private List<String> mPC;
    @SerializedName("TEL")
    private List<String> mTEL;
    @SerializedName("TITLE")
    private List<String> mTITLE;
    @SerializedName("URL")
    private List<String> mURL;

    public List<String> getADDR() {
        return mADDR;
    }

    public void setADDR(List<String> aDDR) {
        mADDR = aDDR;
    }

    public List<String> getCOMPANY() {
        return mCOMPANY;
    }

    public void setCOMPANY(List<String> cOMPANY) {
        mCOMPANY = cOMPANY;
    }

    public List<String> getEMAIL() {
        return mEMAIL;
    }

    public void setEMAIL(List<String> eMAIL) {
        mEMAIL = eMAIL;
    }

    public List<String> getFAX() {
        return mFAX;
    }

    public void setFAX(List<String> fAX) {
        mFAX = fAX;
    }

    public List<String> getMOBILE() {
        return mMOBILE;
    }

    public void setMOBILE(List<String> mOBILE) {
        mMOBILE = mOBILE;
    }

    public List<String> getNAME() {
        return mNAME;
    }

    public void setNAME(List<String> nAME) {
        mNAME = nAME;
    }

    public List<String> getPC() {
        return mPC;
    }

    public void setPC(List<String> pC) {
        mPC = pC;
    }

    public List<String> getTEL() {
        return mTEL;
    }

    public void setTEL(List<String> tEL) {
        mTEL = tEL;
    }

    public List<String> getTITLE() {
        return mTITLE;
    }

    public void setTITLE(List<String> tITLE) {
        mTITLE = tITLE;
    }

    public List<String> getURL() {
        return mURL;
    }

    public void setURL(List<String> uRL) {
        mURL = uRL;
    }

    @Override
    public String toString() {
        return "WordsResult{" +
                "mADDR=" + mADDR +
                ", mCOMPANY=" + mCOMPANY +
                ", mEMAIL=" + mEMAIL +
                ", mFAX=" + mFAX +
                ", mMOBILE=" + mMOBILE +
                ", mNAME=" + mNAME +
                ", mPC=" + mPC +
                ", mTEL=" + mTEL +
                ", mTITLE=" + mTITLE +
                ", mURL=" + mURL +
                '}';
    }
}
