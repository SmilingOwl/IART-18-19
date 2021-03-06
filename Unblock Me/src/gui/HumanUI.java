package gui;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;

import logic.*;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;
import java.awt.event.ActionEvent;
import java.awt.Font;
import javax.swing.JLabel;

public class HumanUI {

	private JFrame frame;
	private JTextField textFile;
	private int[][] ini_board = null;
	private Board board;
	private Search search;
	private JTextField textField;
	private boolean dirChoice = false;
	private int block = 0;
	private ArrayList<Board> sequence;
	private int step = 0;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					HumanUI window = new HumanUI();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public HumanUI() {
		initialize();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame("Human Mode");
		frame.setBounds(100, 100, 702, 692);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);

		textFile = new JTextField();
		textFile.setBounds(45, 32, 185, 28);
		frame.getContentPane().add(textFile);
		textFile.setColumns(10);

		Grid grid = new Grid();
		grid.setLocation(45, 442);

		frame.getContentPane().add(grid);
		grid.setSize(280, 200);

		JButton btnLoadFile = new JButton("Load");
		addLoadButtonListener(grid, btnLoadFile);
		btnLoadFile.setBounds(253, 35, 89, 23);
		frame.getContentPane().add(btnLoadFile);

		ButtonGroup group = new ButtonGroup();

		JTextArea textOutput = new JTextArea();
		textOutput.setFont(new Font("Courier New", Font.PLAIN, 13));
		textOutput.setEditable(false);
		textOutput.setBounds(45, 71, 454, 358);
		JScrollPane scrollPane = new JScrollPane(textOutput);
		scrollPane.setBounds(45, 71, 454, 358);
		frame.getContentPane().add(scrollPane);

		PrintStream out = new PrintStream(new TextAreaOutputStream(textOutput));

		JButton btnPC = new JButton("PC Mode");
		addPcListener(btnPC);
		btnPC.setBounds(538, 32, 109, 28);
		frame.getContentPane().add(btnPC);

		textField = new JTextField();
		textField.setBounds(538, 127, 109, 28);
		frame.getContentPane().add(textField);
		textField.setColumns(10);

		JLabel lblBlock = new JLabel("Block to move");
		lblBlock.setBounds(538, 102, 109, 14);
		frame.getContentPane().add(lblBlock);

		JButton btnUpButton = new JButton("Up");

		btnUpButton.setEnabled(false);
		btnUpButton.setBounds(556, 253, 78, 23);
		frame.getContentPane().add(btnUpButton);

		JButton btnDownButton = new JButton("Down");

		btnDownButton.setEnabled(false);
		btnDownButton.setBounds(556, 348, 78, 23);
		frame.getContentPane().add(btnDownButton);

		JButton btnRight = new JButton("Right");

		btnRight.setEnabled(false);
		btnRight.setBounds(598, 305, 78, 23);
		frame.getContentPane().add(btnRight);

		JButton btnLeft = new JButton("Left");

		btnLeft.setEnabled(false);
		btnLeft.setBounds(509, 305, 78, 23);
		frame.getContentPane().add(btnLeft);

		addUpListener(grid, btnUpButton, btnDownButton, btnRight, btnLeft);

		addLeftListener(grid, btnUpButton, btnDownButton, btnRight, btnLeft);

		addRightListener(grid, btnUpButton, btnDownButton, btnRight, btnLeft);

		addDownListener(grid, btnUpButton, btnDownButton, btnRight, btnLeft);

		JButton btnBlock = new JButton("Move");
		addMoveListener(grid, btnUpButton, btnDownButton, btnRight, btnLeft, btnBlock);

		JButton btnNextStep = new JButton("Next");
		JButton btnPrevStep = new JButton("Previous");
		btnPrevStep.setEnabled(false);
		addPrevListener(grid, btnNextStep, btnPrevStep);
		btnPrevStep.setBounds(334, 470, 96, 23);
		frame.getContentPane().add(btnPrevStep);

		btnNextStep.setEnabled(false);
		addNextListener(grid, btnNextStep, btnPrevStep);
		btnNextStep.setBounds(334, 524, 96, 23);
		frame.getContentPane().add(btnNextStep);

