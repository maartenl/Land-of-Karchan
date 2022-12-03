package mmud.rest.services;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import mmud.database.entities.characters.User;
import mmud.database.entities.web.CharacterInfo;
import mmud.database.entities.web.Family;
import mmud.rest.webentities.PrivatePerson;
import mmud.rest.webentities.PublicFamily;
import mmud.rest.webentities.PublicPerson;

/**
 * Primarily used for converting from entities to rest objects.
 */
public class RestUtilities
{

  private RestUtilities()
  {
    // defeat instantiation
  }

  private static final Logger LOGGER = Logger.getLogger(RestUtilities.class.getName());

  public static PublicPerson getPublicPerson(User person, List<Family> list, CharacterInfo characterInfo)
  {
    PublicPerson res = new PublicPerson();
    res.name = person.getName();
    res.familyname = person.getFamilyname();
    res.title = person.getTitle();
    res.sex = person.getSex().toString();
    res.description = person.getDescription();
    if (characterInfo != null)
    {
      res.imageurl = characterInfo.getImageurl();
      res.homepageurl = characterInfo.getHomepageurl();
      res.dateofbirth = characterInfo.getDateofbirth();
      res.cityofbirth = characterInfo.getCityofbirth();
      res.storyline = characterInfo.getStoryline();
    }
    if (person.getGuild() != null)
    {
      res.guild = person.getGuild().getTitle();
    }
    for (Family fam : list)
    {
      LOGGER.log(Level.FINER, "{0}", fam);
      PublicFamily pfam = new PublicFamily();
      pfam.description = fam.getDescription().getDescription();
      pfam.toname = fam.getFamilyPK().getToname();
      res.familyvalues.add(pfam);
    }
    return res;
  }

  public static PrivatePerson getPrivatePerson(User person, List<Family> list, CharacterInfo characterInfo)
  {
    PrivatePerson res = new PrivatePerson();
    res.name = person.getName();
    res.familyname = person.getFamilyname();
    res.title = person.getTitle();
    res.sex = person.getSex().toString();
    res.description = person.getDescription();
    res.websockets = person.getWebsocketSupport();
    if (characterInfo != null)
    {
      res.imageurl = characterInfo.getImageurl();
      res.homepageurl = characterInfo.getHomepageurl();
      res.dateofbirth = characterInfo.getDateofbirth();
      res.cityofbirth = characterInfo.getCityofbirth();
      res.storyline = characterInfo.getStoryline();
    }
    if (person.getGuild() != null)
    {
      res.guild = person.getGuild().getTitle();
    }
    for (Family fam : list)
    {
      LOGGER.log(Level.FINER, "{0}", fam);
      PublicFamily pfam = new PublicFamily();
      pfam.description = fam.getDescription().getDescription();
      pfam.toname = fam.getFamilyPK().getToname();
      res.familyvalues.add(pfam);
    }
    return res;
  }

}
