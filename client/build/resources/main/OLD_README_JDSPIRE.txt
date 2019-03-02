----------------------------------------

         D R A G O N S P I R E S

   (http://stuff2do.systs.net/dspire)

              presented by

           Me&Mo Productions

----------------------------------------


Contents of README file for: jdspire.zip
----------------------------------------

1.0 .. About DragonSpires
1.1 .... Zip contents
2.0 .. Installation/Running
2.1 .... Linux
2.2 .... Mac OS
2.3 .... Windows
2.4 .... Other OSes
3.0 .. Getting help

----------------------------------------


1.0 - About DragonSpires
---

DragonSpires is a graphical multiplayer online game written
in Java. The world of DragonSpires is generally a fantasy
world where players can collect items, test their skills in
combat, explore, and chat with others.
This README file will give you information about installing
and starting the version of DragonSpires contained in the zip.


1.1 - Zip contents
---

The jdspire.zip is the general DragonSpires distribution. It
contains all the files for running DragonSpires as a Java
application or applet on any Java-capable system.
Regardless of whether or not you run DragonSpires as an applet
or application, you have all of the core DragonSpires features.
When running DragonSpires as an applet, however, you have the
option of having sound effects and music. When running
DragonSpires as an application, you do not have the sound
option, but you do have the option, that is not available in
applet-mode, to choose which server you connect to. It's up
to you which mode you decide to run DragonSpires in.


2.0 - Installation
---

As stated before, DragonSpires is a Java program, and thusly
it will run on any Java-capable system. DragonSpires strives
to be 100% compatible with Java 1.0, so as to run on any
system with Java. Refer to the section for your operating
system below.
(Some abbreviations used commonly below are
those for the Sun's Java Development Kit(JDK) and Java
Runtime Environment(JRE).)


2.1 - Linux
---

To run DragonSpires on Linux you'll need a JDK or JRE, both
of which Sun provides versions of for Linux.

You can get the JDK/JRE at: http://java.sun.com

I recommend notgetting the newest version of either of these
products, because there are often bugs. As of this writing
JDK 1.2.2 worked just fine with DragonSpires. You simply need
to install your JDK/JRE and then unzip DragonSpires into the
directory of your choice. Go to the directory and use this
command:

java DragonSpiresFrame&

That command runs DragonSpires as a Java application, to run
DragonSpires as an applet use this command:

appletviewer dsapplet.html&

You will have to have X started, of course, and you will have
to leave the terminal window open. I haven't been able to add
DragonSpires to any kind of application menu and have it execute
successfully, but I have been able to use the Alt-F2 command in
KDE to execute a script which then executes DragonSpires, so
that I don't have to have an extra terminal window open. My
script, which I named 'runds', looks like this:

#!/bin/sh
cd /opt/dsclient
/opt/jdk1.2.2/bin/java DragonSpiresFrame -smallfonts&

You will, of course, want to change the directories for your
system, and whether or not you want to use the '-smallfonts'
option is up to you.


2.2 - Mac OS
---

DragonSpires has never been officially tested on Mac OS, outside
a web browser that is. There's no reason it shouldn't work
though, but you'll need the Mac OS Runtime for Java(MRJ).

You can get the MRJ at: http://www.apple.com/java

I have no idea how to MRJ. But I can tell you that 'DragonSpiresFrame'
is the main application class, and 'dsapplet.html' is the html file
for running the applet. If all else fails you can always just fire
up your Java-capable web browser and play DragonSpires on the web,
which has been officially tested and works just fine on Mac OS.


2.3 - Windows
---

While there are Windows-specific downloads for DragonSpires on the
site, if you want to use this zip for some strange reason, this is
how you can do it. DragonSpires has been officially tested using
Sun's JDK/JRE and Microsoft's [Java] Virtual Machine(VM). You'll
first want to check to see if you have the 'jview' or 'wjview'
applications in your windows directory. If you have them, then you
have the Microsoft VM. To run DragonSpires as an application with
Microsoft's VM use this command in the directory you unzipped
DragonSpires to:

jview.exe DragonSpiresFrame

In rare cases you will have to put 'DragonSpiresFrame.class' instead
to get it to work. To run DragonSpires as an applet with Microsoft's
VM use this command:

jview.exe /a dsapplet.html

If your system is up to date you will have the 'wjview.exe' command too,
which you can use instead of 'jview.exe', so as to run DragonSpires
without a pesky DOS window.

If you don't have the Microsoft VM, or you just don't want to use it,
you can get a JRE/JDK from: http://java.sun.com
Install the JRE/JDK, then in the directory you unzipped DragonSpires
into use this command to run DragonSpires as an application:

java DragonSpiresFrame

To run DragonSpires as an applet, which, as of this writing, you are
only able to do with the JDK, NOT the JRE, use this command:

appletviewer dsapplet.html

In some older versions of the JRE the command would be 'jre' instead
of 'java'.


2.4 - Other OSes
---

DragonSpires has not been tested on any other operating systems
other than those listed above. Ideally, if a Java Virtual Machine(JVM)
exists for your machine, you just need to figure out how to use it
and you should be just fine. There are most certainly JVMs out there
for UNIX-based systems. If you're using Solaris you probably already
have a JVM installed. If you get DragonSpires to run on BeOS, UNIX, or
whatever, it would be enormously appreciated if you would take a
screenshot and e-mail Mech at mech20@home.com about it.


3.0
---

If you're having trouble playing DragonSpires there are a few
resources where you can find help. You can check the DragonSpires web
site's help section, post a question on the DragonSpires Message Board,
or if all else fails you can e-mail Mech, DragonSpires' programmer, at
mech20@home.com. If you have any questions about the game's art work,
storyline, or content, feel free to e-mail Motorhed, DragonSpires'
artist, at petrockfromhell@hotmail.com.


We hope you enjoy your stay in DragonSpires!
