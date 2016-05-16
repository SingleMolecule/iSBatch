---
layout: post
title: Peak Fitting
categories: [manual]
tags: []
published: True

---

The `Peak Finder` plugin detects all peaks withing certain conditions and return ROIS for each detected peak. iSBatch detects peaks for all images in a certain node saving the results in one zip file at `FieldOfView/PeakFinder/peaks.zip`.

 ![Alt text]({{ site.baseurl}}/images/manual/manual-peakfinder1.png)



 Fill the empty spaces with the following information:

```
 * Name: iSBatch
 * URL: http://sites.imagej.net/Vcaldas
 * Host: webdav:Vcaldas
 ```
 
![Alt text]({{ site.baseurl}}/images/manual/20150516-updateManual.png)

Close the panel to continue;

3 - Back to the ImageJ Updater panel, select on **View options** : *View files of iSBatch site*

![Alt text]({{ site.baseurl}}/images/manual/20150516-updateManual2.png)

4 - Click **install**  and then **Apply changes** to download iSBatch;

5 - Restart Fiji;

```
iSBAtch is stored in the folder Fiji/plugins/jar.
```

If you find any issue, please report on our [Issues Page](https://github.com/SingleMolecule/iSBatch/issues)

