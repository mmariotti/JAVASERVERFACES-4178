package it.shape.core.jsf.context.cache;

import java.io.IOException;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import javax.faces.FacesException;
import javax.faces.context.FacesContext;
import it.shape.core.util.URLUtils;


/**
 * The Class DevelopmentFaceletCache.
 *
 * @author Michele Mariotti
 */
public class DevelopmentFaceletCache extends ShapeFaceletCache
{
    /**
     * Instantiates a new development facelet cache.
     *
     * @param period the period
     */
    public DevelopmentFaceletCache()
    {
        super();
    }

    @Override
    protected FaceletRecord checkFaceletRecord(FaceletCacheKey key, URL url, FaceletRecord record)
    {
        try
        {
            Set<URL> urls = (Set<URL>) FacesContext.getCurrentInstance()
                .getAttributes()
                .computeIfAbsent(key, x -> new HashSet<>());

            if(urls.add(url))
            {
                long lastModified = URLUtils.getLastModified(url);
                if(lastModified != record.getLastModified())
                {
                    return null;
                }
            }

            return record;
        }
        catch(IOException e)
        {
            throw new FacesException(e.getMessage(), e);
        }
    }
}