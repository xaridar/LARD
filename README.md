# LScript
A custom interpreted scripting language, written in Java.

LScript is a primarily statically-typed scripting language, with the ability to use dynamic typing in most cases, as well.
It is written fully in Java.

To run LScript, download a full version directory from [releases], which contains the commandline interface, as well as an .exe to run files.<br>
Place this directory on your path, nd use the command 'lscript' in the command line to use the command line interface.

To run the language in command line, compile all the classes provided. The Main method is located in [Shell.java].


The interpreter can be run without any arguments, which will result in a command line version of the language; 
It can also be provided the path to a file, which it will subsequently process and run.

<br><br>
Some example LScript source files can be found in the [examples folder].
<br><br>

This language is based off of the [tutorial series] by CodePulse on Youtube titled "Create Your Own Programming Language," 
with different syntax choices, capabilities, and quirks, as well as converted from Python to Java.


[Shell.java]: src/main/java/lscript/Shell.java
[tutorial series]: https://www.youtube.com/watch?v=Eythq9848Fg&list=PLZQftyCk7_SdoVexSmwy_tBgs7P0b97yD
[examples folder]: examples
[releases]: releases
