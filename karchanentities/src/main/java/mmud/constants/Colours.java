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
package mmud.constants;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Colours
{

  private static final List<Colour> COLOURS;

  static
  {
    COLOURS = Stream.of(new Colour("airred", "#fd5c63"), new Colour("ajax", "#d2122e"), new Colour("amerrose",
        "#ff033e"), new Colour("asparagus", "#87a96b"), new Colour("auburn", "#a52a2a"), new Colour("avocado",
        "#568203"), new Colour("byzantine", "#3457d5"), new Colour("carlsberg", "#17b169"), new Colour("carmine",
        "#ff0038"), new Colour("cerise", "#de3163"), new Colour("chili", "#e23d28"), new Colour("cinnabar",
        "#e44d2e"), new Colour("darkpastelgreen", "#03c03c"), new Colour("gamboge", "#e49b0f"), new Colour(
        "glaucous", "#6082b6"), new Colour("iris", "#5a4fcf"), new Colour("jungle", "#29ab87"), new Colour(
        "kelly", "#4cbb17"), new Colour("madrid", "#febe10"), new Colour("myrtle", "#317873"), new Colour(
        "neonorange", "#ff5f1f"), new Colour("pistachio", "#93c572"), new Colour("ruby", "#e0115f"),
      new Colour("russet", "#80461b"), new Colour("sanguine", "#bc3f4a"), new Colour("secret", "#f9629f"),
      new Colour("tearose", "#f88379"), new Colour("watermelon", "#e37383"), new Colour("black", "#000000"),
      new Colour("chocolate", "#D2691E"), new Colour("cornflowerblue", "#6495ED"), new Colour("red", "#FF0000"),
      new Colour("darkcyan", "#008B8B"), new Colour("darkgoldenrod", "#B8860B"),
      new Colour("darkorange", "#FF8C00"), new Colour("darkorchid", "#9932CC"), new Colour("deeppink", "#FF1493"),
      new Colour("deepskyblue", "#00BFFF"), new Colour("dodgerblue", "#1E90FF"), new Colour("forestgreen",
        "#228B22"), new Colour("fuchsia", "#FF00FF"), new Colour("gold", "#FFD700"), new Colour("goldenrod",
        "#DAA520"), new Colour("hotpink", "#FF69B4"), new Colour("indianred", "#CD5C5C"), new Colour("lightcoral",
        "#F08080"), new Colour("lightseagreen", "#20B2AA"), new Colour("lime", "#00FF00"), new Colour("limegreen",
        "#32CD32"), new Colour("mediumorchid", "#BA55D3"), new Colour("mediumpurple", "#9370DB"), new Colour(
        "mediumslateblue", "#7B68EE"), new Colour("mediumvioletred", "#C71585"), new Colour("olive", "#808000"),
      new Colour("olivedrab", "#6B8E23"), new Colour("orangered", "#FF4500"), new Colour("orchid", "#DA70D6"),
      new Colour("palevioletred", "#DB7093"), new Colour("peru", "#CD853F"), new Colour("royalblue", "#4169E1"),
      new Colour("seagreen", "#2E8B57"), new Colour("sienna", "#A0522D"), new Colour("steelblue", "#4682B4"),
      new Colour("teal", "#008080"), new Colour("cyanblue", "#4c76a2"), new Colour("green", "green")
    ).sorted().toList();
  }

  public record Colour(String name, String value) implements Comparable<Colour>
  {
    @Override
    public int compareTo(Colour o)
    {
      return this.name.compareTo(o.name);
    }
  }

  public static List<Colour> getColours()
  {
    return COLOURS;
  }

  /**
   * Useless function that I use for regenerating the CSS files in the Angular app.
   *
   * @return a String formatted css like a list of ".chat-cinnabar { color:  #e44d2e;}"
   */
  public static String getColoursAsCss()
  {
    return COLOURS.stream()
      .map(colour -> String.format(".chat-%s { color: %s;}", colour.name, colour.value))
      .collect(Collectors.joining("\n"));
  }

  /**
   * Useless function that I use for generating the selector in the Angular app.
   * @return a String formatted html containing a "select" form component.
   */
  public static String getColoursAsInputfield()
  {
    String coloursAsHtml = COLOURS.stream()
      .filter(colour -> !colour.name.equals("black"))
      .map(colour -> String.format("<option value=\"%s\">%s</option>", colour.name, colour.name))
      .collect(Collectors.joining("\n"));
    return String.format("""
    <select class="form-control" id="colour" name="colour" formControlName="colour">
    <option selected="selected" value="black">black</option>
    %s
    </select>
    """, coloursAsHtml);
  }

}
