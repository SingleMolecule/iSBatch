package iSBatch;

import iSBatch.view.MainFrame;

/**
Copyright 2015, Victor E. A. Caldas
caldas.victor@gmail.com
v.e.a.cadas@rug.nl


and the Fiji project. Fiji is just imageJ - batteries included.

This version is based on the original iSBatch version, developed by Victor E.A. Caldas and Christiaan Michiel Punter.
University of Groningen, 2015.

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see http://www.gnu.org/licenses/ .
*/

/**
* iSBatch is a plugin to handle hyerarchical datasets and perform common analysis regarding to in Singulo and Single Molecule imaging.
*  @param <T>
*/

import ij.plugin.PlugIn;

public class iSbatch_ implements PlugIn {

	public static void main(final String... args) {
	//	new ij.ImageJ();
		new iSbatch_().run("");

	}

	@Override
	public void run(String arg0) {
		MainFrame mainFrame = new MainFrame();
		mainFrame.setVisible(true);
	}

}
