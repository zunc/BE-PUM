package v2.org.analysis.value;

import v2.org.analysis.complement.BitVector;
import v2.org.analysis.complement.Convert;

import java.util.List;
import java.util.Map;

public class DoubleValue implements Value {
	private double value;

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return "Concrete Value";
	}

	public DoubleValue(double value) {
		super();
		this.value = value;
	}

	public DoubleValue(long intValue, int bitCount) {
		// TODO Auto-generated constructor stub
		super();
		if (bitCount == 8)
			this.value = intValue & 0xFF;
		else if (bitCount == 16)
			this.value = intValue & 0xFFFF;
		else
			this.value = intValue;
	}

	@Override
	public boolean equal(Value e) {
		// TODO Auto-generated method stub
		if (e instanceof DoubleValue)
			return value == ((DoubleValue) e).getValue();
		return false;
	}

	public double getValue() {
		return value;
	}

	@Override
	public String toString() {
		//return Convert.longToHex(value) + "";
		return value + "";
	}

	@Override
	public DoubleValue clone() {
		return new DoubleValue(value);
	}

	@Override
	public Value movFunction(Value exp) {
		// TODO Auto-generated method stub
		Value result = null;
		if (exp instanceof DoubleValue)
			result = new DoubleValue(((DoubleValue) exp).getValue());
		else if (exp instanceof SymbolValue)
			result = new SymbolValue(((SymbolValue) exp).getVarName());
		else if (exp instanceof HybridValue)
			result = new HybridValue(((HybridValue) exp).getLeft(), ((HybridValue) exp).getConnector(),
					((HybridValue) exp).getRight());
		else if (exp instanceof TopValue)
			return new TopValue();
		return result;
	}

	@Override
	public Value addFunction(Value exp) {
		// TODO Auto-generated method stub
		Value result = null;
		if (exp instanceof DoubleValue)
			result = new DoubleValue(value + ((DoubleValue) exp).getValue());
		else if (exp instanceof SymbolValue)
			result = new HybridValue(exp, "+", new DoubleValue(this.value));
		else if (exp instanceof HybridValue)
			result = new HybridValue(exp, "+", new DoubleValue(this.value));
		else if (exp instanceof TopValue)
			return new TopValue();
		return result;
	}

	@Override
	public Value subFunction(Value exp) {
		// TODO Auto-generated method stub
		Value result = null;
		if (exp instanceof DoubleValue)
			result = new DoubleValue(value - ((DoubleValue) exp).getValue());
		else if (exp instanceof SymbolValue)
			result = new HybridValue(new DoubleValue(this.value), "-", exp);
		else if (exp instanceof HybridValue)
			result = new HybridValue(new DoubleValue(this.value), "-", exp);
		else if (exp instanceof TopValue)
			return new TopValue();
		return result;
	}

	@Override
	public String toStringPreFix() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public Value unsignedMulFunction(Value exp) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public Value signedMulFunction(Value exp, int size) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public Value signedMulFunction(Value exp) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public Value unsignedDivFunction(Value exp) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public Value signedDivFunction(Value exp) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public Value andFunction(Value exp) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public Value orFunction(Value exp) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public Value modFunction(Value v) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public Value powFunction(Value v) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public Value notFunction() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public Value xorFunction(Value exp) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public Value rrFunction(Value exp) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public Value rlFunction(Value exp) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public Value evaluate(Map<String, Long> z3Value) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public Map<String, Long> getValueMap() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void setValueMap(Map<String, Long> valueMap) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public Value rl8Function(Value s) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public Value rl16Function(Value s) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public Value rr16Function(Value s) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public Value rr8Function(Value s) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public Value powFunction(int i) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public List<String> getVariable() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}


}
