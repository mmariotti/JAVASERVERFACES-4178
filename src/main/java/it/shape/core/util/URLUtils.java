package it.shape.core.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.Authenticator;
import java.net.JarURLConnection;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;



/**
 * The Class URLUtils.
 *
 * @author Michele Mariotti
 */
public class URLUtils
{
    /** The Constant PATTERN. */
    private static final Pattern PATTERN = Pattern.compile(
        "<a[^>]*href=\"([^\"]*)\"[^>]*>(?:<[^>]+>)*?([^<>]+?)(?:<[^>]+>)*?</a>",
        Pattern.CASE_INSENSITIVE);

    /**
     * The Class PasswordAuthenticator.
     *
     * @author Michele Mariotti
     */
    public static class PasswordAuthenticator extends Authenticator
    {

        /** The authentication. */
        protected final PasswordAuthentication authentication;

        /**
         * Instantiates a new password authenticator.
         *
         * @param authentication the authentication
         */
        public PasswordAuthenticator(PasswordAuthentication authentication)
        {
            this.authentication = authentication;
        }

        /**
         * Instantiates a new password authenticator.
         *
         * @param username the username
         * @param password the password
         */
        public PasswordAuthenticator(String username, char[] password)
        {
            this(new PasswordAuthentication(username, password));
        }

        /**
         * Instantiates a new password authenticator.
         *
         * @param username the username
         * @param password the password
         */
        public PasswordAuthenticator(String username, String password)
        {
            this(username, password.toCharArray());
        }

        @Override
        protected PasswordAuthentication getPasswordAuthentication()
        {
            return authentication;
        }
    }

    /**
     * Sets the default authenticator.
     *
     * @param username the username
     * @param password the password
     */
    public static void setDefaultAuthenticator(String username, String password)
    {
        Authenticator.setDefault(new PasswordAuthenticator(username, password));
    }

    /**
     * Gets the content.
     *
     * @param url the url
     * @return the content
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static String getContent(URL url) throws IOException
    {
        URLConnection connection = url.openConnection();
        String encoding = connection.getContentEncoding();

        try(InputStream input = connection.getInputStream();
            Scanner s = new Scanner(input, encoding);)
        {
            s.useDelimiter("\\A");
            return s.hasNext() ? s.next() : "";
        }
    }

    /**
     * Returns a list of sub urls of the given url. The returned list is a list of URL.
     *
     * @param url The base URL from which to retrieve the listing.
     * @return a list of sub urls of the given url.
     * @throws IOException If an error occures retrieving the HTML.
     */
    public static List<URL> list(URL url) throws IOException
    {
        if(!url.toString().endsWith("/"))
        {
            return list(new URL(url.toString() + "/"));
        }

        List<URL> urlList = new ArrayList<>();

        String content = getContent(url);

        Matcher matcher = PATTERN.matcher(content);

        while(matcher.find())
        {
            // get the href text and the displayed text
            String href = matcher.group(1);
            String text = matcher.group(2);

            if(href == null || text == null)
            {
                // the groups were not found (shouldn't happen, really)
                continue;
            }

            text = text.trim();

            // handle complete URL listings
            if(href.startsWith("http:") || href.startsWith("https:"))
            {
                try
                {
                    href = new URL(href).getPath();
                    if(!href.startsWith(url.getPath()))
                    {
                        // ignore URLs which aren't children of the base URL
                        continue;
                    }
                    href = href.substring(url.getPath().length());
                }
                catch(Exception ignore)
                {
                    // incorrect URL, ignore
                    continue;
                }
            }

            if(href.startsWith("../"))
            {
                // we are only interested in sub-URLs, not parent URLs, so skip this one
                continue;
            }

            // absolute href: convert to relative one
            if(href.startsWith("/"))
            {
                int slashIndex = href.substring(0, href.length() - 1).lastIndexOf('/');
                href = href.substring(slashIndex + 1);
            }

            // relative to current href: convert to simple relative one
            if(href.startsWith("./"))
            {
                href = href.substring("./".length());
            }

            // exclude those where they do not match
            // href will never be truncated, text may be truncated by apache
            if(text.endsWith("..>"))
            {
                // text is probably truncated, we can only check if the href starts with text
                if(!href.startsWith(text.substring(0, text.length() - 3)))
                {
                    continue;
                }
            }
            else if(text.endsWith("..&gt;"))
            {
                // text is probably truncated, we can only check if the href starts with text
                if(!href.startsWith(text.substring(0, text.length() - 6)))
                {
                    continue;
                }
            }
            else
            {
                // text is not truncated, so it must match the url after stripping optional
                // trailing slashes
                String strippedHref = href.endsWith("/") ? href.substring(0, href.length() - 1) : href;
                String strippedText = text.endsWith("/") ? text.substring(0, text.length() - 1) : text;
                if(!strippedHref.equalsIgnoreCase(strippedText))
                {
                    continue;
                }
            }

            URL child = new URL(url, href);
            urlList.add(child);
        }

        return urlList;
    }

    /**
     * Gets the last modified.
     *
     * @param url the url
     * @return the last modified
     * @throws IOException Signals that an I/O exception has occurred.
     */
    public static long getLastModified(URL url) throws IOException
    {
        URLConnection urlConnection = url.openConnection();

        if(urlConnection instanceof JarURLConnection)
        {
            JarURLConnection jarUrlConnection = (JarURLConnection) urlConnection;
            URL jarFileUrl = jarUrlConnection.getJarFileURL();

            return getLastModified(jarFileUrl);
        }

        try(InputStream input = urlConnection.getInputStream())
        {
            return urlConnection.getLastModified();
        }
    }
}
