package com.example.spms.utils;

public class Constant {
    public static final String DATABASE_NAME="info.db";//数据库名称
    public static final int DATABASE_VERSION=1;//数据库的版本号
    //  验收表头和详情表名
    public static final String TABLE_NAME_CHECKHEAD="check_head";//表名
    public static final String TABLE_NAME_CHECKDETAIL="check_detail";//表名
    public static final String _ID="_id";//ID自增主键
    //  验收表头字段
    public static final String CHECKJOB_ID="checkjob_id";//表头ID
    public static final String WAREHOUSE="warehouse_id";//仓库名
    public static final String ITEM_NUMBER="item_number";//物料总数
    //  验收详情字段
    public static final String ITEM_ID="item_id";//物料号
    public static final String REQUESTNUMBER="request_number";//应收数
    public static final String ACTUALNUMBER="actual_number";//实收数
    public static final String LOCATION="location";//库位信息
    public static final String CHECKJOB_ID_COPY="checkjob_id_copy";//库位信息
}
