/**
 * Project: BE_PUM
 * Package name: v2.org.analysis.apihandle.be_pum.winapi
 * File name: APIClass.java
 * Created date: Jan 30, 2015
 */
package v2.org.analysis.apihandle.winapi;

import java.util.ArrayList;
import java.util.List;
import org.jakstab.Program;

import org.jakstab.asm.AbsoluteAddress;
import org.jakstab.asm.Instruction;

import v2.org.analysis.environment.Environment;
import v2.org.analysis.environment.Memory;
import v2.org.analysis.environment.Register;
import v2.org.analysis.environment.Stack;
import v2.org.analysis.path.BPState;
import v2.org.analysis.value.LongValue;
import v2.org.analysis.value.Value;

/**
 * 
 * The stub abstract class is used for describe some primary attributes &
 * methods
 * 
 * @author Yen Nguyen
 * @version 0.2
 */
public abstract class API {
//	private final static boolean IS_ALWAYS_RETURN_SYMBOLIC = true;

	protected int NUM_OF_PARMS = 0;
	protected boolean IS_POP_STACK_VALUE = true;

	protected String libraryName;
	protected String apiName;

	protected BPState curState = null;
	protected Stack stack = null;
	protected Memory memory = null;
	protected Register register = null;

	protected List<Long> params = null;

	public void run(AbsoluteAddress address, BPState curState, Instruction inst, String apiName) throws APIException {
		this.apiName = apiName;
		this.curState = curState;
		this.initAttributes();

//		if (!IS_ALWAYS_RETURN_SYMBOLIC) {
			this.execute();
//		} else {
//			register.setRegisterValue("eax", new SymbolValue("api_eax_" + apiName));
//		}
	}

	public String getLibraryName() {
		return this.libraryName;
	}

	public String getAPIName() {
		return this.apiName;
	}

	public String getFullName() {
		return String.format("%s@%s.DLL", this.getAPIName(), this.getLibraryName());
	}

	public boolean is64bit() {
		if (this.apiName.charAt(this.apiName.length() - 1) == 'W') {
			return true;
		}
		return false;
	}

	protected void initAttributes() throws APIException {
		Environment env = curState.getEnvironement();
		this.stack = env.getStack();
		this.memory = env.getMemory();
		this.register = env.getRegister();

		if (NUM_OF_PARMS > 0) {
			this.params = new ArrayList<Long>();

			System.out.print("Argument:");

			for (int i = 0; i < NUM_OF_PARMS; i++) {
				Value value = stack.pop();

				if (value instanceof LongValue) {
					this.params.add(((LongValue) value).getValue());
				} else {
					throw new APIException(this);
				}
				
				System.out.print(String.format("\tx%d: %s\t", i + 1, value.toString()));
				
				// zunc: get printable value
				long lngAddress = ((LongValue) value).getValue();
				Program prog = Program.getProgram();
				if (Program.getProgram().isInside(new AbsoluteAddress(lngAddress))) {
					int ASCII = 1;
					int UNICODE = 2;
					String strAsc = this.memory.getText(ASCII, lngAddress);
					String strUni = this.memory.getText(UNICODE, lngAddress);
					if (!strAsc.isEmpty()) {
						System.out.print(String.format("\tx%d: %s\t", i + 1, strAsc));
					}
					
					if (!strUni.isEmpty()) {
						System.out.print(String.format("\tx%d: %s\t", i + 1, strUni));
					}
				}
				
			}
			System.out.println();

			if (!IS_POP_STACK_VALUE) {
				for (int i = this.params.size(); i > 0; i--) {
					stack.push(new LongValue(this.params.get(i - 1)));
				}
			}
		}
	}

	public abstract void execute();
}
