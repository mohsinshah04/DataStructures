package edu.uwm.cs351;

public enum Operation {
	PLUS("+",1) {
		@Override
		public long operate(long d1, long d2) {
			return d1 + d2;
		}

	}, 
	MINUS("-",1) {
		@Override
		public long operate(long d1, long d2) {
			return d1 - d2;
		}
	}, 
	TIMES("*",2) {
		@Override
		public long operate(long d1, long d2) {
			return d1 * d2;
		}
	}, 
	DIVIDE("/",2) {
		@Override
		public long operate(long d1, long d2) {
			return d1 / d2;
		}
	},
	LPAREN("(",0),
	RPAREN(")",0),
	;
	
	private final String name;
	private final int precedence;
	private Operation(String nm, int prec) {
		name = nm;
		precedence = prec;
	}
	
	/**
	 * Find an operator with this name.  This differs from {@link #valueOf(String)}
	 * because it uses the operator string, not the identifier.  It uses +, not PLUS.
	 * @param name a string such as "+"
	 * @return operation with that string
	 * @throws IUllegalArgumentException if no operation with this string 
	 */
	public static Operation find(String name) {
		for (Operation op : Operation.values()) {
			if (op.name.equals(name)) return op;
		}
		throw new IllegalArgumentException("no such operation: " + name);
	}
	
	@Override
	public String toString() {
		return name;
	}
	
	/**
	 * Return identifier for this operation (e.g. PLUS)
	 * @return identifier of this operation.
	 */
	public String getId() {
		return super.toString();
	}
	
	public long operate(long d1, long d2) {
		throw new UnsupportedOperationException("unbalanced parenthesis");
	}
	public int precedence() {
		return precedence;
	}
}
