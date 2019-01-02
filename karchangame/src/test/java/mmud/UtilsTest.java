package mmud;

import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class UtilsTest
{

  @Test
  public void testSecurity()
  {
    String security = Utils.security("Try this!");
    assertEquals(security, "Try this!");
  }

  @Test
  public void testSecurityWithIncompleteHtml()
  {
    String security = Utils.security("<p>Try this!");
    assertEquals(security, "<p>Try this!</p>");
  }

  @Test
  public void testSecurityWithJavascript()
  {
    String security = Utils.security("<javascript>alert('Hmm');</javascript>Try this!");
    assertEquals(security, "alert(&#39;Hmm&#39;);Try this!");
  }
}