		btnBlock.setBounds(548, 174, 89, 23);
		frame.getContentPane().add(btnBlock);

		JLabel lblHint = new JLabel("");
		lblHint.setBounds(45, 440, 454, 14);
		frame.getContentPane().add(lblHint);

		JButton btnHint = new JButton("Hint");
		addHintListener(btnHint);
		btnHint.setBounds(509, 406, 167, 23);
		frame.getContentPane().add(btnHint);

		System.setOut(out);
		System.setErr(out);

	}
	/**
	 * Adds listener to hint button
	 */
	private void addHintListener(JButton btnHint) {
		btnHint.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				board.set_parent(null);
				Search search = new AStarSearch(board);
				Board final_board = search.run();
				ArrayList<Board> sequence = search.generate_sequence(final_board);
				int[] move = board.check_moved_block(sequence.get(1));
				System.out.print("Move block number " + move[0]);
				switch (move[1]) {
				case 0:
					System.out.println(" to the left.");
					break;
				case 1:
					System.out.println(" to the right.");
					break;
				case 2:
					System.out.println(" up.");
					break;
				default:
					System.out.println(" down.");
				}
			}
		});
	}
	/**
	 * Adds listener to next button
	 */
	private void addNextListener(Grid grid, JButton btnNextStep, JButton btnPrevStep) {
		btnNextStep.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				try {
					grid.setBoard(sequence.get(step).get_board());
					step++;

					if (step >= sequence.size()) {
						btnNextStep.setEnabled(false);
					}
					if (step > 0) {
						btnPrevStep.setEnabled(true);
					}
				} catch (Exception e) {
					System.out.println("Board was finished");
				}

			}
		});
	}

	/**
	 * Adds listener to prev button
	 */
	private void addPrevListener(Grid grid, JButton btnNextStep, JButton btnPrevStep) {
		btnPrevStep.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				try {
					step--;
					grid.setBoard(sequence.get(step).get_board());

					if (step == 0) {
						btnPrevStep.setEnabled(false);
					}
					if (step < sequence.size()) {
						btnNextStep.setEnabled(true);
					}
				} catch (Exception e) {
					System.out.println("Invalid Operation");
				}

			}
		});
	}
	/**
	 * Adds listener to move button
	 */
	private void addMoveListener(Grid grid, JButton btnUpButton, JButton btnDownButton, JButton btnRight, JButton btnLeft, JButton btnBlock) {
		btnBlock.addActionListener(new ActionListener() {
			@SuppressWarnings("deprecation")
			public void actionPerformed(ActionEvent e) {

				boolean[] can_move = new boolean[3];
				can_move[0] = false;
				can_move[1] = false;
				can_move[2] = false;
				boolean dont_get_out = false;

				try {
					block = Integer.parseInt(textField.getText());
				} catch (Exception e1) {
					System.out.println("Invalid Input");
				}
				if (block < 0) {
					System.out.println("Invalid Block");
				} else if (block != 0) {
					can_move = board.can_block_move(block);
					if (can_move[0] == false && can_move[1] == false) {
						System.out.println("The chosen block can't move!");
						dont_get_out = true;
					}
				}
				if (block == 0) {
					board.set_parent(null);
					Search search = new AStarSearch(board);
					Board final_board = search.run();
					ArrayList<Board> sequence = search.generate_sequence(final_board);
					int[] move = board.check_moved_block(sequence.get(1));
					System.out.print("Move block number " + move[0]);
					switch (move[1]) {
					case 0:
						System.out.println(" to the left.");
						break;
					case 1:
						System.out.println(" to the right.");
						break;
					case 2:
						System.out.println(" up.");
						break;
					default:
						System.out.println(" down.");
					}

				}

				int direction = 1;
				if (can_move[0] && can_move[1]) {
					if (can_move[2]) {
						System.out.println("Choose left or right.");
						btnRight.setEnabled(true);
						btnLeft.setEnabled(true);
						btnDownButton.setEnabled(false);
						btnUpButton.setEnabled(false);
						dirChoice = true;
					} else {
						System.out.println("Choose up or down.");
						btnDownButton.setEnabled(true);
						btnUpButton.setEnabled(true);
						btnRight.setEnabled(false);
						btnLeft.setEnabled(false);
						dirChoice = true;
					}
					return;

				} else if (can_move[0]) {
					direction = 1;
					btnDownButton.setEnabled(false);
					btnUpButton.setEnabled(false);
					btnRight.setEnabled(false);
					btnLeft.setEnabled(false);
				} else {
					direction = 2;
					btnDownButton.setEnabled(false);
					btnUpButton.setEnabled(false);
					btnRight.setEnabled(false);
					btnLeft.setEnabled(false);
				}
				board = board.generate_specific_successor(block, direction);
				board.print_for_human();
				grid.setBoard(board.get_board());
				if (board.is_final())
					System.out.println("You win!");

			}
		});
	}
	/**
	 * Adds listener to down button
	 */
	private void addDownListener(Grid grid, JButton btnUpButton, JButton btnDownButton, JButton btnRight, JButton btnLeft) {
		btnDownButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dirChoice = false;
				board = board.generate_specific_successor(block, 2);
				board.print_for_human();
				btnDownButton.setEnabled(false);
				btnUpButton.setEnabled(false);
				btnRight.setEnabled(false);
				btnLeft.setEnabled(false);
				grid.setBoard(board.get_board());

				if (board.is_final())
					System.out.println("You win!");
			}
		});
	}
	/**
	 * Adds listener to right button
	 */
	private void addRightListener(Grid grid, JButton btnUpButton, JButton btnDownButton, JButton btnRight, JButton btnLeft) {
		btnRight.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dirChoice = false;
				board = board.generate_specific_successor(block, 2);
				board.print_for_human();
				btnDownButton.setEnabled(false);
				btnUpButton.setEnabled(false);
				btnRight.setEnabled(false);
				btnLeft.setEnabled(false);
				grid.setBoard(board.get_board());
				if (board.is_final())
					System.out.println("You win!");

			}
		});
	}
	/**
	 * Adds listener to left button
	 */
	private void addLeftListener(Grid grid, JButton btnUpButton, JButton btnDownButton, JButton btnRight, JButton btnLeft) {
		btnLeft.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dirChoice = false;
				board = board.generate_specific_successor(block, 1);
				board.print_for_human();
				btnDownButton.setEnabled(false);
				btnUpButton.setEnabled(false);
				btnRight.setEnabled(false);
				btnLeft.setEnabled(false);
				grid.setBoard(board.get_board());
				if (board.is_final())
					System.out.println("You win!");
			}
		});
	}
	/**
	 * Adds listener to up button
	 */
	private void addUpListener(Grid grid, JButton btnUpButton, JButton btnDownButton, JButton btnRight, JButton btnLeft) {
		btnUpButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dirChoice = false;
				board = board.generate_specific_successor(block, 1);
				board.print_for_human();
				btnDownButton.setEnabled(false);
				btnUpButton.setEnabled(false);
				btnRight.setEnabled(false);
				btnLeft.setEnabled(false);
				grid.setBoard(board.get_board());
				if (board.is_final())
					System.out.println("You win!");
			}
		});
	}
	/**
	 * Adds listener to PC button
	 */
	private void addPcListener(JButton btnPC) {
		btnPC.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				frame.dispose();
				new UI();
			}
		});
	}
	/**
	 * Adds listener to load button
	 */
	private void addLoadButtonListener(Grid grid, JButton btnLoadFile) {
		btnLoadFile.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				try {
					FileReader file_reader = new FileReader(textFile.getText());
					ini_board = Main.parseBoard(file_reader);
					System.out.println("Loaded file.");
					board = new Board(ini_board, null, 0, 0, "a_star");
					board.print_for_human();

					grid.setBoard(ini_board);
					grid.repaint();
				} catch (FileNotFoundException e) {
					System.out.println("File not found.");
				}
			}
		});
	}
}
