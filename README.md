# MatterNote
##What is it?
MatterNote is a note-taking app build on android platform.
##Features
The app is tested by Espresso, Android testing framework for UI testing. It also apply Material Design elements such as color palette, view transition, floating action button.
##Functions
MatterNote can save plain text content. It organize the content at notebook-note hierarchy.
There are three Fragments in the app. NotebookFragment show all notebook at the app. NoteListFragment show all note within notebook. SingleNoteFragment can edit and view certain note.
##Build
To Download the project, at command line, type the command below
```
git clone https://github.com/LamKanSing/MatterNote.git
```
Or you can click the "Download Zip" button to get the zip file, then extract the file. 
Open the file by Android Studio. 
##Test
1. Prepare testing device
To Run the Espresso test, you need a android device connected with USB cable or emulator
**Turn off animations** on your test device. Leaving system animations turned on in the test device might cause unexpected results or may lead your test to fail.
To turn off animations on the device or emulator, click *Settings*, then click *Developing Options* and then turning all the following options off:

-Window animation scale
-Transition animation scale
-Animator duration scale

2. Edit Configurations

    Open Run menu | Edit Configurations
    Add a new Android Tests configuration
    Set the "Name" field of the test as "Espresso Test"
    Choose the module "app"
    Choose "Test" the "Class" and type the class name "com.lamkansing.matternote.EspressoTest"
    Add a Specific instrumentation runner: android.support.test.runner.AndroidJUnitRunner
    Set the "Target Device" field as you like
    Apply this configuration, run the testing by running "Espresso Test"


## License
The program license under GNU Affero General Public License v3.0. Please see the file called License.txt
