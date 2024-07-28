package mmud.database.entities.characters;

import jakarta.annotation.Nonnull;

public record Transform(@Nonnull Position position, @Nonnull Rotation rotation, @Nonnull Scale scale)
{
}
