Keyboard Statistics
===================
Small open source utility program to record keyboard usage in multiple applications and display them
as a heatmap on the keyboard.

## Features
* Record key presses with direct input api in windows
* Display keyboard heatmap in customizable layout (SVG format)
* Export Heatmap as svg or png
* Currently supported layouts are: *Qwertz* and *Qwerty*

## Keyboard layouts
* Keyboard Layout must be a valid SVG Document
* The id attribute must be the `java.awt.KeyEvent.VK_*` Keycode *(can hex or decimal)*

## Notes
* Currently only works in Windows since it only implements the Windows raw device input API
* The [Qwerty](https://commons.wikimedia.org/wiki/File:Qwerty.svg) and [Qwertz](https://commons.wikimedia.org/wiki/File:Qwertz_de.svg) 
keyboard layouts are modified versions from Wikimedia 

## Dependencies
* **JNA** 4.5.1 
* **Batik** 1.10
* **ScalaFX** 8.0.144-R12
* **ScalaFXML** 0.4
* **fontawesomefx** 8.9

