package logic;

import java.util.ArrayList;

public class Board implements Comparable<Board> {
	int[][] board;
	Board parent;
	ArrayList<Block> blocks;
	int h; //distance to solution
	int g; //distance already achieved
	String search;
	int depth;
	ArrayList<Board> successors;

	Board(int[][] board, Board parent_board, int h, int g, String search, int depth) {
		this.build_board(board, parent_board, h, g, search);
		this.depth = depth;
	}
	
	public Board(int[][] board, Board parent_board, int h, int g, String search) {
		this.build_board(board, parent_board, h, g, search);
		this.depth = 0;
	}
	
	private void build_board(int[][] board, Board parent_board, int h, int g, String search) {
		this.board = board;
		this.blocks = new ArrayList<>();
		this.parent = parent_board;
		this.h = h;
		this.g = g;
		this.search = search;
		this.build_blocks();
	}

	public ArrayList<Board> generate_successors() {
		if(this.successors != null) {
			return this.successors;
		}
		this.successors = new ArrayList<>();
		for(int i = 0; i < this.blocks.size(); i++) {
			int line_bef = this.blocks.get(i).get_line();
			int column_bef = this.blocks.get(i).get_column();
			int line_aft = this.blocks.get(i).get_line();
			int column_aft = this.blocks.get(i).get_column();
			int line_bef_empty = this.blocks.get(i).get_line();
			int column_bef_empty = this.blocks.get(i).get_column();
			int line_aft_empty = this.blocks.get(i).get_line();
			int column_aft_empty = this.blocks.get(i).get_column();
			if(this.blocks.get(i).get_direction().equals("horizontal")) {
				column_bef--;
				column_bef_empty += this.blocks.get(i).get_length()-1;
				column_aft += this.blocks.get(i).get_length();
			} else {
				line_bef--;
				line_bef_empty += this.blocks.get(i).get_length()-1;
				line_aft += this.blocks.get(i).get_length();
			}
			//generate board with block moved to cell before
			if(this.board[line_bef][column_bef] == 0) {
				int[][] new_board_bef = this.copy_board();
				new_board_bef[line_bef][column_bef] = this.blocks.get(i).get_id();
				new_board_bef[line_bef_empty][column_bef_empty] = 0;
				Board new_bef = new Board(new_board_bef, this, calculate_h(new_board_bef), this.g+1, this.search, this.depth+1);
				if(this.parent == null || (this.parent != null && !this.parent.compare_board(new_bef)))
					this.successors.add(new_bef);
			} 
			//generate board with block moved to cell after
			if(this.board[line_aft][column_aft] == 0) {
				int[][] new_board_aft = this.copy_board();
				new_board_aft[line_aft][column_aft] = this.blocks.get(i).get_id();
				new_board_aft[line_aft_empty][column_aft_empty] = 0;
				Board new_aft = new Board(new_board_aft, this, calculate_h(new_board_aft), this.g+1, this.search, this.depth+1);
				if(this.parent == null || (this.parent != null && !this.parent.compare_board(new_aft)))
					this.successors.add(new_aft);
			}
		}

		return this.successors;
	}
	
	public Board generate_specific_successor(int block_id, int direction) {
		Board new_board = null;
		System.out.println(block_id);
		for(int i = 0; i < this.blocks.size(); i++) {
			if(this.blocks.get(i).get_id() == block_id) {
				int new_line = this.blocks.get(i).get_line();
				int new_column = this.blocks.get(i).get_column();
				int old_line = this.blocks.get(i).get_line();
				int old_column = this.blocks.get(i).get_column();
				if(direction == 1 && this.blocks.get(i).get_direction().equals("vertical")) {
					new_line--;
					old_line += this.blocks.get(i).get_length()-1;
				} else if(direction == 1) {
					new_column--; 
					old_column += this.blocks.get(i).get_length()-1;
				} else if(direction == 2 && this.blocks.get(i).get_direction().equals("vertical")) {
					new_line += this.blocks.get(i).get_length();
				} else {
					new_column += this.blocks.get(i).get_length();
				}
				int[][] new_board_matrix = this.copy_board();
				new_board_matrix[new_line][new_column] = this.blocks.get(i).get_id();
				new_board_matrix[old_line][old_column] = 0;
				new_board = new Board(new_board_matrix, this, calculate_h(new_board_matrix), this.g+1, this.search, this.depth+1);
			}
		}
		return new_board;
	}

	public void build_blocks() {
		boolean found = false;

		for (int i = 0; i < this.board.length; i++) { // percorre linha
			for (int j = 0; j < this.board[i].length; j++) { //percorre a coluna
				//System.out.println(board[i][j]);

				if (this.board[i][j] > 0) // se for um bloco
				{
					found=false;
					for (int x = 0; x < this.blocks.size(); x++) // verificar se o bloco j� existe na lista
					{
						if (this.board[i][j] == this.blocks.get(x).get_id()) {
							// se existir aumenta o length
							this.blocks.get(x).increase_length();
							found = true;

						}
						
					}
					
					if (found == false) {// n�o encontrou o bloco -> cria um novo
						Block newBlock = new Block(board[i][j], j, i);
						blocks.add(newBlock);
						
					}

				}

			}
		}
	}
	
	/**
	 * This function calculates the distance to the final board, taking into consideration the 
	 * distance between block 1 and the exit and the number of blocks blocking its way.
	 * @param board
	 * @return
	 */
	public int calculate_h(int[][] board) {
		int num_blocks = 0, distance_to_exit = 0;
		boolean found_block = false;
		for(int i = 0; i < board[3].length; i++) {
			if(board[3][i]==1)
				found_block = true;
			else if(!found_block)
				continue;
			else if(found_block && board[3][i] > 1) {
				num_blocks++;
				distance_to_exit++;
			}
			else if(found_block && board[3][i] != 1 && board[3][i] != -1)
				distance_to_exit++;
		}
		return num_blocks + distance_to_exit;
	}
	
