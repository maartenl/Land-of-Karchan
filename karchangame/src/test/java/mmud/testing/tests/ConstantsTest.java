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

import mmud.Constants;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author maartenl
 */
public class ConstantsTest
{

  public ConstantsTest()
  {
  }

  @DataProvider(name = "regExpDataProvider")
  public Object[][] createData1()
  {
    return new Object[][]
    {
      // regular expression, matches this, yesno
      {
        Constants.NAME_REGEXP, "to", false // too short
      },
      {
        Constants.ONLY_LETTERS_REGEXP, "many", true
      },
      {
        Constants.ONLY_LETTERS_REGEXP, "Many", true
      },
      {
        Constants.ONLY_LETTERS_REGEXP, "many fowled", false // no spaces allowed
      },
      {
        Constants.ONLY_LETTERS_REGEXP, "many-fowled", true
      },
      {
        Constants.ONLY_LETTERS_REGEXP, "<b>many</b>", false // no html allowed
      },
      {
        Constants.ONLY_LETTERS_REGEXP, "", true
      },
      {
        Constants.ONLY_LETTERS_ONE_OR_MORE_REGEXP, "", false // at least one char required.
      },
      {
        Constants.URL_REGEXP, "", false // must start with a /
      },
      {
        Constants.URL_REGEXP, "/", false // must start with a / and at least some chars.
      },
      {
        Constants.URL_REGEXP, "/wo-ah.jpg", true
      },
      {
        Constants.URL_REGEXP, "/woash.jpg", true
      },
      {
        Constants.URL_REGEXP, "/woah.jpg", true
      },
      {
        Constants.URL_REGEXP, "/woahs", false // no file extention
      },
      {
        Constants.URL_REGEXP, "/woahs.jpeg", false // no proper file extention
      },
      {
        Constants.URL_REGEXP, "/woahs.gif", true
      },
      {
        Constants.URL_REGEXP, "/woahs/woahs/woahs.gif", true
      },
      {
        Constants.URL_REGEXP, "/WOAHS/woahs/woahs.BMP", true // capitals are okay.
      },
      {
        Constants.URL_REGEXP, "/area51/lake/lake01.jpg", true
      },
      {
        Constants.URL_REGEXP, "/woahs/woahs.gif/wosha.gif", false // no . in the center
      },
      {
        Constants.URL_REGEXP, "/woahs/wo ahs.gif/wosha.gif", false // no spaces allowed anywhere
      }
    };
  }

  @Test(dataProvider = "regExpDataProvider")
  public void regExpTest(String regexp, String matched, Boolean positive)
  {
    assertThat(Constants.regExpTest(regexp, matched)).isEqualTo(positive);
  }

  @Test
  public void getBasePath()
  {
    assertThat(Constants.getMudfilepath()).isEqualTo("/home/maartenl/temp");
  }
}
