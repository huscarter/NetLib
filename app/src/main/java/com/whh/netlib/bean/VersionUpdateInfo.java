package com.whh.netlib.bean;

/**
 * Create by huscarter@163.com on 9/12/18
 * <p>
 * 类说明:<BR/>
 * 自动更新的接口
 */
public class VersionUpdateInfo extends BaseBean {

    /**
     * version : 2.0.0
     * filesize : 25M
     * update_content : ["添加","删除","优化"]
     */
    private Data data;

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public static class Data {
        private String version;
        private String filesize;
        private String is_mandatory;// 0不强制，1强制
        private String update_content;

        private String is_notified;//是否开启提醒
        private String down_url;//下载url
        private String hot_url;//热修复url,若果有值就进行热修复,没有就不需要
        private String is_online;//0-未上线1-已上线

        public String getIs_notified() {
            return is_notified;
        }

        public void setIs_notified(String is_notified) {
            this.is_notified = is_notified;
        }

        public String getDown_url() {
            return down_url;
        }

        public void setDown_url(String down_url) {
            this.down_url = down_url;
        }

        public String getHot_url() {
            return hot_url;
        }

        public void setHot_url(String hot_url) {
            this.hot_url = hot_url;
        }

        public String getIs_online() {
            return is_online;
        }

        public void setIs_online(String is_online) {
            this.is_online = is_online;
        }

        public String getVersion() {
            if (null == version || version.equals("")) {
                return "1";
            }
            return version;
        }

        public void setVersion(String version) {
            this.version = version;
        }

        public String getIs_mandatory() {
            return is_mandatory;
        }

        public void setIs_mandatory(String is_mandatory) {
            this.is_mandatory = is_mandatory;
        }

        public String getFilesize() {
            return filesize;
        }

        public void setFilesize(String filesize) {
            this.filesize = filesize;
        }

        public String getUpdate_content() {
            return update_content;
        }

        public void setUpdate_content(String update_content) {
            this.update_content = update_content;
        }
    }

}
