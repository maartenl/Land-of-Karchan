package mmud;

import javax.json.bind.JsonbBuilder;

import mmud.exceptions.JsonException;

public class JsonUtils
{

  private JsonUtils()
  {
    // Adding a private constructor to hide the implicit public one.
  }

  public static <T> T fromJson(String json, Class<T> clazz)
  {
    try (var jsonb = JsonbBuilder.create())
    {
      return jsonb.fromJson(json, clazz);
    } catch (Exception e)
    {
      throw new JsonException(e);
    }
  }

  public static String toJson(Object object)
  {
    try (var jsonb = JsonbBuilder.create())
    {
      return jsonb.toJson(object);
    } catch (Exception e)
    {
      throw new JsonException(e);
    }
  }
}
