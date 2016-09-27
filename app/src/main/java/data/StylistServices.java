package data;

/**
 * Created by Obakeng Moshane on 2016-03-28.
 */
public class StylistServices {
    private int itemid;
    private String servicename, servicedescription,serviceimage;


    public StylistServices() {
    }

    public StylistServices(String servicename, String servicedescription,
                           String serviceimage) {
        super();
        this.servicename = servicename;
        this.servicedescription = servicedescription;
        this.serviceimage =serviceimage;

    }


    public String getservicename() {
        return servicename;
    }

    public void setServicename(String servicename) {
        this.servicename = servicename;
    }


    public String getServicedescription()
    {
        return servicedescription;
    }

    public void setServicedescription(String servicedescription)
    {
        this.servicedescription = servicedescription;
    }




    public String getServiceimage() {
        return serviceimage;
    }

    public void setServiceimage(String serviceimage) {
        this.serviceimage = serviceimage;
    }




}
