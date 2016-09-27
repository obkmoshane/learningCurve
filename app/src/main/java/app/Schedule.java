package app;

/**
 * Created by Obakeng Moshane on 2016-02-29.
 */
public class Schedule {
    private int id;
    private String cfirstname, clastname, cemail, ccontact; //clients information
    private String datebooked, timebooked;
    private String stylistid, sfirstname, slastname; //stylist information
    private String bookingtype; //either nails or hair


    public Schedule() {
        //
}
    public Schedule(String cfirstname,String clastname,String cemail,String ccontact,String datebooked,
                    String timebooked,String stylistid,String sfirstname,String slastname,String bookingtype)
    {
        super();
        this.cfirstname = cfirstname;
        this.clastname = clastname;
        this.cemail = cemail;
        this.ccontact = ccontact;
        this.datebooked = datebooked;
        this.timebooked = timebooked;
        this.stylistid = stylistid;
        this.sfirstname = sfirstname;
        this.slastname = slastname;
        this.bookingtype = bookingtype;
    }

    public void setCfirstname(String cfirstname)
    {
        this.cfirstname = cfirstname;
    }
    public void setClastname(String clastname)
    {
        this.clastname = clastname;
    }
    public void setCemail(String cemail)
    {
        this.cemail = cemail;
    }
    public void setCcontact(String ccontact)
    {
        this.ccontact =ccontact;
    }
    public void setDatebooked(String datebooked)
    {
        this.datebooked = datebooked;
    }
    public void setTimebooked(String timebooked)
    {
        this.timebooked = timebooked;
    }
    public void setStylistid(String stylistid)
    {
        this.stylistid = stylistid;
    }
    public void setSfirstname(String sfirstname)
    {
        this.sfirstname = sfirstname;
    }
    public void setSlastname(String slastname)
    {
        this.slastname = slastname;
    }
    public void setBookingtype(String bookingtype)
    {
        this.bookingtype  = bookingtype;
    }

    public String getCfirstname()
    {
    return cfirstname;
    }
    public String getclastname()
    {
        return clastname;
    }
    public String getCemail()
    {
        return cemail;
    }
    public String getCcontact()
    {
        return ccontact;
    }
    public String getDatebooked()
    {
        return datebooked;
    }
    public String getTimebooked()
    {
        return timebooked;
    }
    public String getStylistid()
    {
        return stylistid;
    }
    public String getSfirstname()
    {
        return sfirstname;
    }
    public String getSlastname()
    {
        return slastname;
    }
    public String getBookingtype()
    {
        return bookingtype;
    }





}