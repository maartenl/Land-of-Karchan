package mmud;

import org.owasp.validator.html.PolicyException;
import org.owasp.validator.html.ScanException;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class UtilsTest
{

  @Test
  public void testSecurity() throws ScanException, PolicyException
  {
    String security = Utils.security("Try this!");
    assertEquals(security, "Try this!");
  }

  @Test
  public void testSecurityWithIncompleteHtml() throws ScanException, PolicyException
  {
    String security = Utils.security("<p>Try this!");
    assertEquals(security, "<p>Try this!</p>");
  }

  @Test
  public void testSecurityWithJavascript() throws ScanException, PolicyException
  {
    String security = Utils.security("<javascript>alert('Hmm');</javascript>Try this!");
    assertEquals(security, "alert('Hmm');Try this!");
  }
}
