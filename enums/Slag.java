package enums;

/**
 * Enum klasse for kortslagene
 * Clubs, Diamonds, Hearts, Spades
 * 
 * @author Michael Johansen
 * @author Anja Rønning
 * @author Cathrine Holm
 * @version 0.2
 */
public enum Slag
{
	C, D, H, S;
	
	public static Slag getSlag( int c )
	{
		switch (c) {
		case 0: return C;
		case 1: return D;
		case 2: return H;
		default: return S;
		}
	}
}
