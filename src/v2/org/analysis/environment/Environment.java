package v2.org.analysis.environment;

import v2.org.analysis.system.SystemHandle;
import v2.org.analysis.value.LongValue;

import com.twmacinta.util.MD5;

public class Environment {
	private Stack stack;
	private Flag flag;
	private Memory memory;
	private Register register;
	public final static SystemHandle system = new SystemHandle();

	public Stack getStack() {
		return stack;
	}

	public void setStack(Stack stack) {
		this.stack = stack;
	}

	public Register getRegister() {
		return register;
	}

	public void setRegister(Register register) {
		this.register = register;
	}

	public Memory getMemory() {
		return memory;
	}

	public void setMemory(Memory memory) {
		this.memory = memory;
	}

	public Flag getFlag() {
		return flag;
	}

	public void setFlag(Flag flag) {
		this.flag = flag;
	}

	public Environment() {
		stack = new StackV2();

		flag = new Flag();
		flag.init();
		memory = new Memory();
		memory.setEnvironment(this);
		// memory.resetImportTable(Program.getProgram());

		register = new Register();
		// register.mov("edx", new
		// LongValue(Program.getProgram().getEntryPoint()
		// .getValue()));
		register.mov("edx", new LongValue(0x7C90E4F4));
		register.mov("esi", new LongValue(0xFFFFFFFF));
		// register.mov("ecx", new LongValue(0x12FFB0));
		register.mov("edi", new LongValue(0x7C910208));
		// register.mov("ebx", new LongValue(0x7FFD6000));
		// register.mov("eax", new LongValue(0));

		register.mov("cs", new LongValue(0x0));
		register.mov("ds", new LongValue(0x0));
		register.mov("es", new LongValue(0x0));
		register.mov("gs", new LongValue(0x0));
		register.mov("ss", new LongValue(0x0));
		register.mov("fs", new LongValue(0x7EFDD000));
		// register.mov("esp", new LongValue(((StackV2)stack).getTopAddress()));
		// register.mov("ebp", new
		// LongValue(((StackV2)stack).getBaseAddress()));

		if (stack instanceof StackV2) {
			((StackV2) stack).setEnvironment(this);
			// ((StackV2) stack).init(new LongValue(system.getKernel()
			// .getReturnRandomValue()));
			// ((StackV2) stack).init(new LongValue(0x7C817067));
			((StackV2) stack).init(new LongValue(0x7c8000c0));
		} else if (stack instanceof StackV1) {
			stack.push((new LongValue(system.getKernel().getReturnRandomValue())));
		}
	}

	public SystemHandle getSystem() {
		// TODO Auto-generated method stub
		return system;
	}

	@Override
	public Environment clone() {
		Environment ret = new Environment();
		if (register != null) {
			ret.setRegister(register.clone());
		}

		if (flag != null) {
			ret.setFlag(flag.clone());
		}

		if (memory != null) {
			ret.setMemory(memory.clone());
		}

		if (stack != null) {
			ret.setStack(stack.clone());
		}
		ret.getMemory().setEnvironment(ret);
		if (ret.getStack() instanceof StackV2) {
			((StackV2) ret.getStack()).setEnvironment(ret);
		}

		return ret;
	}

	public boolean equals(Environment e) {
		return register.equals(e.getRegister()) && memory.equals(e.getMemory()) && stack.equals(e.getStack())
		// && flag.equals(e.getFlag())
		;
	}

	public void reset() {
		// TODO Auto-generated method stub
		register.reset();
	}

	public String hash() {
		try {
			MD5 md5 = new MD5();

			// Hash memory
//			String memoryStr = memory.getOrderedStringContent();
//			md5.Update(memoryStr, null);
//			String hash1 = md5.asHex();

			// Hash Flag + Register + Stack
//			StringBuilder stringBuilder = new StringBuilder();
//			stringBuilder.append(flag.toString());
//			stringBuilder.append(register.toString());
//			stringBuilder.append(String.format("%d%d", ((StackV2)stack).getBaseAddress(), ((StackV2)stack).getTopAddress()));
//			md5.Update(stringBuilder.toString(), null);
			
//			String hash2 = md5.asHex();
//
//			// Append 2 string of hashing
//			stringBuilder.setLength(0);
//			stringBuilder.append(hash1);
//			stringBuilder.append(hash2);
//
//			// Return the final hashing string
//			md5.Update(stringBuilder.toString(), null);
//			return md5.asHex();
			
			md5.Update(register.toString(), null);
			return md5.asHex();
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return null;
	}
}
