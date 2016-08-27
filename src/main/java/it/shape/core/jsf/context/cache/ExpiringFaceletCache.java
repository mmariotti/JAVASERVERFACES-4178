package it.shape.core.jsf.context.cache;

import java.io.IOException;
import java.net.URL;
import javax.faces.FacesException;
import it.shape.core.util.URLUtils;


/**
 * The Class ExpiringFaceletCache.
 *
 * @author Michele Mariotti
 */
public class ExpiringFaceletCache extends ShapeFaceletCache
{
    /** The period. */
    protected final long period;

    /**
     * Instantiates a new expiring facelet cache.
     *
     * @param period the period
     */
    public ExpiringFaceletCache(long period)
    {
        super();
        this.period = period;
    }

    @Override
    protected FaceletRecord checkFaceletRecord(FaceletCacheKey key, URL url, FaceletRecord record)
    {
        try
        {
            long now = System.currentTimeMillis();
            if(now > record.getLastChecked() + period)
            {
                long lastModified = URLUtils.getLastModified(url);
                if(lastModified != record.getLastModified())
                {
                    return null;
                }

                record.setLastChecked(now);
            }

            return record;
        }
        catch(IOException e)
        {
            throw new FacesException(e.getMessage(), e);
        }
    }
}