package mmud.constants;

import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

public class ColoursTest
{
  @Test
  public void testColoursAsCss()
  {
    String coloursAsCss = Colours.getColoursAsCss();
    assertThat(coloursAsCss).startsWith("""
      .chat-airred { color: #fd5c63;}
      .chat-ajax { color: #d2122e;}
      .chat-amerrose { color: #ff033e;}
      """);
  }

  @Test
  public void testColoursAsHtml() {
    String coloursAsInputfield = Colours.getColoursAsInputfield();
    assertThat(coloursAsInputfield).startsWith("""
      <select class="form-control" id="colour" name="colour" formControlName="colour">
      <option selected="selected" value="black">black</option>
      <option value="airred">airred</option>
      <option value="ajax">ajax</option>
      <option value="amerrose">amerrose</option>
      """);
  }
}
