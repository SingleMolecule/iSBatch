---
layout: post
title: "Beam Profile Correction"
date: "2015-05-19"
categories: [manual]
tags: []
published: True
---


When acquiring images, it is natural that the illumination profile is not even. iSBatch provides a tool to correct the acquired image so the image illumination is evenly distributed.

The calculation performed is:

$$FlatImage = { (InputImage - Electronic Offset) \over Normalized(BeamProfile - Electronic Offset) }.$$

The electronic Offset may be provided as a image or as a constant (integer).

Images will be saved alongside the input image and with the tag "\_flat".

Note that the operation will flat the entire stack.

![flat]({{site.baseurl}}/images/manual/flatten.png).

The GUI provides a DropDown menu to allow addition of different algorithms for Image Flattening.
