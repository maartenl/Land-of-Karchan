package mmud.scripting;

import org.graalvm.polyglot.Context;
import org.graalvm.polyglot.Value;
import org.testng.annotations.Test;

public class RunScriptTest
{

  static String JS_CODE = "(function myFun(param){console.log('Hello ' + param + ' from JS');})";

  @Test
  public void testJavascript()
  {
    String who = "mrBear";
    try (Context context = Context.create())
    {
      Value value = context.eval("js", JS_CODE);
      value.execute(who);
    }
  }
}
