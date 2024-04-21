package edu.uwm.cs351;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EmptyStackException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

public class CalculatorGUI extends JFrame {

	/**
	 * Keep Eclipse happy
	 */
	private static final long serialVersionUID = 1L;
	
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				CalculatorGUI c = new CalculatorGUI();
				c.setSize(300,300);
				c.setVisible(true);
			}
		});
	}
	
	private static final int FONT_SIZE = 20;
	
	public CalculatorGUI() {
		super("Hexidecimal Calculator");
		JPanel contentPane = new JPanel();
		contentPane.setLayout(new BorderLayout());
		JPanel buttonPane = new JPanel();
		contentPane.add(buttonPane,BorderLayout.CENTER);
		resultPane = new ResultPane();
		resultPane.setPreferredSize(new Dimension(FONT_SIZE*(16+1),FONT_SIZE*2));
		contentPane.add(resultPane,BorderLayout.NORTH);
		buttonPane.setLayout(new GridLayout(5,5));
		
		JButton[] buttons = new JButton[16];
		for (int i=0; i < 16; ++i) {
			String hexits = "" + (char)((i < 10 ? '0' : ('A'-10))+i);
			buttons[i] = new JButton(hexits);
			buttons[i].addActionListener(new AddHexitsActionListener(hexits));
		}
		JButton byteButton = new JButton("00");
		byteButton.addActionListener(new AddHexitsActionListener("00"));
		
		JButton[] opButtons = new JButton[Operation.values().length];
		for (Operation op : Operation.values()) {
			int i = op.ordinal();
			opButtons[i] = new JButton(op.toString());
			opButtons[i].addActionListener(new OperationActionListener(op));
		}
				
		JButton clearButton = new JButton("C");
		clearButton.setForeground(Color.RED);
		JButton sqrtButton = new JButton("\u221a");
		JButton eqlButton = new JButton("=");
		
		for (int i=0; i < 4; ++i) {
			for (int j=0; j < 4; ++j) {
				buttonPane.add(buttons[i*4+j]);
			}
			buttonPane.add(opButtons[i]);
		}
		buttonPane.add(clearButton);
		buttonPane.add(opButtons[Operation.LPAREN.ordinal()]);
		buttonPane.add(opButtons[Operation.RPAREN.ordinal()]);
		buttonPane.add(sqrtButton);
		buttonPane.add(eqlButton);
		
		this.setContentPane(contentPane);
		
		this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

		clearButton.addActionListener(new ClearListener());
		eqlButton.addActionListener(new EqlListener());
		sqrtButton.addActionListener(new SqrtListener());
	}
	
	
	private Font myFont = new Font("Monospaced",Font.BOLD,20);
	
	private Calculator calculator = new Calculator();
	private StringBuilder builder = new StringBuilder();
	
	private void handleBuilder() {
		if (builder.length() > 0) {
			long result = Long.parseLong(builder.toString(), 16);
			calculator.value(result);
			builder.setLength(0);
			resultPane.repaint();
		}
	}
	
	private void handleException(RuntimeException ex) {
		if (ex instanceof EmptyStackException || ex instanceof NumberFormatException) {
			builder.setLength(0);
			builder.append("Error ");
			resultPane.repaint();
		} else if (ex instanceof IllegalStateException){
			Toolkit.getDefaultToolkit().beep();
		} else if (ex instanceof ArithmeticException) {
			builder.setLength(0);
			builder.append("Error: ");
			builder.append(ex.getMessage());
			resultPane.repaint();
		} else {
			throw ex; // not expected
		}
	}
	

	private long getResult() {
		return calculator.getCurrent();
	}
	
	private ResultPane resultPane;
	
	private class ResultPane extends JPanel {

		/**
		 * Keep Eclipse Happy
		 */
		private static final long serialVersionUID = 1L;

		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			g.setFont(myFont);
			g.setColor(Color.BLACK);
			String toShow;
			if (builder.length() > 0) {
				toShow = builder.toString();
			} else {
				toShow = String.format("%16X",getResult());
			}
			g.drawString(toShow,FONT_SIZE/2,FONT_SIZE*3/2);
		}
		
	}
	
	private class ClearListener implements ActionListener {
		public void actionPerformed(ActionEvent arg0) {
			builder.setLength(0);
			calculator.clear();
			resultPane.repaint();
		}
	}
	
	private class SqrtListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			try {
				handleBuilder();
				calculator.sqrt();
			} catch (RuntimeException ex) {
				handleException(ex);
			}
			resultPane.repaint();
		}
		
	}
	
	private class EqlListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			try {
				handleBuilder();
				calculator.compute();
			} catch (RuntimeException ex) {
				handleException(ex);
			}
			resultPane.repaint();
		}
	}
	
	private class AddHexitsActionListener implements ActionListener {

		private final String hexits;
		
		AddHexitsActionListener(String add) {
			hexits = add;
		}
		
		public void actionPerformed(ActionEvent e) {
			if (builder.toString().startsWith("Err")) return; // don't change display
			builder.append(hexits);
			resultPane.repaint();
		}
		
	}
	
	private class OperationActionListener implements ActionListener {
		private final Operation op;
		
		OperationActionListener(Operation o) {
			op = o;
		}

		public void actionPerformed(ActionEvent e) {
			try {
				handleBuilder();
				switch (op) {
				case LPAREN:
					calculator.open();
					break;
				case RPAREN:
					calculator.close();
					break;
				default:
					calculator.binop(op);
					break;
				}
			} catch (RuntimeException ex) {
				handleException(ex);
			}
		}
	}
}
