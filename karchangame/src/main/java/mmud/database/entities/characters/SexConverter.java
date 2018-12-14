package mmud.database.entities.characters;

import mmud.database.enums.Sex;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

@Converter(autoApply = true)
public class SexConverter implements AttributeConverter<Sex, String>
{

  @Override
  public String convertToDatabaseColumn(Sex vehicle) {
    return vehicle.toString();
  }

  @Override
  public Sex convertToEntityAttribute(String shortName) {
    return Sex.createFromString(shortName);
  }
}
