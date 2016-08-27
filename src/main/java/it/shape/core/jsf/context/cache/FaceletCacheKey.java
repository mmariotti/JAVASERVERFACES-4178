package it.shape.core.jsf.context.cache;

/**
 * The Enum FaceletCacheKey.
 *
 * @author Michele Mariotti
 */
public enum FaceletCacheKey
{
    /** The member. */
    MEMBER,

    /** The metadata. */
    METADATA;

    @Override
    public String toString()
    {
        return getClass().getName() + "." + name();
    }
}