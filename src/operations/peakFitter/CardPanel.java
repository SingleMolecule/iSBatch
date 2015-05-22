package operations.peakFitter;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

/** @see http://stackoverflow.com/questions/6432170 */
public class CardPanel extends JPanel{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	/**
	 * 
	 */
	private Random random = new Random();
	private JPanel cards = new JPanel(new CardLayout());
	private JComboBox<CardPanel> combo = new JComboBox<CardPanel>();
	private String name;

	public CardPanel(String name) {
		setPreferredSize(new Dimension(320, 240));
		setBackground(new Color(random.nextInt()));
		add(new JLabel(name));
	}

	@Override
	public String toString() {
		return name;
	}

	public static void main(String[] args) {
		CardPanel card = new CardPanel("Teste");
		card.create();

	}

	private void create() {
		JFrame f = new JFrame();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		
		CardPanel p = new CardPanel("Panel : "+ String.valueOf(1));
		combo.addItem(p);
		cards.add(p, p.toString());
		
		CardPanel p1 = new CardPanel("Panel : "+ String.valueOf(2));
		combo.addItem(p1);
		cards.add(p1, p1.toString());
		
//		for (int i = 1; i < 9; i++) {
//			CardPanel p = new CardPanel("Panel : "+ String.valueOf(i));
//			combo.addItem(p);
//			cards.add(p, p.toString());
//		}
		JPanel control = new JPanel();
		combo.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                JComboBox<?> jcb = (JComboBox<?>) e.getSource();
                CardLayout cl = (CardLayout) cards.getLayout();
                cl.show(cards, jcb.getSelectedItem().toString());
            }
        });
		control.add(combo);
		f.add(cards, BorderLayout.CENTER);
		f.add(control, BorderLayout.NORTH);
		f.pack();
		f.setLocationRelativeTo(null);
		f.setVisible(true);
	}

	public void setPanel(String selected) {

		CardLayout cl = (CardLayout) cards.getLayout();
		cl.show(cards, selected);

	}

//	@Override
//	public void actionPerformed(ActionEvent e) {
//		JComboBox jcb = (JComboBox) e.getSource();
//        CardLayout cl = (CardLayout) cards.getLayout();
//        cl.show(cards, jcb.getSelectedItem().toString());
//
//	}

}