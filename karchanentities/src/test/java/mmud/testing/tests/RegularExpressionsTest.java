/*
 * Copyright (C) 2016 maartenl
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package mmud.testing.tests;

import mmud.database.RegularExpressions;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author maartenl
 */
public class RegularExpressionsTest
{

  public RegularExpressionsTest()
  {
  }

  @DataProvider(name = "regExpDataProvider")
  public Object[][] createData1()
  {
    return new Object[][]
    {
      // regular expression, matches this, yesno
      {
        RegularExpressions.NAME_REGEXP, "to", false // too short
      },
      {
        RegularExpressions.ONLY_LETTERS_REGEXP, "many", true
      },
      {
        RegularExpressions.ONLY_LETTERS_REGEXP, "Many", true
      },
      {
        RegularExpressions.ONLY_LETTERS_REGEXP, "many fowled", true // spaces allowed
      },
      {
        RegularExpressions.ONLY_LETTERS_REGEXP, "many-fowled", true // dashes allrighty too
      },
      {
        RegularExpressions.ONLY_LETTERS_REGEXP, "Philosopher's stone", true // apostrophes are fine
      },
      {
        RegularExpressions.ONLY_LETTERS_REGEXP, "old, faded, yellow", true // , are also fine
      },
      {
        RegularExpressions.ONLY_LETTERS_REGEXP, "<b>many</b>", false // no html allowed
      },
      {
        RegularExpressions.ONLY_LETTERS_REGEXP, "", true
      },
      {
        RegularExpressions.ONLY_LETTERS_ONE_OR_MORE_REGEXP, "", false // at least one char required.
      },
      {
        RegularExpressions.URL_REGEXP, "", false // must start with a /
      },
      {
        RegularExpressions.URL_REGEXP, "/", false // must start with a / and at least some chars.
      },
      {
        RegularExpressions.URL_REGEXP, "/wo-ah.jpg", true
      },
      {
        RegularExpressions.URL_REGEXP, "/woash.jpg", true
      },
      {
        RegularExpressions.URL_REGEXP, "/woah.jpg", true
      },
      {
        RegularExpressions.URL_REGEXP, "/woahs", false // no file extention
      },
      {
        RegularExpressions.URL_REGEXP, "/woahs.jpeg", false // no proper file extention
      },
      {
        RegularExpressions.URL_REGEXP, "/woahs.gif", true
      },
      {
        RegularExpressions.URL_REGEXP, "/woahs/woahs/woahs.gif", true
      },
      {
        RegularExpressions.URL_REGEXP, "/WOAHS/woahs/woahs.BMP", true // capitals are okay.
      },
      {
        RegularExpressions.URL_REGEXP, "/area51/lake/lake01.jpg", true
      },
      {
        RegularExpressions.URL_REGEXP, "/woahs/woahs.gif/wosha.gif", false // no . in the center
      },
      {
        RegularExpressions.URL_REGEXP, "/woahs/wo ahs.gif/wosha.gif", false // no spaces allowed anywhere
      }
    };
  }

  @Test(dataProvider = "regExpDataProvider")
  public void regExpTest(String regexp, String matched, Boolean positive)
  {
    assertThat(RegularExpressions.regExpTest(regexp, matched)).isEqualTo(positive);
  }

}
