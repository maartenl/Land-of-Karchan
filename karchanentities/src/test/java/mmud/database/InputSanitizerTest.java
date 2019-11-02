package mmud.database;

import mmud.database.InputSanitizer;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class InputSanitizerTest
{

  @Test
  public void testSecurity()
  {
    String security = InputSanitizer.security("Try this!");
    assertEquals(security, "Try this!");
  }

  @Test
  public void testSecurityWithIncompleteHtml()
  {
    String security = InputSanitizer.security("<p>Try this!");
    assertEquals(security, "<p>Try this!</p>");
  }

  @Test
  public void testSecurityWithJavascript()
  {
    String security = InputSanitizer.security("<javascript>alert('Hmm');</javascript>Try this!");
    assertEquals(security, "alert(&#39;Hmm&#39;);Try this!");
  }
}
