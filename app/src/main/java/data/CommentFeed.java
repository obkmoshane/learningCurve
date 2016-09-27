package data;

/**
 * Created by Obakeng Moshane on 2016-03-28.
 */
public class CommentFeed {
    private int itemid;
    private String name, comment, profilePic, timeStamp,handle;


    public CommentFeed() {
    }

    public CommentFeed(int itemid, String name, String comment,
                    String profilePic, String timeStamp,String handle) {
        super();
        this.itemid = itemid;
        this.name = name;
        this.comment = comment;
        this.profilePic = profilePic;
        this.timeStamp = timeStamp;
        this.handle = handle;
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


    public String getHandle()
    {
        return handle;
    }

    public void setHandle(String handle)
    {
        this.handle = handle;
    }


    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
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

}
