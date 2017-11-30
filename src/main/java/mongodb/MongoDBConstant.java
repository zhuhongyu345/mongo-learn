package mongodb;

public interface MongoDBConstant {

    //元数据collection
    public static final String DB_NAME_META = "meta";

    //平台配置项数据
    public static final String DB_NAME_SETTINGS = "settings";

    //节目库collection
    public static final String DB_NAME_PROGRAM = "program";

    //内容审核库collection
    public static final String DB_NAME_TASKMANUAL = "taskmanual";

    //内容审核数量统计collection
    public static final String DB_NAME_AUDIT = "audit";

    //剧集库collection
    public static final String DB_NAME_SERIES = "series";

    //数据来源配置collection
    public static final String DB_NAME_SOURCECONFIG = "sourceconfig";

    //工作队列配置
    public static final String DB_NAME_JOB = "job";

    //供应商的program统计数据
    public static final String DB_NAME_PROVIDERASSET = "providerasset";

    //地区的program统计数据
    public static final String DB_NAME_REGIONASSET = "regionasset";

    //类型的program统计数据
    public static final String DB_NAME_PROGRAMTYPEASSET = "programtypeasset";

    //每天的数量统计
    public static final String DB_NAME_DAILYCOUNT = "dailycount";

    //每周的数量统计
    public static final String DB_NAME_WEEKCOUNT = "weekcount";

    //每月的数量统计
    public static final String DB_NAME_MONTHCOUNT = "monthcount";

    //每年的数量统计
    public static final String DB_NAME_YEARCOUNT = "yearcount";
    //asset节目库
    public static final String DB_NAME_ASSET = "asset";
}