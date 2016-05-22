package v2.org.analysis.transition_rule.x86instruction;

import org.jakstab.asm.Immediate;
import org.jakstab.asm.x86.X86MemoryOperand;
import v2.org.analysis.complement.Convert;

import v2.org.analysis.path.BPState;
import v2.org.analysis.transition_rule.stub.X86InstructionStub;
import v2.org.analysis.value.DoubleValue;
import v2.org.analysis.value.LongValue;
import v2.org.analysis.value.Value;

public class fmul extends X86InstructionStub {

	@Override
	public BPState execute() {
		if (dest == null) {
			return curState;
		}

		if (dest.getClass().getSimpleName().equals("X86Register")) {
			d = env.getRegister().getRegisterValue(dest.toString());
		} else if (dest.getClass().getSimpleName().equals("X86RegisterPart")) {
			d = env.getRegister().getRegisterValue(dest.toString());
		} else if (dest.getClass().getSimpleName().equals("Immediate")) {
			// Immediate t = (Immediate) dest;
			// long x = t.getNumber().longValue();
			d = new LongValue(Convert.convetUnsignedValue(((Immediate) dest).getNumber().longValue(),
					rule.getBitCount(inst)));
		} else if (dest.getClass().getSimpleName().equals("X86MemoryOperand")) {
			// d = symbolValueMemoryOperand
			// .getMemoryOperandVal((X86MemoryOperand) dest);
			X86MemoryOperand t = (X86MemoryOperand) dest;
			if (t.getSegmentRegister() != null && t.getSegmentRegister().toString() == "%fs"
					&& t.getDisplacement() == 0) {
				// PHONG: update 20150526-----------------
				d = new LongValue(env.getSystem().getSEHHandler().getStart().getAddrSEHRecord());
				// ---------------------------------------
			} else {
				d = env.getMemory().getQWordMemoryValue(t);
				long lngD = ((LongValue) d).getValue();
				d = new DoubleValue(Double.longBitsToDouble(lngD));
			}

		} else if (dest.getClass().getSimpleName().equals("X86SegmentRegister")) {
			d = env.getRegister().getRegisterValue(dest.toString());
		} else if (dest.getClass().getSimpleName().equals("X86FloatRegister")) {
			d = env.getRegister().getRegisterValue(dest.toString());
		}
		
		Value st = env.getRegister().getSt();
		double dbOp1, dbOp2;
		dbOp1 = d instanceof DoubleValue ?
				((DoubleValue) d).getValue() :
				((LongValue) d).getValue();
		dbOp2 = st instanceof DoubleValue ?
				((DoubleValue) st).getValue() :
				((LongValue) st).getValue();
		double dbRet = dbOp2 != 0 ? dbOp1 * dbOp2 : 0.0;
		DoubleValue ret = new DoubleValue(dbRet);
		env.getRegister().setSt(ret);
		System.out.println(String.format(" -> %.2f * %.2f = %.2f", dbOp1, dbOp2, ret.getValue()));
		return null;
	}

}