	public Boolean is_final() {
		if(this.board[3][6] == 1) {
			return true;
		}
		return false;
	}
	
	public Board get_parent() {
		return this.parent;
	}
	
	public void set_parent(Board parent) {
		this.parent = parent;
		this.set_depth();
	}
	
	public int get_g() {
		return this.g;
	}
	
	public int get_h() {
		return this.h;
	}
	
	public int[][] copy_board() {
		int[][] new_array = new int[8][8];
		for(int i = 0; i < this.board.length; i++) {
			for(int j = 0; j < this.board[i].length; j++) {
				new_array[i][j] = board[i][j];
			}
		}
		return new_array;
	}
	
	public ArrayList<Block> get_blocks() {
		return this.blocks;
	}
	
	public void print_for_human() {
		for (int i = 0; i < this.board.length; i++) {
			for (int j = 0; j < this.board[i].length; j++) {
				if (this.board[i][j] == -2) {
					if (i == 0 || i == this.board.length - 1) {
						System.out.print("--");
					} else if (j == 0) {
						System.out.print("| ");
					} else if (j == this.board[i].length - 1) {
						System.out.print(" |");
					}
				} else if (this.board[i][j] == 0) {
					System.out.print("  ");
				} else if (this.board[i][j] == 1) {
					System.out.print("@@");
				} else if (this.board[i][j] > 1) {
					if (this.board[i][j] % 2 == 0) {
						System.out.print(">>");
					} else {
						System.out.print("\\/");
					}
				} else if (this.board[i][j] == -1) {
					System.out.print("  ");
				}
			}
			System.out.print("     ");
			for (int j = 0; j < this.board[i].length; j++) {
				if (this.board[i][j] == -2) {
					if (i == 0 || i == this.board.length - 1) {
						System.out.print("---");
					} else if (j == 0) {
						System.out.print("|  ");
					} else if (j == this.board[i].length - 1) {
						System.out.print("  |");
					}
				} else if (this.board[i][j] == 0) {
					System.out.print("   ");
				} else if (this.board[i][j] > 0) {
					if(this.board[i][j] / 10 >= 1)
						System.out.print(" " + this.board[i][j]);
					else
						System.out.print(" " + this.board[i][j] + " ");
				}
			}
			System.out.println();
		}
	}
	
	public void print() {
		for (int i = 0; i < this.board.length; i++) {
			for (int j = 0; j < this.board[i].length; j++) {
				if (this.board[i][j] == -2) {
					if (i == 0 || i == this.board.length - 1) {
						System.out.print("--");
					} else if (j == 0) {
						System.out.print("| ");
					} else if (j == this.board[i].length - 1) {
						System.out.print(" |");
					}
				} else if (this.board[i][j] == 0) {
					System.out.print("  ");
				} else if (this.board[i][j] == 1) {
					System.out.print("@@");
				} else if (this.board[i][j] > 1) {
					if (this.board[i][j] % 2 == 0) {
						System.out.print(">>");
					} else {
						System.out.print("\\/");
					}
				}
			}
			System.out.println();
		}
	}
	
	public int[][] get_board() {
		return this.board;
	}
	
	public boolean compare_board(Board b) {
		int[][] board_to_compare = b.get_board();
		for(int i = 0; i < this.board.length; i++) {
			for(int j = 0; j < this.board[i].length; j++) {
				if(board[i][j] != board_to_compare[i][j])
					return false;
			}
		}
		return true;
	}
	
	public int get_depth() {
		return this.depth;
	}
	
	public void set_depth() {
		if(parent == null)
			this.depth = 0;
		else
			this.depth = this.parent.get_depth()+1;
	}
	
	public ArrayList<Board> get_successors() {
		return successors;
	}
	
	public void set_successors(ArrayList<Board> successors) {
		this.successors = successors;
	}
	
	public boolean[] can_block_move(int block_id) {
		boolean[] can_move = new boolean[3];
		can_move[0] = false; //can move to place before?
		can_move[1] = false; //can move to place after?
		can_move[2] = false; //moves horizontally -> true else false
		for(int i = 0; i < this.blocks.size(); i++) {
			if(this.blocks.get(i).get_id() == block_id) {
				int line_bef = this.blocks.get(i).get_line();
				int column_bef = this.blocks.get(i).get_column();
				int line_aft = this.blocks.get(i).get_line();
				int column_aft = this.blocks.get(i).get_column();
				if(this.blocks.get(i).get_direction().equals("horizontal")) {
					column_bef--;
					column_aft += this.blocks.get(i).get_length();
					can_move[2] = true;
				} else {
					line_bef--;
					line_aft += this.blocks.get(i).get_length();
				}
				if(this.board[line_bef][column_bef] == 0)
					can_move[0] = true;
				if(this.board[line_aft][column_aft] == 0)
					can_move[1] = true;
			}
		}
		return can_move;
	}
	
	@Override
    public int compareTo(Board b) {
		if(this.search.equals("a_star"))
			return (this.g+this.h)-(b.get_g()+b.get_h());
		else if(this.search.equals("greedy"))
			return this.h-b.get_h();
		else if(this.search.equals("iter_deep")) {
			if(b.get_depth()-this.depth>0)
				return 1;
			if(b.get_depth()-this.depth<0)
				return -1;
		}
		return 0;
    }
}