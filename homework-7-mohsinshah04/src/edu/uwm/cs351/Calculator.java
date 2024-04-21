package edu.uwm.cs351;

import java.util.EmptyStackException;

import edu.uwm.cs351.util.IntMath;
import edu.uwm.cs351.util.Stack;

/**
 * Class to perform integer calculations online given method calls.
 * It uses normal arithmetic operator precedence, defined on the Operation enum,
 * and assumes left associativity. A calculator can be in one of three states:
 * <ol>
 * <li> Clear: Nothing pending
 * <li> Ready: A value is available
 * <li> Waiting: An operator has been started and we're waiting for a value
 * </ol>
 * At any point if a division by zero is caused, the appropriate exception is raised.
 */
public class Calculator {
	private Stack<Long> operands = new Stack<Long>();
	private Stack<Operation> operators = new Stack<Operation>();

	private long defaultValue;
	private boolean expectingValue;

	/**
	 * Create a calculator in the "clear" state with "0" as the default value.
	 */
	public Calculator() { 
		//TODO initialize the fields
		//	This depends on which design you choose.
		defaultValue=0;
		expectingValue=false;
		

	}
	//was given this recommendation by the tutor, many students used this as the 
	//tutor said this would be easier, specifically was with peter when talking to tutor
//	private enum State{
//		EMPTY,READY,WAITING;
//	}
//	private State state;
	
	private int checkState() {
		if(operands.isEmpty()&&operators.isEmpty())return 0;
		if(!expectingValue)return 1;
		return 2;
	}
	/**
	 * Enter a value into the calculator.
	 * The current value is changed to the argument.
	 * @pre not "Ready" 
	 * @post "Ready"
	 * @param x value to enter
	 * @exception IllegalStateException if precondition not met
	 */
	//this takes a value and you arent ready(state) and just pushes it to operands
	//and then sets expecting value to false because you now have the value
	public void value(long x) 
	{
		// TODO implement this
		if(checkState()==1)throw new IllegalStateException("is ready");
//		if(state==State.READY)throw new IllegalStateException("is ready");
		operands.push(x);
		expectingValue=false;

	}

	/**
	 * Start a parenthetical expression.
	 * @pre not "Ready" 
	 * @post "Waiting"
	 * @exception IllegalStateException if precondition not met
	 */
	//all this does is add a parenthesis and doesnt do much else, u make sure
	//you are expecting a value and you cant be ready.
	public void open() {
		// TODO implement this
		if(checkState()==1)throw new IllegalStateException("is ready");
		operators.push(Operation.LPAREN);
		expectingValue=true;
	}

	/**
	 * End a parenthetical expression.
	 * The current value shows the computation result since
	 * @pre "Ready"
	 * @post "Ready"
	 * @throws EmptyStackException if no previous unclosed open.
	 * @exception IllegalStateException if precondition not met
	 */
	//this closes a parenthesis, its more complex because you are ready before and have
	//to be ready after. while there isnt a parenthesis it keeps stepping until you get that 
	//open parenthesis, and then after that you just pop the open so its removed from the operators
	public void close() {
		// TODO implement this
		if(checkState()==0)throw new IllegalStateException();
		if(checkState()!=1)throw new IllegalStateException("is ready");
		while(operators.peek()!=Operation.LPAREN)step();
		operators.pop();
		expectingValue=false;
	}

	/**
	 * Start an operation using the previous computation and waiting for another argument.
	 * @param op operation to use, must be a binary operation, not null or a parenthesis.
	 * @pre not "Waiting"
	 * @post "Waiting"
	 * @throws IllegalArgumentException if the operator is illegal
	 * @exception IllegalStateException if precondition not met
	 */
	//same as value but adding an operation such as +-*/. the operation has to be binary,
	//cant be a lparen or rparen,and then you look at precedence and do the steps if
	//the ops precedence is less than the current operation
	public void binop(Operation op) {
		// TODO implement this
		if(op==null||op==Operation.LPAREN||op==Operation.RPAREN)throw new IllegalArgumentException("operator isnt legal");
		if(checkState()==2)throw new IllegalStateException("is waiting");
		while(!operators.isEmpty()&&op.precedence()<=operators.peek().precedence())step();
		operators.push(op);
		expectingValue=true;
	}

	/**
	 * Replace the current value with its unsigned integer square root.
	 * @see IntMath#isqrt(long)
	 * @pre not "Waiting"
	 * @post "Ready"
	 * @exception IllegalStateException if precondition not met
	 */
	//pop, take the square root of a number and just push it back in
	//if the stack is empty then you are just returning the default value
	public void sqrt() {
		// TODO implement this
		if(checkState()==2)throw new IllegalStateException("is waiting");
		long num=defaultValue;
		if(operands.isEmpty())num= defaultValue;
		else num=operands.pop();
		num=IntMath.isqrt(num);
		operands.push(num);
		expectingValue=false;
	}

	/**
	 * Compute one step.
	 */
	//go one step and catch arithmetic exceptions(random testing). if there isnt space for the
	//a number, then use default value, pop those 2 numbers out and push them back in after
	//calling operate(doing whatever the current operator is)
	private void step() {
		// TODO implement this
		if(operators.peek()==Operation.LPAREN){operators.pop();return;}
		
		long num2=defaultValue;
		long num1=defaultValue;
		if(!operands.isEmpty())num2=operands.pop();
		if(!operands.isEmpty())num1=operands.pop();
		try
		{
			if(!operators.isEmpty())operands.push(operators.pop().operate(num1, num2));

		}
		catch (ArithmeticException e)
		{
			clear();
			throw e;
		}

	}

	/**
	 * Return the current value.
	 * This is the last entered or computed value.
	 * @return current value.
	 */
	//return the current vlue, if there isnt  acurrent value, return default
	public long getCurrent() {
		// TODO implement this
		if(!(operands.isEmpty()))
		{
			return operands.peek();
		}
		return defaultValue;
	}

	/**
	 * Perform any pending calculations.
	 * Any previously unclosed opens are closed in the process.
	 * The new default value is the result of the computation.
	 * @pre not "Waiting"
	 * @post "Empty"
	 * @return result of computation
	 * @exception IllegalStateException if precondition not met
	 */
	
	//essentially step untill you cant step anymore, checking specifically the operators
	//if the stack with the numbers is empty then you set the return number to the default value
	public long compute() 
	{
		// TODO implement this
		if(checkState()==0)return defaultValue;
		if(checkState()==2)throw new IllegalStateException("waiting for expected value");
		while(!operators.isEmpty())
		{
			step();
		}
		long popped;
		if(!operands.isEmpty()) 
		{  
			popped=operands.pop();
			defaultValue=popped;
		}
		assert checkState()==0;//assertion failed error
		return defaultValue;
	}

	/**
	 * just reset everything, clear both stacks, reset default value and expecting value
	 * Clear the calculator, reseting the default value to zero.
	 * @post "Clear"
	 */
	public void clear() {
		// TODO implement this
		operators.clear();
		operands.clear();
		defaultValue=0;
		expectingValue=false;
	}
}
