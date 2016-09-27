package data;

/**
 * Created by Obakeng Moshane on 2016-02-07.
 */
public class FeedItem {
    private int itemid;
    private String name, status, image, profilePic, timeStamp, url,handle;
    private String TotalComments;


    public FeedItem() {
    }

    public FeedItem(int itemid, String name, String image, String status,
                    String profilePic, String timeStamp, String url,String handle,String TotalComments) {
        super();
        this.itemid = itemid;
        this.name = name;
        this.image = image;
        this.status = status;
        this.profilePic = profilePic;
        this.timeStamp = timeStamp;
        this.url = url;
        this.handle = handle;
        this.TotalComments = TotalComments;
    }

    public int getItemId() {
        return itemid;
    }

    public void setId(int itemid) {
        this.itemid = itemid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImge() {
        return image;
    }

    public String getHandle()
    {
        return handle;
    }

    public void setHandle(String handle)
    {
        this.handle = handle;
    }

    public void setImge(String image) {
        this.image = image;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTotalComments()
    {
        return TotalComments;
    }
    public void setTotalComments(String TotalComments)
    {
        this.TotalComments = TotalComments;
    }
}
