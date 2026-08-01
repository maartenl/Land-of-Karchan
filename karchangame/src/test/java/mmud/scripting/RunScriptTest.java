package mmud.scripting;

import mmud.scripting.entities.Person;
import mmud.testing.TestingConstants;
import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.HostAccess;
import org.graalvm.polyglot.Value;
import org.testng.annotations.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Simple test to verify that JavaScript is running using GraalJS on GraalVM.
 */
public class RunScriptTest
{

  private static final String JS_CODE = "(function myFun(param){console.log('Hello ' + param + ' from JS');})";

  private static final String JS_CODERETURN = "(function myFun(param){return 'Hello ' + param + ' from JS';})";

  private static final String JS_CODERETURNA = "(function myFun(param){return 'Hello ' + a + ' from JS';})";

  private static final String JS_CODERETURNSTRING = """
    function myFun(param) {
        return 'Hello ' + marvin.getName() + ':' + marvin + ':' + param + ' from JS';
    };
    """;

  private static final String JS_CODERETURNMAP = """
    function myFun(param) {
        return {
           title: "Main title",
           image: "No image",
           body: "The body"
       };
    };
    """;

  private static final String JS_CODERETURNOBJECT = """
    function myFun(param) {
        return {
           getTitle: function() { return "Main title"; },
           getImage: function() { return "No image"; },
           getBody: function() { return "The body"; }
       };
    };
    """;

  @Test
  public void testJavascript()
  {
    String who = "mrBear";
    try (Context context = createContext())
    {
      // value in this case is a JavaScript function
      Value value = context.eval("js", JS_CODE);
      value.executeVoid(who);
    }
  }

  @Test
  public void testJavascriptWithReturn()
  {
    String who = "mrBear";
    try (Context context = createContext())
    {
      // value in this case is a JavaScript function
      Value value = context.eval("js", JS_CODERETURN);
      // returnValue should be the result of the execution of the JavaScript function.
      Value returnValue = value.execute(who);
      assertThat(returnValue.asString()).isEqualTo("Hello mrBear from JS");
    }
  }

  @Test
  public void testJavascriptWithState()
  {
    String who = "mrBear";
    try (Context context = createContext())
    {
      context.getBindings("js").putMember("a", 10);
      // value in this case is a JavaScript function
      Value value = context.eval("js", JS_CODERETURNA);
      // returnValue should be the result of the execution of the JavaScript function.
      Value returnValue = value.execute(who);
      assertThat(returnValue.asString()).isEqualTo("Hello 10 from JS");
    }
  }

  @Test
  public void testJavascriptWithObjectState()
  {
    String who = "mrBear";
    var marvin = new Person(TestingConstants.getMarvin(TestingConstants.getRoom(TestingConstants.getArea())));
    try (Context context = createContext())
    {
      context.getBindings("js").putMember("marvin", marvin);
      // value in this case is a JavaScript function
      context.eval("js", JS_CODERETURNSTRING);
      Value value = context.eval("js", "myFun");

      // returnValue should be the result of the execution of the JavaScript function.
      Value returnValue = value.execute(who);
      assertThat(returnValue.asString()).startsWith("Hello Marvin:mmud.scripting.entities.Person").endsWith("mrBear from JS");
    }
  }

  @Test
  public void testJavascriptReturnsMap()
  {
    try (Context context = createContext())
    {
      // value in this case is a JavaScript function
      context.eval("js", JS_CODERETURNMAP);
      Value value = context.eval("js", "myFun");

      // returnValue should be the result of the execution of the JavaScript function.
      Value returnValue = value.execute();
      assertThat(returnValue.getMember("title").asString()).isEqualTo("Main title");
      assertThat(returnValue.getMember("image").asString()).isEqualTo("No image");
      assertThat(returnValue.getMember("body").asString()).isEqualTo("The body");
    }
  }

  @Test
  public void testJavascriptReturnsObject()
  {
    try (Context context = createContext())
    {
      // value in this case is a JavaScript function
      context.eval("js", JS_CODERETURNOBJECT);
      Value value = context.eval("js", "myFun");

      // returnValue should be the result of the execution of the JavaScript function.
      Value returnValue = value.execute();
      assertThat(returnValue.getMember("getTitle").execute().asString()).isEqualTo("Main title");
      assertThat(returnValue.getMember("getImage").execute().asString()).isEqualTo("No image");
      assertThat(returnValue.getMember("getBody").execute().asString()).isEqualTo("The body");
    }
  }

  private Context createContext()
  {
    return Context.newBuilder("js")
      .allowHostAccess(HostAccess.ALL)
      //allows access to all Java classes
      .allowHostClassLookup(className -> true)
      .build();
  }

}
