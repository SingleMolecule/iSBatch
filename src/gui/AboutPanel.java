/************************************************************************
 * 				iSBatch  Copyright (C) 2015  							*
 *		Victor E. A. Caldas -  v.e.a.caldas at rug.nl					*
 *		C. Michiel Punter - c.m.punter at rug.nl						*
 *																		*
 *	This program is distributed in the hope that it will be useful,		*
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of		*
 * 	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the		*
 *	GNU General Public License for more details.						*
 *	You should have received a copy of the GNU General Public License	*
 *	along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ***********************************************************************/
package gui;

import javax.swing.JFrame;

import javax.swing.JLabel;

import java.awt.Desktop;
import java.awt.GridBagLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.JButton;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Color;
import java.net.URL;
import java.awt.Component;

import javax.swing.Box;

public class AboutPanel extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public AboutPanel(String version) {
		getContentPane().setBackground(Color.WHITE);
		setBackground(Color.WHITE);
		setTitle("About");
		
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[]{0, 0, 0, 0, 0, 0, 24, 0};
		gridBagLayout.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gridBagLayout.columnWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		getContentPane().setLayout(gridBagLayout);
		
		Component horizontalStrut = Box.createHorizontalStrut(20);
		GridBagConstraints gbc_horizontalStrut = new GridBagConstraints();
		gbc_horizontalStrut.insets = new Insets(0, 0, 5, 5);
		gbc_horizontalStrut.gridx = 0;
		gbc_horizontalStrut.gridy = 1;
		getContentPane().add(horizontalStrut, gbc_horizontalStrut);
		
		JLabel lblIsbatchA = new JLabel("iSBatch - A plugin for Hyerarchical Analysis");
		lblIsbatchA.setBackground(Color.WHITE);
		GridBagConstraints gbc_lblIsbatchA = new GridBagConstraints();
		gbc_lblIsbatchA.insets = new Insets(0, 0, 5, 5);
		gbc_lblIsbatchA.gridx = 3;
		gbc_lblIsbatchA.gridy = 1;
		getContentPane().add(lblIsbatchA, gbc_lblIsbatchA);
		
		Component horizontalStrut_1 = Box.createHorizontalStrut(20);
		GridBagConstraints gbc_horizontalStrut_1 = new GridBagConstraints();
		gbc_horizontalStrut_1.insets = new Insets(0, 0, 5, 5);
		gbc_horizontalStrut_1.gridx = 0;
		gbc_horizontalStrut_1.gridy = 2;
		getContentPane().add(horizontalStrut_1, gbc_horizontalStrut_1);
		
		JLabel lblCopyrightc = new JLabel("Copyright (C) 2015  ");
		GridBagConstraints gbc_lblCopyrightc = new GridBagConstraints();
		gbc_lblCopyrightc.insets = new Insets(0, 0, 5, 5);
		gbc_lblCopyrightc.gridx = 3;
		gbc_lblCopyrightc.gridy = 2;
		getContentPane().add(lblCopyrightc, gbc_lblCopyrightc);
		
		JLabel lblCurrentVersionBla = new JLabel("Current Version: " +  version);
		GridBagConstraints gbc_lblCurrentVersionBla = new GridBagConstraints();
		gbc_lblCurrentVersionBla.insets = new Insets(0, 0, 5, 5);
		gbc_lblCurrentVersionBla.gridx = 3;
		gbc_lblCurrentVersionBla.gridy = 3;
		getContentPane().add(lblCurrentVersionBla, gbc_lblCurrentVersionBla);
		
		JLabel lblVictorEA = new JLabel("Victor E. A. Caldas -  v.e.a.caldas at rug.nl\t\t\t\t\t");
		GridBagConstraints gbc_lblVictorEA = new GridBagConstraints();
		gbc_lblVictorEA.insets = new Insets(0, 0, 5, 5);
		gbc_lblVictorEA.gridx = 3;
		gbc_lblVictorEA.gridy = 5;
		getContentPane().add(lblVictorEA, gbc_lblVictorEA);
		
		JLabel lblCMichielPunter = new JLabel("C. Michiel Punter - c.m.punter at rug.nl");
		GridBagConstraints gbc_lblCMichielPunter = new GridBagConstraints();
		gbc_lblCMichielPunter.insets = new Insets(0, 0, 5, 5);
		gbc_lblCMichielPunter.gridx = 3;
		gbc_lblCMichielPunter.gridy = 6;
		getContentPane().add(lblCMichielPunter, gbc_lblCMichielPunter);
		
		JButton btnNewButton_2 = new JButton("Bug tracker");
		btnNewButton_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					Desktop.getDesktop().browse(
							new URL("https://github.com/SingleMolecule/iSBatch/issues").toURI());
				} catch (Exception e1) {
					LogPanel.log(e1.getMessage());
				}
			}
		});
		
		JButton btnNewButton_1 = new JButton("Changelog");
		btnNewButton_1.setBackground(Color.LIGHT_GRAY);
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					Desktop.getDesktop().browse(
							new URL("https://github.com/SingleMolecule/iSBatch/blob/master/CHANGELOG.md").toURI());
				} catch (Exception e1) {
					LogPanel.log(e1.getMessage());
				}
			}
		});
		
		JButton btnNewButton = new JButton("Source Code");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					Desktop.getDesktop().browse(
							new URL("https://github.com/SingleMolecule/iSBatch").toURI());
				} catch (Exception e1) {
					LogPanel.log(e1.getMessage());
				}
			}
		});
		
		JButton btnWebsite = new JButton("Website");
		btnWebsite.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					Desktop.getDesktop().browse(
							new URL("http://singlemolecule.github.io/iSBatch/").toURI());
				} catch (Exception e1) {
					LogPanel.log(e1.getMessage());
				}
			}
		});
		btnWebsite.setBackground(Color.LIGHT_GRAY);
		GridBagConstraints gbc_btnWebsite = new GridBagConstraints();
		gbc_btnWebsite.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnWebsite.insets = new Insets(0, 0, 5, 5);
		gbc_btnWebsite.gridx = 3;
		gbc_btnWebsite.gridy = 8;
		getContentPane().add(btnWebsite, gbc_btnWebsite);
		btnNewButton.setBackground(Color.LIGHT_GRAY);
		GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
		gbc_btnNewButton.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnNewButton.insets = new Insets(0, 0, 5, 5);
		gbc_btnNewButton.gridx = 3;
		gbc_btnNewButton.gridy = 9;
		getContentPane().add(btnNewButton, gbc_btnNewButton);
		GridBagConstraints gbc_btnNewButton_1 = new GridBagConstraints();
		gbc_btnNewButton_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnNewButton_1.insets = new Insets(0, 0, 5, 5);
		gbc_btnNewButton_1.gridx = 3;
		gbc_btnNewButton_1.gridy = 10;
		getContentPane().add(btnNewButton_1, gbc_btnNewButton_1);
		btnNewButton_2.setBackground(Color.LIGHT_GRAY);
		GridBagConstraints gbc_btnNewButton_2 = new GridBagConstraints();
		gbc_btnNewButton_2.fill = GridBagConstraints.HORIZONTAL;
		gbc_btnNewButton_2.insets = new Insets(0, 0, 0, 5);
		gbc_btnNewButton_2.gridx = 3;
		gbc_btnNewButton_2.gridy = 11;
		getContentPane().add(btnNewButton_2, gbc_btnNewButton_2);
		
		
		pack();
		setVisible(true);
		
	
	}
	
	public static void main(String[] args) {
		String version = "v0.2-beta";
		new AboutPanel(version);
	}
	
	

}
