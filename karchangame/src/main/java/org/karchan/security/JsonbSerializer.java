package org.karchan.security;

import java.nio.charset.StandardCharsets;

import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.io.SerializationException;
import io.jsonwebtoken.io.Serializer;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbException;
import static java.util.Objects.requireNonNull;

/**
 * @since JJWT_RELEASE_VERSION
 */
public class JsonbSerializer<T> implements Serializer<T>
{

  static final Jsonb DEFAULT_JSONB = JsonbBuilder.create();

  private final Jsonb jsonb;

  @SuppressWarnings("unused") //used via reflection by RuntimeClasspathDeserializerLocator
  public JsonbSerializer()
  {
    this(DEFAULT_JSONB);
  }

  @SuppressWarnings("WeakerAccess") //intended for end-users to use when providing a custom ObjectMapper
  public JsonbSerializer(Jsonb jsonb)
  {
    requireNonNull(jsonb, "Jsonb cannot be null.");
    this.jsonb = jsonb;
  }

  @Override
  public byte[] serialize(T t) throws SerializationException
  {
    requireNonNull(t, "Object to serialize cannot be null.");
    try
    {
      return writeValueAsBytes(t);
    } catch (JsonbException jsonbException)
    {
      String msg = "Unable to serialize object: " + jsonbException.getMessage();
      throw new SerializationException(msg, jsonbException);
    }
  }

  @SuppressWarnings("WeakerAccess") //for testing
  protected byte[] writeValueAsBytes(T t)
  {
    final Object obj;

    if (t instanceof byte[])
    {
      obj = Encoders.BASE64.encode((byte[]) t);
    } else if (t instanceof char[])
    {
      obj = new String((char[]) t);
    } else
    {
      obj = t;
    }

    return this.jsonb.toJson(obj).getBytes(StandardCharsets.UTF_8);
  }
}
