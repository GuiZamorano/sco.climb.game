Steps to build and run project!

First make sure you download MongoDB: https://www.mongodb.com/download-center/community

Download IntelliJ as well.

How to clone and build:


Step 1: Clone the repository into a directory on your local machine.

Step 2: Open IntelliJ and go to File -> New -> New Project From Existing Sources

Step 3: Find the directory where you cloned the project and select the entire directory. It should be called "sco.climb.game". This directory should contain three subdirectories: "sco.climb.context-model", "sco.climb.domain", "sco.climb.game-dashboard"

Step 4: After selecting the main directory choose "Import project from external module" and select "Maven"

Step 5: THIS IS IMPORTANT! Make sure you select "Search for projects recursively" Then click next. There should be three projects listed. Click next again. The project will now be shown in IntelliJ


How to Run:


Step 1: Open a command line terminal and type 

  $ mongod --dbpath=.
  
This will create a local MongoDB instance on your local machine

Step 2: Find the file sco.climb.game-dashboard/src/main/java/ClimbDashboardApp.java aka this one https://github.com/GuiZamorano/sco.climb.game/blob/master/sco.climb.game-dashboard/src/main/java/it/smartcommunitylab/climb/gamification/dashboard/ClimbDashboardApp.java

Step 3: Open the file, right click on it, and hit run (or debug if you want to set breakpoints and test code).

Step 4: Open a web browser and go to http://localhost:8080. Hint (To debug UI changes right click on the page, select inspect, go to sources, find your file and set breakpoints accordingly.


-Gui
