package mmud.beans;

/**
 *
 * @author maartenl
 */
public class CommandOutput
{

    private String image;
    private String description;
    private String title;
    private MmudLog log;
    private String sessionpassword;

    /**
     * Get the value of image
     *
     * @return the value of image
     */
    public String getImage()
    {
        return image;
    }

    /**
     * Set the value of image
     *
     * @param image new value of image
     */
    public void setImage(String image)
    {
        this.image = image;
    }


    /**
     * Get the value of title
     *
     * @return the value of title
     */
    public String getTitle()
    {
        return title;
    }

    /**
     * Set the value of title
     *
     * @param title new value of title
     */
    public void setTitle(String title)
    {
        this.title = title;
    }


    /**
     * Get the value of description
     *
     * @return the value of description
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * Set the value of description
     *
     * @param description new value of description
     */
    public void setDescription(String description)
    {
        this.description = description;
    }

    public MmudLog getLog()
    {
        return log;
    }

    public void setLog(MmudLog log)
    {
        this.log = log;
    }

    /**
     * @return the sessionpassword
     */
    public String getSessionpassword()
    {
        return sessionpassword;
    }

    /**
     * @param sessionpassword the sessionpassword to set
     */
    public void setSessionpassword(String sessionpassword)
    {
        this.sessionpassword = sessionpassword;
    }

}
