package it.shape.core.jsf.context.cache;

import javax.faces.application.ViewHandler;
import javax.faces.context.FacesContext;
import javax.faces.view.facelets.FaceletCacheFactory;


/**
 * A factory for creating ShapeFaceletCache objects.
 *
 * @author Michele Mariotti
 */
public class ShapeFaceletCacheFactory extends FaceletCacheFactoryWrapper
{
    /**
     * Instantiates a new shape facelet cache factory.
     *
     * @param wrapped the wrapped
     */
    public ShapeFaceletCacheFactory(FaceletCacheFactory wrapped)
    {
        super(wrapped);
    }

    @Override
    public ShapeFaceletCache getFaceletCache()
    {
        String param = FacesContext.getCurrentInstance()
            .getExternalContext()
            .getInitParameter(ViewHandler.FACELETS_REFRESH_PERIOD_PARAM_NAME);

        long period;

        try
        {
            period = Long.parseLong(param);
        }
        catch(Exception e)
        {
            period = 2;
        }

        period *= 1000;

        if(period < 0)
        {
            return new UnlimitedFaceletCache();
        }

        if(period == 0)
        {
            return new DevelopmentFaceletCache();
        }

        return new ExpiringFaceletCache(period);
    }
}
