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

/**
 *
 * @author maartenl
 */
public enum Hash
{

    SHA_1("SHA-1"), MD_2("MD2"), MD_5("MD5"), SHA_256("SHA-256"), SHA_384(
            "SHA-384"), SHA_512("SHA-512");
    private final String hash;

    Hash(String hash)
    {
        this.hash = hash;
    }

    public String getHash()
    {
        return hash;
    }

}
