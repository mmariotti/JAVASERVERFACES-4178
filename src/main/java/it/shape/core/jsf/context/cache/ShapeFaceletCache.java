package it.shape.core.jsf.context.cache;

import java.io.IOException;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import javax.faces.FacesException;
import javax.faces.view.facelets.Facelet;
import javax.faces.view.facelets.FaceletCache;
import it.shape.core.util.URLUtils;


/**
 * The Class ShapeFaceletCache.
 *
 * @author Michele Mariotti
 */
public abstract class ShapeFaceletCache extends FaceletCache<Facelet>
{
    /** The instance. */
    protected static volatile ShapeFaceletCache INSTANCE;

    /** The member cache. */
    protected Map<URL, FaceletRecord> memberCache = new ConcurrentHashMap<>();

    /** The metadata cache. */
    protected Map<URL, FaceletRecord> metadataCache = new ConcurrentHashMap<>();

    /**
     * Instantiates a new shape facelet cache.
     */
    protected ShapeFaceletCache()
    {
        INSTANCE = this;
    }

    /**
     * Gets the single instance of ShapeFaceletCache.
     *
     * @return single instance of ShapeFaceletCache
     */
    public static ShapeFaceletCache getInstance()
    {
        return INSTANCE;
    }

    /**
     * Gets the facelet.
     *
     * @param key the key
     * @param url the url
     * @return the facelet
     */
    protected Facelet getFacelet(FaceletCacheKey key, URL url)
    {
        Map<URL, FaceletRecord> cache = getLocalCache(key);
        FaceletRecord record = cache.compute(url, (u, r) -> computeFaceletRecord(key, u, r));
        Facelet facelet = record.getFacelet();
        return facelet;
    }

    /**
     * Checks if is cached.
     *
     * @param key the key
     * @param url the url
     * @return true, if is cached
     */
    protected boolean isCached(FaceletCacheKey key, URL url)
    {
        Map<URL, FaceletRecord> cache = getLocalCache(key);
        FaceletRecord record = cache.computeIfPresent(url, (u, r) -> checkFaceletRecord(key, u, r));
        return record != null;
    }

    /**
     * Compute facelet record.
     *
     * @param key the key
     * @param url the url
     * @param record the record
     * @return the facelet record
     */
    protected FaceletRecord computeFaceletRecord(FaceletCacheKey key, URL url, FaceletRecord record)
    {
        if(record == null || checkFaceletRecord(key, url, record) == null)
        {
            return buildFaceletRecord(key, url);
        }

        return record;
    }

    /**
     * Builds the facelet record.
     *
     * @param key the key
     * @param url the url
     * @return the facelet record
     */
    protected FaceletRecord buildFaceletRecord(FaceletCacheKey key, URL url)
    {
        try
        {
            MemberFactory<Facelet> factory = getFactory(key);
            Facelet facelet = factory.newInstance(url);
            long lastModified = URLUtils.getLastModified(url);
            FaceletRecord record = new FaceletRecord(facelet, lastModified);
            return record;
        }
        catch(IOException e)
        {
            throw new FacesException(e.getMessage(), e);
        }
    }

    /**
     * Check facelet record.
     *
     * @param key the key
     * @param url the url
     * @param record the record
     * @return the facelet record
     */
    protected FaceletRecord checkFaceletRecord(FaceletCacheKey key, URL url, FaceletRecord record)
    {
        return record;
    }

    /**
     * Gets the cache.
     *
     * @param key the key
     * @return the cache
     */
    protected Map<URL, FaceletRecord> getLocalCache(FaceletCacheKey key)
    {
        if(key == FaceletCacheKey.MEMBER)
        {
            return memberCache;
        }

        if(key == FaceletCacheKey.METADATA)
        {
            return metadataCache;
        }

        throw new IllegalArgumentException();
    }

    /**
     * Gets the factory.
     *
     * @param key the key
     * @return the factory
     */
    protected MemberFactory<Facelet> getFactory(FaceletCacheKey key)
    {
        if(key == FaceletCacheKey.MEMBER)
        {
            return getMemberFactory();
        }

        if(key == FaceletCacheKey.METADATA)
        {
            return getMetadataMemberFactory();
        }

        throw new IllegalArgumentException();
    }

    @Override
    public Facelet getFacelet(URL url) throws IOException
    {
        return getFacelet(FaceletCacheKey.MEMBER, url);
    }

    @Override
    public Facelet getViewMetadataFacelet(URL url) throws IOException
    {
        return getFacelet(FaceletCacheKey.METADATA, url);
    }

    @Override
    public boolean isFaceletCached(URL url)
    {
        return isCached(FaceletCacheKey.MEMBER, url);
    }

    @Override
    public boolean isViewMetadataFaceletCached(URL url)
    {
        return isCached(FaceletCacheKey.METADATA, url);
    }

    /**
     * Clear facelets.
     */
    public void clearFacelets()
    {
        getLocalCache(FaceletCacheKey.MEMBER).clear();
    }

    /**
     * Clear view metadata facelets.
     */
    public void clearViewMetadataFacelets()
    {
        getLocalCache(FaceletCacheKey.METADATA).clear();
    }

    /**
     * Clear all.
     */
    public void clearAll()
    {
        clearViewMetadataFacelets();
        clearFacelets();
    }
}