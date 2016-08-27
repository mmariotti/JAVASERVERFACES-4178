package it.shape.core.jsf.context.cache;

import javax.faces.view.facelets.FaceletCacheFactory;


/**
 * The Class FaceletCacheFactoryWrapper.
 *
 * @author Michele Mariotti
 */
public abstract class FaceletCacheFactoryWrapper extends FaceletCacheFactory
{
    /** The wrapped. */
    protected final FaceletCacheFactory wrapped;

    /**
     * Instantiates a new facelet cache factory wrapper.
     *
     * @param wrapped the wrapped
     */
    public FaceletCacheFactoryWrapper(FaceletCacheFactory wrapped)
    {
        this.wrapped = wrapped;
    }

    @Override
    public FaceletCacheFactory getWrapped()
    {
        return wrapped;
    }
}