=================================================================
README - HaiQ Relational Probabilistic Model Analyzer
=================================================================


--------
CONTENTS
--------

Once the contents of the package have been extracted, the distribution should
contain the following files and folders:

	- README.txt - This file.
	- haiq - HaiQ startup script.
	- /lib - Library folder including haiq.jar, as well as other jar and native 
	         libraries (e.g., Alloy, PRISM).

------------
INSTALLATION
------------

The current binary prototype distribution of HaiQ only supports Mac OSX. 
To install it:
	
1.	cd $HAIQ_ROOT, where $HAIQ_ROOT is the folder where the contents of the 
                   package have been extracted.

2. Edit the startup script "haiq", and set the value of the "HAIQ_DIR" variable 
   to point to your $HAIQ_ROOT folder.


It is also advisable to add the $HAIQ_ROOT folder to your path.