---
layout: post
title: iSBatch Macro Functionality
categories: []
tags: [manual]
published: True
comments: true

---
Before running a macro the user has to select a node on which the macro will run. The macro will be executed for each file node that lies below the selected node and conforms to the given selection criteria. In this tutorial we run the macro on the DnaX project.

![main window]({{ site.baseurl}}/images/macro/main-window.png "Main Window")

After clicking the Run Macro operation it is necessary to select on which channel the macro needs to be executed.

![macro]({{ site.baseurl}}/images/macro/run-macro1.png "Macro 1")

Optionally it is also possible to specify keywords that need to be in the filename. Only file nodes with a filename that contains all the keywords will be considered.
When the macro changes the original file and you want the original file to be overwritten by the output of the macro you need to check the ‘replace original file’ checkbox.

![macro]({{ site.baseurl}}/images/macro/run-macro2.png "Macro 2")

In this first example we delete the first slice of each image stack that is of the green channel and belongs to the DnaX project (the node that we selected before). We specify the filename of the output file to be ‘[Green]tutorial1’. Instead of typing the macro every time you can also open a pre-existing macro file by clicking the ‘choose Macro file’ button.

![macro]({{ site.baseurl}}/images/macro/run-macro3.png "Macro 3")
 
After clicking the ‘run’ button the macro is executed for each file node of the green channel. The output of the macro is stored as ‘[Green]tutorial1’ in the database tree. The newly created file node will be alongside the file node on which the macro operated (they will have the same parent node). Note that the created files will be shown as ‘[Green][Green]tutorial1’ in the the database. This is because each filenode is shown with the corresponding channel as a prefix ‘[Green]’ . The filename is ‘[Green]tutorial1.tif’ so the node is shown as ‘[Green][Green]tutorial1.tif’.

![macro]({{ site.baseurl}}/images/macro/run-macro4.png "Macro 4")

It is also possible that the macro outputs a results table instead of an image. The following example does just that. 
 
We selected the DnaX project again and clicked on the ‘run macro’ operation. We run the macro on the green channel and we do not pose any constrains on the filename. After running the macro the tree contains a results table ‘[green]tutorial2.csv’ for each file node on which the macro executed.

![macro]({{ site.baseurl}}/images/macro/run-macro5.png "Macro 5")
 


