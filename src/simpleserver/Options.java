/*******************************************************************************
 * Open Source Initiative OSI - The MIT License:Licensing
 * The MIT License
 * Copyright (c) 2010 Charles Wagner Jr. (spiegalpwns@gmail.com)
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 ******************************************************************************/
package simpleserver;

import java.util.Scanner;

import simpleserver.config.CommandList;

public class Options extends AbstractOptions {
  private static final String[] ranks = new String[] { "warpPlayerRank",
      "warpPlayerRank", "teleportRank", "homeCommandRank", "giveRank",
      "givePlayerRank", "muteRank", "muteRank", "setRankRank", "useWarpRank",
      "createWarpRank" };
  private static final String[] rankConversions = new String[] { "warptome",
      "warpmeto", "tp", "home", "give", "giveplayer", "mute", "unmute",
      "setgroup", null, null };

  public Options() {
    super("simpleserver.properties");
  }

  public boolean contains(String option) {
    String value = options.getProperty(option);
    return value != null && value.trim().length() > 0;
  }

  public int getInt(String option) {
    String value = options.getProperty(option);
    try {
      return Integer.parseInt(value);
    }
    catch (NumberFormatException e) {
      String defaultValue = options.getProperty(option);
      if (!value.equals(defaultValue)) {
        options.setProperty(option, defaultOptions.getProperty(option));
        return getInt(option);
      }
      else {
        e.printStackTrace();
        System.out.println("Error: Asked for int value of " + option);
        return Integer.MIN_VALUE;
      }
    }
  }

  public boolean getBoolean(String option) {
    return Boolean.parseBoolean(options.getProperty(option));
  }

  public void set(String option, String value) {
    options.setProperty(option, value);
  }

  public void load() {
    super.load();

    boolean needsConversion = false;
    for (String rank : ranks) {
      if (options.contains(rank)) {
        needsConversion = true;
        break;
      }
    }

    if (needsConversion) {
      conversion();
    }

    if (getInt("internalPort") == getInt("port")) {
      System.out.println("OH NO! Your 'internalPort' and 'port' properties are the same! Edit simpleserver.properties and change them to different values. 'port' is recommended to be 25565, the default port of minecraft, and will be the port you actually connect to.");
      System.out.println("Press enter to continue...");
      Scanner in = new Scanner(System.in);
      in.nextLine();
      System.exit(0);
    }
  }

  protected void missingFile() {
    super.missingFile();

    System.out.println("Properties file not found! Created simpleserver.properties! Adjust values and then start the server again!");
    System.out.println("Press enter to continue...");
    Scanner in = new Scanner(System.in);
    in.nextLine();
    System.exit(0);
  }

  private void conversion() {
    CommandList cmds = new CommandList();
    cmds.load();
    for (int c = 0; c < ranks.length; ++c) {
      if (rankConversions[c] != null) {
        String value = options.getProperty(ranks[c]);
        if (value != null) {
          try {
            cmds.setGroup(rankConversions[c], Integer.parseInt(value));
          }
          catch (NumberFormatException e) {
          }
        }
      }
    }
    cmds.save();

    for (String rank : ranks) {
      options.remove(rank);
    }
    save();

    System.out.println("The Properties file format has changed! Command ranks are now set in command-list.txt!");
    System.out.println("Your previous settings for commands have been saved, and cleared from simpleserver.properties!");
    System.out.println("Press enter to continue...");
    Scanner in = new Scanner(System.in);
    in.nextLine();
    System.exit(0);
  }
}
