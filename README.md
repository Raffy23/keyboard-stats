Keyboard Statistics
===================
Small open source utility program to record keyboard usage and for display of Keyboard heatmaps.

## Keyboard layouts
* Keyboard Layout must be a valid SVG Document
* The id attribute must be the `java.awt.KeyEvent.VK_*` Keycode 

## Notes
* Currently only works in Windows since it only implements the Windows raw device input API
* The Standard Querty Layout is a modified version of [Qwerty.svg](https://commons.wikimedia.org/wiki/File:Qwerty.svg) taken from Wikimedia

## Dependencies
* **JNA** 4.5.1 
* **Batik** 1.10
* ScalaFX
* ScalaXML

