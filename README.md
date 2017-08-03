# jenkolsql
Clutter free graphical SQL client that will allow you to view the structure of a JDBC compliant database, browse the data in tables and issue SQL commands.

# Motivation
I just need a very lightweight, simple, clutter free tools to execute sql.

# Status
This is very much pre-alpha and currently only supporting *Postgresql*. Immediate future is to support Mysql and Oracle (in this order).

# Development tools
* Java 8
* JavaFx
* CDI (Weld)
* Netbeans

# TODO List for v1.0
* Splash screen
* To incorporate better UI element either JFoenix (www.jfoenix.com) or 
MaterialFx (www.agix.pt/single-post/2015/09/02/MaterialFX-Material-Design-CSS-for-JavaFX)
* Fix Database Browser not able to resize.
* Syntax highlighting for Sql editor.
* Hide trails of Mysql and Oracle (such as the drop down selection) because they are not enabled yet.

# TODO list for v1.1
* Buffering large datasets to memory mapped file.
* Statistics for metadata information
* Enable support for Mysql
