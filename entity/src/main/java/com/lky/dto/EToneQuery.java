package com.lky.dto;

/**
 * 易通支付长时间未回调时，进行查询对象
 *
 * @author zhangzheng
 * @version 1.0
 * @since 2017-12-20
 */
public class EToneQuery {

    /**
     * 易通代付批次号，唯一
     */
    private String batchNo;

    /**
     * 提现记录id
     */
    private Integer withdrawRecordId;

    /**
     * 发起查询次数计数，当请求24次查询还没有结果时，
     * 将不再查询，查询间隔时间默认为半小时
     */
    private Integer queryCount = 24;

    /**
     * 执行查询时间
     */
    private Long queryTime;

    public String getBatchNo() {
        return batchNo;
    }

    public void setBatchNo(String batchNo) {
        this.batchNo = batchNo;
    }

    public Integer getWithdrawRecordId() {
        return withdrawRecordId;
    }

    public void setWithdrawRecordId(Integer withdrawRecordId) {
        this.withdrawRecordId = withdrawRecordId;
    }

    public Long getQueryTime() {
        return queryTime;
    }

    public void setQueryTime(Long queryTime) {
        this.queryTime = queryTime;
    }

    public Integer getQueryCount() {
        return queryCount;
    }

    public void setQueryCount(Integer queryCount) {
        this.queryCount = queryCount;
    }

    @Override
    public boolean equals(Object obj) {
        return (this.batchNo.equals(((EToneQuery)obj).batchNo));
    }


    @Override
    public String toString() {
        return "EToneQuery{" +
                "batchNo='" + batchNo + '\'' +
                ", withdrawRecordId=" + withdrawRecordId +
                ", queryCount=" + queryCount +
                ", queryTime=" + queryTime +
                '}';
    }
}
