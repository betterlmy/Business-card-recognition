
package com.example.demo.bean;

import com.google.gson.annotations.SerializedName;

import javax.annotation.Generated;

@Generated("net.hexar.json2pojo")
@SuppressWarnings("unused")
public class Result {

    @SerializedName("log_id")
    private Long mLogId;
    @SerializedName("words_result")
    private WordsResult mWordsResult;
    @SerializedName("words_result_num")
    private Long mWordsResultNum;

    public Long getLogId() {
        return mLogId;
    }

    public void setLogId(Long logId) {
        mLogId = logId;
    }

    public WordsResult getWordsResult() {
        return mWordsResult;
    }

    public void setWordsResult(WordsResult wordsResult) {
        mWordsResult = wordsResult;
    }

    public Long getWordsResultNum() {
        return mWordsResultNum;
    }

    public void setWordsResultNum(Long wordsResultNum) {
        mWordsResultNum = wordsResultNum;
    }

    @Override
    public String toString() {
        return "Result{" +
                "mLogId=" + mLogId +
                ", mWordsResult=" + mWordsResult +
                ", mWordsResultNum=" + mWordsResultNum +
                '}';
    }

}
