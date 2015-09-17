/**
 * Project: BE-PUMv2
 * Package name: v2.org.analysis.apihandle.winapi.kernel32.functions
 * File name: InterlockedIncrement.java
 * Created date: Sep 17, 2015
 * Description:
 */
package v2.org.analysis.apihandle.winapi.kernel32.functions;

import org.jakstab.asm.DataType;
import org.jakstab.asm.x86.X86MemoryOperand;

import v2.org.analysis.apihandle.winapi.kernel32.Kernel32API;
import v2.org.analysis.value.LongValue;

/**
 * Increments (increases by one) the value of the specified 32-bit variable as
 * an atomic operation. To operate on 64-bit values, use the
 * InterlockedIncrement64 function.
 * 
 * @param Addend
 *            A pointer to the variable to be incremented.
 * 
 * @return The function returns the resulting incremented value.
 * 
 * @author Yen Nguyen
 *
 */
public class InterlockedIncrement extends Kernel32API {

	public InterlockedIncrement() {
		super();
		NUM_OF_PARMS = 1;
	}

	@Override
	public void execute() {
		long t1 = this.params.get(0);

		LongValue value = (LongValue) memory.getDoubleWordMemoryValue(t1);
		
		long v = value.getValue();
		v++;
		value = new LongValue(v);
		
		memory.setDoubleWordMemoryValue(new X86MemoryOperand(DataType.INT32, t1), value);
		
		register.mov("eax", value);
	}

}