package org.karchan.security;

import java.nio.charset.StandardCharsets;

import io.jsonwebtoken.io.DeserializationException;
import io.jsonwebtoken.io.Deserializer;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbException;
import static java.util.Objects.requireNonNull;

/**
 * @since JJWT_RELEASE_VERSION
 */
public class JsonbDeserializer<T> implements Deserializer<T>
{

  private final Class<T> returnType;
  private final Jsonb jsonb;

  @SuppressWarnings("unused") //used via reflection by RuntimeClasspathDeserializerLocator
  public JsonbDeserializer()
  {
    this(JsonbSerializer.DEFAULT_JSONB);
  }

  @SuppressWarnings({"unchecked", "WeakerAccess", "unused"}) // for end-users providing a custom ObjectMapper
  public JsonbDeserializer(Jsonb jsonb)
  {
    this(jsonb, (Class<T>) Object.class);
  }

  private JsonbDeserializer(Jsonb jsonb, Class<T> returnType)
  {
    requireNonNull(jsonb, "ObjectMapper cannot be null.");
    requireNonNull(returnType, "Return type cannot be null.");
    this.jsonb = jsonb;
    this.returnType = returnType;
  }

  @Override
  public T deserialize(byte[] bytes) throws DeserializationException
  {
    try
    {
      return readValue(bytes);
    } catch (JsonbException jsonbException)
    {
      String msg = "Unable to deserialize bytes into a " + returnType.getName() + " instance: " + jsonbException.getMessage();
      throw new DeserializationException(msg, jsonbException);
    }
  }

  protected T readValue(byte[] bytes)
  {
    return jsonb.fromJson(new String(bytes, StandardCharsets.UTF_8), returnType);
  }

}
