package mmud.database.entities.web;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

public class UrlValidator
{
  private UrlValidator()
  {
    // created private constructor to hide the public one.
  }

  public static boolean isValid(String url)
  {
    try
    {
      new URL(url).toURI();
      return true;
    } catch (MalformedURLException | URISyntaxException e)
    {
      return false;
    }
  }
}
