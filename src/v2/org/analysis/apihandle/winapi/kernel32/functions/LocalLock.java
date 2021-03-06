/**
 * 
 */
package v2.org.analysis.apihandle.winapi.kernel32.functions;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.WinDef.LPVOID;
import com.sun.jna.platform.win32.WinNT.HANDLE;

import v2.org.analysis.apihandle.winapi.kernel32.Kernel32API;
import v2.org.analysis.apihandle.winapi.kernel32.Kernel32DLL;

import org.jakstab.asm.AbsoluteAddress;
import org.jakstab.asm.Instruction;

import v2.org.analysis.environment.Environment;
import v2.org.analysis.environment.Register;
import v2.org.analysis.environment.Stack;
import v2.org.analysis.path.BPState;
import v2.org.analysis.value.LongValue;
import v2.org.analysis.value.Value;

/**
 * Locks a local memory object and returns a pointer to the first byte of the
 * object's memory block.
 * 
 * @param hMem
 *            A handle to the local memory object. This handle is returned by
 *            either the LocalAlloc or LocalReAlloc function.
 * 
 * @return If the function succeeds, the return value is a pointer to the first
 *         byte of the memory block. If the function fails, the return value is
 *         NULL. To get extended error information, call GetLastError.
 * 
 * @author Yen Nguyen
 *
 */
public class LocalLock extends Kernel32API {

	public LocalLock() {
		super();
		NUM_OF_PARMS = 1;
	}

	@Override
	public void execute() {
		long t1 = this.params.get(0);

		HANDLE hMem = new HANDLE(new Pointer(t1));
		LPVOID ret = Kernel32DLL.INSTANCE.LocalLock(hMem);

		long value = (ret == null) ? 0 : Pointer.nativeValue(ret.toPointer());
		register.mov("eax", new LongValue(value));
		System.out.println("Return Value: " + value);
	}

}
