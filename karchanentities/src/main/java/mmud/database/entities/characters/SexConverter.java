package mmud.database.entities.characters;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import mmud.database.enums.Sex;

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
