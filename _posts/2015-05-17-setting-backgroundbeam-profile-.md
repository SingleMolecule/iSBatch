---
layout: post
title: Setting Background/Beam Profile
categories: [manual]
tags: []
published: True
---

Background is a image taken without your sample. It should represent imperfections of the surface, the excitation profile and other features.

iSBatch currently provides 2 ways of adding your Background/BeamProfile image.

 1 - Load the image directly.

 2 - Averaging all images under the selected node. If you select to generate the image on an Experiment, all images under it will be used, for instance.

![bg]({{site.baseurl}}/images/manual/setBackground.png)

Note you may use any image - or perform other pre-processing steps using the MacroPanel.

![]({{site.baseurl}}/images/manual/flattenComparison.png)

And the histograms may also be compared.

![]({{site.baseurl}}/images/manual/flattenComparisonHist.png)
