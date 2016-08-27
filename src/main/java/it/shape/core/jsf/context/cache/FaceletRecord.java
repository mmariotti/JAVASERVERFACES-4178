package it.shape.core.jsf.context.cache;

import javax.faces.view.facelets.Facelet;


/**
 * The Class FaceletRecord.
 *
 * @author Michele Mariotti
 */
public class FaceletRecord
{
    /** The facelet. */
    protected final Facelet facelet;

    /** The last modified. */
    protected final long lastModified;

    /** The last checked. */
    protected long lastChecked;

    /**
     * Instantiates a new facelet record.
     *
     * @param facelet the facelet
     * @param lastModified the last modified
     */
    public FaceletRecord(Facelet facelet, long lastModified)
    {
        this.facelet = facelet;
        this.lastModified = lastModified;
        lastChecked = System.currentTimeMillis();
    }

    /**
     * Gets the last modified.
     *
     * @return the last modified
     */
    public long getLastModified()
    {
        return lastModified;
    }

    /**
     * Gets the facelet.
     *
     * @return the facelet
     */
    public Facelet getFacelet()
    {
        return facelet;
    }

    /**
     * Gets the last checked.
     *
     * @return the last checked
     */
    public long getLastChecked()
    {
        return lastChecked;
    }

    /**
     * Sets the last checked.
     *
     * @param lastChecked the new last checked
     */
    public void setLastChecked(long lastChecked)
    {
        this.lastChecked = lastChecked;
    }
}