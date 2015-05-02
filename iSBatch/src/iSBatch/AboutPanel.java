/*
 * 
 */
package iSBatch;

import javax.swing.JPanel;
import javax.swing.JLabel;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.factories.FormFactory;
import javax.swing.JButton;

// TODO: Auto-generated Javadoc
/**
 * The Class AboutPanel.
 */
public class AboutPanel extends JPanel {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/**
	 * Instantiates a new about panel.
	 */
	public AboutPanel() {
		setLayout(new FormLayout(new ColumnSpec[] {
				ColumnSpec.decode("113px"),
				ColumnSpec.decode("224px"),},
			new RowSpec[] {
				FormFactory.LINE_GAP_ROWSPEC,
				RowSpec.decode("14px"),
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,
				FormFactory.RELATED_GAP_ROWSPEC,
				FormFactory.DEFAULT_ROWSPEC,}));
		
		JLabel lblThisIsA = new JLabel("This is a Place holder for the About information");
		add(lblThisIsA, "2, 2, left, top");
		
		JButton btnVisitWebsite = new JButton("Visit Website");
		add(btnVisitWebsite, "2, 10");
		
		JButton btnReportBug = new JButton("Report Bug");
		add(btnReportBug, "2, 12");
		
		JButton btnContribute = new JButton("Contribute");
		add(btnContribute, "2, 14");
		
	}

	
	
}
