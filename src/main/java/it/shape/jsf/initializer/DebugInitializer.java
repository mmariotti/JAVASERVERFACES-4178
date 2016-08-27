package it.shape.jsf.initializer;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Map.Entry;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;


@WebListener
public class DebugInitializer implements ServletContextListener
{
    @Override
    public void contextInitialized(ServletContextEvent sce)
    {
        try
        {
            ServletContext context = sce.getServletContext();

            Field handlersField = URL.class.getDeclaredField("handlers");
            handlersField.setAccessible(true);
            Hashtable<String, URLStreamHandler> handlers = (Hashtable<String, URLStreamHandler>) handlersField.get(null);

            for(Entry<String, URLStreamHandler> entry : new HashSet<>(handlers.entrySet()))
            {
                handlers.put(entry.getKey(), new DebugURLStreamHandler(context, entry.getValue()));
            }
        }
        catch(Exception e)
        {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce)
    {
        return;
    }

    public static class DebugURLStreamHandler extends URLStreamHandler
    {
        protected ServletContext context;

        protected URLStreamHandler wrapped;

        protected Method method;

        public DebugURLStreamHandler(ServletContext context, URLStreamHandler wrapped)
        {
            super();
            this.context = context;
            this.wrapped = wrapped;

            try
            {
                method = wrapped.getClass().getDeclaredMethod("openConnection", URL.class);
                method.setAccessible(true);
            }
            catch(Exception e)
            {
                throw new RuntimeException(e.getMessage(), e);
            }
        }

        @Override
        protected URLConnection openConnection(URL u) throws IOException
        {
            try
            {
                URLConnection result = (URLConnection) method.invoke(wrapped, u);
                if(u.toString().endsWith("/comp.xhtml"))
                {
                    context.log("---------------------------------> opening connection!! [" + u + "]");
                }

                return result;
            }
            catch(IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
            {
                throw new IOException(e.getMessage(), e);
            }
        }
    }
}
