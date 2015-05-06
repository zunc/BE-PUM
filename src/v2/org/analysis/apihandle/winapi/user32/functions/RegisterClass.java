/**
 * Project: BE_PUM_V2
 * Package name: v2.org.analysis.apihandle.be_pum.winapi.user32.functions
 * File name: RegisterClass.java
 * Created date: Mar 27, 2015
 * Description:
 */
package v2.org.analysis.apihandle.winapi.user32.functions;

import com.sun.jna.Pointer;
import com.sun.jna.WString;
import com.sun.jna.platform.win32.WinDef.*;

import v2.org.analysis.apihandle.winapi.structures.WinUser.WNDCLASS;
import v2.org.analysis.apihandle.winapi.structures.WinUser.WNDCLASS.WNDPROC;
import v2.org.analysis.apihandle.winapi.user32.User32API;
import v2.org.analysis.apihandle.winapi.user32.User32DLL;

import org.jakstab.asm.AbsoluteAddress;
import org.jakstab.asm.DataType;
import org.jakstab.asm.Instruction;
import org.jakstab.asm.x86.X86MemoryOperand;

import v2.org.analysis.environment.Environment;
import v2.org.analysis.environment.Memory;
import v2.org.analysis.environment.Register;
import v2.org.analysis.environment.Stack;
import v2.org.analysis.path.BPState;
import v2.org.analysis.value.LongValue;
import v2.org.analysis.value.Value;

/**
 * Registers a window class for subsequent use in calls to the CreateWindow or
 * CreateWindowEx function.
 * 
 * @param lpWndClass
 *            A pointer to a WNDCLASS structure. You must fill the structure
 *            with the appropriate class attributes before passing it to the
 *            function.
 * 
 * @return If the function succeeds, the return value is a class atom that
 *         uniquely identifies the class being registered. This atom can only be
 *         used by the CreateWindow, CreateWindowEx, GetClassInfo,
 *         GetClassInfoEx, FindWindow, FindWindowEx, and UnregisterClass
 *         functions and the IActiveIMMap::FilterClientWindows method. If the
 *         function fails, the return value is zero. To get extended error
 *         information, call GetLastError.
 * 
 * @author Yen Nguyen
 *
 */
public class RegisterClass extends User32API {

	public RegisterClass() {
	}

	@Override
	public boolean execute(AbsoluteAddress address, String funcName, BPState curState, Instruction inst) {
		Environment env = curState.getEnvironement();
		Stack stack = env.getStack();
		Memory memory = env.getMemory();
		Register register = env.getRegister();

		Value x1 = stack.pop();
		System.out.println("Argument:" + x1);

		if (x1 instanceof LongValue) {
			long t1 = ((LongValue) x1).getValue();

			// public UINT style;
			// public WNDPROC lpfnWndProc;
			// public int cbClsExtra;
			// public int cbWndExtra;
			// public HINSTANCE hInstance;
			// public HICON hIcon;
			// public HCURSOR hCursor;
			// public HBRUSH hbrBackground;
			// public WString lpszMenuName;
			// public WString lpszClassName;

			WNDCLASS lpWndClass = new WNDCLASS();
			lpWndClass.style = new UINT(((LongValue) memory.getDoubleWordMemoryValue(t1)).getValue());
			lpWndClass.lpfnWndProc = new WNDPROC() {
				@Override
				public LRESULT invoke(HWND hwnd, UINT unit, WPARAM wparam, LPARAM lparam) {
					// TODO Auto-generated method stub
					return null;
				}
			};
			t1 += 4;
			lpWndClass.cbClsExtra = (int) ((LongValue) memory.getDoubleWordMemoryValue(t1 += 4)).getValue();
			lpWndClass.cbWndExtra = (int) ((LongValue) memory.getDoubleWordMemoryValue(t1 += 4)).getValue();
			lpWndClass.hInstance = new HINSTANCE();
			lpWndClass.hInstance.setPointer(new Pointer((((LongValue) memory.getDoubleWordMemoryValue(t1 += 4))
					.getValue())));
			lpWndClass.hIcon = new HICON(new Pointer(
					(((LongValue) memory.getDoubleWordMemoryValue(t1 += 4)).getValue())));
			lpWndClass.hCursor = new HCURSOR(new Pointer(
					((LongValue) memory.getDoubleWordMemoryValue(t1 += 4)).getValue()));
			lpWndClass.hbrBackground = new HBRUSH(new Pointer(
					((LongValue) memory.getDoubleWordMemoryValue(t1 += 4)).getValue()));
			lpWndClass.lpszMenuName = new WString(memory.getText(new X86MemoryOperand(DataType.INT32,
					((LongValue) memory.getDoubleWordMemoryValue(t1 += 4)).getValue())));
			lpWndClass.lpszClassName = new WString(memory.getText(new X86MemoryOperand(DataType.INT32,
					((LongValue) memory.getDoubleWordMemoryValue(t1 += 4)).getValue())));

			ATOM ret = User32DLL.INSTANCE.RegisterClass(lpWndClass);

			register.mov("eax", new LongValue(ret.longValue()));
		}
		return false;
	}

}
