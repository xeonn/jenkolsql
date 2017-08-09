# jenkolsql
Clutter free graphical SQL client that will allow you to view the structure of a JDBC compliant database, browse the data in tables and issue SQL commands in rich text syntax highlighted environment.

# Motivation
I just need a very lightweight, simple, clutter free tools to execute sql.

# Status
The application is in a usable state despite still being under heavy development. For now it is supporting *Postgresql*, *Mysql* and *Oracle*.

For Oracle database to work, please download the jdbc driver separately from Oracle website, extract and drop it in the _lib_ folder.

# Development tools
* Java 8
* JavaFx
* CDI (Weld)
* Netbeans

# TODO List for v1.0
* ~~Splash screen~~ Done
* ~~Fix Database Browser not able to resize.~~ Done
* ~~Enable support for Mysql~~ Done
* ~~Syntax highlighting for Sql editor.~~ Done

# TODO list for v1.1
* Buffering large datasets to memory mapped file.
* Statistics for metadata information
* Auto drop down keyword in Sql Editor.
* Popup notification (from controlsfx) at Connection dialog informing user of Oracle case sensitivity issues.
* Cache retrieved database structure (Catalog, Schema, Tables etc).
* Add Functions/Procedure object into TreeView

# Cancelled Plan
* ~~To incorporate better UI element either JFoenix (www.jfoenix.com) or 
MaterialFx (www.agix.pt/single-post/2015/09/02/MaterialFX-Material-Design-CSS-for-JavaFX)~~
* ~~Hide trails of Oracle (such as the drop down selection) because it is not enabled yet.~~

# Acknowledgement
* Thomas Mikula - RichtextFx

