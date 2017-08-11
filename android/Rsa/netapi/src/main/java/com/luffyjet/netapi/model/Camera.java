package com.luffyjet.netapi.model;

import java.util.List;

/**
 * Title :    
 * Author : luffyjet 
 * Date : 2017/7/12
 * Project : bdplayer-sample
 * Site : http://www.luffyjet.com
 */

public class Camera extends BaseModel {
    public boolean audioEnabled;
    public String backupType;
    public boolean controlEnabled;
    public String dewarpingParams;
    public String failoverPriority;
    public String groupId;
    public String groupName;
    public String id;
    public boolean licenseUsed;
    public String mac;
    public boolean manuallyAdded;
    public int maxArchiveDays;
    public int minArchiveDays;
    public String model;
    public String motionMask;
    public String motionType;
    public String name;
    public String parentId;
    public String physicalId;
    public String preferedServerId;
    public boolean scheduleEnabled;
    public String secondaryStreamQuality;
    public String status;
    public String statusFlags;
    public String typeId;
    public String url;
    public String userDefinedGroupName;
    public String vendor;
    public List<AddParams> addParams;
    public List<Object> scheduleTasks;

    public static class AddParams {
        /**
         * name : DeviceUrl
         * value : http://10.0.49.6:80/onvif/device_service
         */
        public String name;
        public String value;
    }
}
