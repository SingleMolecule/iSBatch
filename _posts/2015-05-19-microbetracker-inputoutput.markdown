---
layout: post
title: "MicrobeTracker Input/Output"
date: "2015-05-19"
categories: [manual]
tags: []
published: True
---

iSBatch offers a simple connection with [MicrobeTracker][1a637c74] to assign automatically cell outlines.

When analysing _Rapid Acquisition_ datasets, one image can be used as a reference for the field of view.

In this example, we show the **Bright Field** as reference.

![mt1]({{site.baseurl}}/images/manual/manual-microbeTracker.png)

Note the **DnaQ** Experiment node selected. The selected type is **Raw**, with no customize parameters. Select _Proccess_  to create a stack containing all images from the particular selected node. This image must be loaded into [MicrobeTracker][1a637c74] to assing cell outlines.

The [MicrobeTracker][1a637c74] is saved with the tag **MTInput** - e.g.[Green]MTInput.tif.



[MicrobeTracker][1a637c74] produces a **.mat** file. Select the tab **Load** again in **Microbe Tracker IO**.

Load the **.mat** file generated on [MicrobeTracker][1a637c74] and the corresponding input file.


![mt2]({{site.baseurl}}/images/manual/manual-microbeTracker2.png)


After this operation, every _Field of View_ will have a file called **cellsROI.zip**. 


  [1a637c74]: http://microbetracker.org/ "MicrobeTracker"
