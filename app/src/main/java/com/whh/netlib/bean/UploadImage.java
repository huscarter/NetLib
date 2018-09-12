package com.whh.netlib.bean;

/**
 * Create by huscarter@163.com on 9/12/18
 * <p>
 * 类说明:<BR/>
 * 图片上传
 */
public class UploadImage extends BaseBean {
    private String image_id;
    private String url;
    private String l_url;
    private String m_url;
    private String s_url;

    public String getImage_id() {
        return image_id;
    }

    public void setImage_id(String image_id) {
        this.image_id = image_id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getL_url() {
        return l_url;
    }

    public void setL_url(String l_url) {
        this.l_url = l_url;
    }

    public String getM_url() {
        return m_url;
    }

    public void setM_url(String m_url) {
        this.m_url = m_url;
    }

    public String getS_url() {
        return s_url;
    }

    public void setS_url(String s_url) {
        this.s_url = s_url;
    }

}
