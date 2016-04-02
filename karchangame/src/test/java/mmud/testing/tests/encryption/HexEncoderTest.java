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
package mmud.testing.tests.encryption;

import mmud.encryption.Hash;
import mmud.encryption.HexEncoder;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 *
 * @author maartenl
 */
public class HexEncoderTest
{

    private static final String original = "The quick brown fox jumps over the lazy dog.";

    public HexEncoderTest()
    {
    }

    @Test
    public void sha1Short()
    {
        HexEncoder encoder = new HexEncoder(40);
        assertThat(encoder.encrypt(original, Hash.SHA_1), equalTo("408d94384216f890ff7a0c3528e8bed1e0b01621"));
    }

    @Test
    public void sha1()
    {
        HexEncoder encoder = new HexEncoder(128);
        assertThat(encoder.encrypt(original, Hash.SHA_1), equalTo("0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000408d94384216f890ff7a0c3528e8bed1e0b01621"));
    }

    @Test
    public void md2()
    {
        HexEncoder encoder = new HexEncoder(128);
        assertThat(encoder.encrypt(original, Hash.MD_2), equalTo("00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000071eaa7e440b611e41a6f0d97384b342a"));
    }

    @Test
    public void md5()
    {
        HexEncoder encoder = new HexEncoder(128);
        assertThat(encoder.encrypt(original, Hash.MD_5), equalTo("000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000e4d909c290d0fb1ca068ffaddf22cbd0"));
    }

    @Test
    public void sha256()
    {
        HexEncoder encoder = new HexEncoder(128);
        assertThat(encoder.encrypt(original, Hash.SHA_256), equalTo("0000000000000000000000000000000000000000000000000000000000000000ef537f25c895bfa782526529a9b63d97aa631564d5d789c2b765448c8635fb6c"));
    }

    @Test
    public void sha384()
    {
        HexEncoder encoder = new HexEncoder(128);
        assertThat(encoder.encrypt(original, Hash.SHA_384), equalTo("00000000000000000000000000000000ed892481d8272ca6df370bf706e4d7bc1b5739fa2177aae6c50e946678718fc67a7af2819a021c2fc34e91bdb63409d7"));
    }

    @Test
    public void sha512()
    {
        HexEncoder encoder = new HexEncoder(128);
        assertThat(encoder.encrypt(original, Hash.SHA_512), equalTo("91ea1245f20d46ae9a037a989f54f1f790f0a47607eeb8a14d12890cea77a1bbc6c7ed9cf205e67b7f2b8fd4c7dfd3a7a8617e45f3c463d481c7e586c39ac1ed"));
    }

    @BeforeClass
    public static void setUpClass() throws Exception
    {
    }

    @AfterClass
    public static void tearDownClass() throws Exception
    {
    }

    @BeforeMethod
    public void setUpMethod() throws Exception
    {
    }

    @AfterMethod
    public void tearDownMethod() throws Exception
    {
    }
}
