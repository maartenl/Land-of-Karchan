/*
 *  Copyright (C) 2012 maartenl
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
package mmud.testing.tests.database.entities;

import mmud.database.entities.web.CharacterInfo;
import mmud.exceptions.MudException;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.Assert;
import static org.testng.Assert.*;

/**
 *
 * @author maartenl
 */
public class CharacterInfoTest
{

    public CharacterInfoTest()
    {
    }

    @BeforeClass
    public void setUpClass()
    {
    }

    @AfterClass
    public void tearDownClass()
    {
    }

    @BeforeMethod
    public void setUp()
    {
    }

    @AfterMethod
    public void tearDown()
    {
    }

    @Test
    public void checkUrlValidationTest() throws MudException
    {
        final CharacterInfo charinfo = new CharacterInfo();
        charinfo.setImageurl("http://www.images.com/imageurl.jpg");
        charinfo.setHomepageurl("http://www.homepage.com/");
        charinfo.setDateofbirth("0000");
        charinfo.setCityofbirth("Sirius");
        charinfo.setStoryline("An android");
        charinfo.setName("Marvin");
    }

    @Test
    public void checkImageurlValidationTest() throws MudException
    {
        final CharacterInfo charinfo = new CharacterInfo();
        try
        {
            charinfo.setImageurl("imageurl.jpg");
            fail("MudException expected");
        } catch (MudException e)
        {
            assertEquals(e.getMessage(), "imageurl 'imageurl.jpg' invalid");
        }
        charinfo.setHomepageurl("http://www.homepage.com/");
        charinfo.setDateofbirth("0000");
        charinfo.setCityofbirth("Sirius");
        charinfo.setStoryline("An android");
        charinfo.setName("Marvin");
    }

    @Test
    public void checkHomepageurlValidationTest() throws MudException
    {
        final CharacterInfo charinfo = new CharacterInfo();
        charinfo.setImageurl("http://www.images.com/imageurl.jpg");
        try
        {
            charinfo.setHomepageurl("cowabunga");
            fail("MudException expected");
        } catch (MudException e)
        {
            assertEquals(e.getMessage(), "homepageurl 'cowabunga' invalid");
        }
        charinfo.setDateofbirth("0000");
        charinfo.setCityofbirth("Sirius");
        charinfo.setStoryline("An android");
        charinfo.setName("Marvin");
    }

    @Test
    public void checkImageurlAndHomepageurlValidationTest() throws MudException
    {
        final CharacterInfo charinfo = new CharacterInfo();
        try
        {
            charinfo.setImageurl("imageurl.jpg");
            fail("MudException expected");
        } catch (MudException e)
        {
            assertEquals(e.getMessage(), "imageurl 'imageurl.jpg' invalid");
        }
        try
        {
            charinfo.setHomepageurl("homepage");
            fail("MudException expected");
        } catch (MudException e)
        {
            assertEquals(e.getMessage(), "homepageurl 'homepage' invalid");
        }
        charinfo.setDateofbirth("0000");
        charinfo.setCityofbirth("Sirius");
        charinfo.setStoryline("An android");
        charinfo.setName("Marvin");
    }
}
