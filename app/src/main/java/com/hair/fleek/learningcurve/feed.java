package com.hair.fleek.learningcurve;

/**
 * Created by Obakeng Moshane on 2016-02-04.
 */
public class feed {
    private String title, dpicURL,uploadURL;
    private String handle;
    private String dateuploaded;
    private String msg;

    public feed(){}

    public feed(String title, String dpicURL,String uploadURL, String handle, String dateuploaded,
                String msg) {
        this.title = title;
        this.dpicURL = dpicURL;
        this.uploadURL = uploadURL;
        this.handle = handle;
        this.dateuploaded = dateuploaded;
        this.msg = msg;
    }


    public String getTitle()
    {
        return title;
    }
    public String getDpicURL()
    {
        return getDpicURL();
    }
    public String getUploadURL()
    {
        return getUploadURL();
    }
    public String getDateuploaded()
    {
        return dateuploaded;
    }
    public String getHandle()
    {
        return handle;
    }
    public String getMsg()
    {
        return msg;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }
    public void setDpicURL(String dpicURL)
    {

        this.dpicURL = dpicURL;
    }
    public void setUploadURL(String uploadURL)
    {
        this.uploadURL = uploadURL;
    }
    public void setHandle(String handle)
    {
        this.handle = handle;
    }
    public void setDateuploaded(String dateuploaded)
    {
        this.dateuploaded= dateuploaded;
    }
    public void setMsg(String msg)
    {
        this.msg = msg;
    }
}
