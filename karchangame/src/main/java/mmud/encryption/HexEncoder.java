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
package mmud.encryption;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Encoder of binary data into a hex representation.
 *
 * @author maartenl
 */
public class HexEncoder
{

    private static final Logger LOGGER = Logger.getLogger(HexEncoder.class.getName());

    public static final int PADDING_DEFAULT = 32;

    private static final String ZEROES8 = "00000000";

    private final int padding;

    /**
     * Constructs encoder with default padding.
     */
    public HexEncoder()
    {
        padding = PADDING_DEFAULT;
    }

    /**
     * Constructs encoder with padding length. .
     *
     * @param aPadding Padding to use.
     */
    public HexEncoder(int aPadding)
    {
        padding = aPadding;
    }

    public String encode(byte[] aData)
    {
        String res = new BigInteger(1, aData).toString(16);
        if (res.length() > padding)
        {
            LOGGER.log(Level.WARNING, "Number (length is {0}) larger than {1} characters in hex", new Object[]
            {
                res.length(), padding
            });
            return res;
        }
        StringBuilder padstr = new StringBuilder();
        int numpad = padding - res.length();
        while (numpad > 0)
        {
            if (numpad >= 8)
            {
                padstr.append(ZEROES8);
                numpad -= 8;
            } else
            {
                padstr.append("0");
                numpad--;
            }
        }
        return padstr.toString() + res;
    }

    /**
     * Encrypts the password using the given encoding.
     *
     * @param aPassword
     * Password.
     * @param aEncoding
     * Encoding to use.
     * @return Encoded password.
     */
    public String encrypt(String aPassword, Hash aEncoding)
    {
        try
        {
            MessageDigest md = MessageDigest.getInstance(aEncoding.getHash());
            byte[] digested = md.digest(aPassword.getBytes());
            return encode(digested);
        } catch (NoSuchAlgorithmException e)
        {
            throw new RuntimeException("Could not get encoding '" + aEncoding
                    + "' for encoding password", e);
        }
    }

    public int getPadding()
    {
        return padding;
    }
}
