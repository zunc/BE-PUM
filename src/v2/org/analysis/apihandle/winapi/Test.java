/**
 * Project: BE_PUM_V1
 * Package name: v2.org.analysis.apihandle.be_pum.winapi
 * File name: Test.java
 * Created date: Jan 30, 2015
 */
package v2.org.analysis.apihandle.winapi;

import com.sun.jna.Memory;
import com.sun.jna.Pointer;
import org.apache.log4j.Logger;

import v2.org.analysis.apihandle.winapi.structures.WinNTn.RTL_CRITICAL_SECTION;
import v2.org.analysis.apihandle.winapi.wininet.WininetDLL;

import com.sun.jna.platform.win32.Kernel32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinDef.DWORD;
import com.sun.jna.platform.win32.WinDef.DWORDByReference;
import com.sun.jna.platform.win32.WinDef.LONG;
import com.sun.jna.platform.win32.WinNT.HANDLE;
import com.sun.jna.ptr.IntByReference;
import com.sun.jna.ptr.PointerByReference;
import v2.org.analysis.apihandle.winapi.advapi32.Advapi32DLL;
import v2.org.analysis.apihandle.winapi.msvcrt.MSVCRTDLL;
import v2.org.analysis.apihandle.winapi.structures.Internal._startupinfo;

//import com.sun.jna.platform.win32.WinBase.STARTUPINFO;

/**
 * @author SEGFRY
 *
 */
public class Test {

	private static final Logger LOGGER = Logger.getLogger(Test.class);

	/**
	 * @param args
	 */
	@SuppressWarnings("deprecation")
	public static void main(String[] args) {
//		String Filter = null;
//		DWORD Flags = new DWORD(0);
//		DWORDByReference ReturnLength = new DWORDByReference();
//		PointerByReference Credentials = new PointerByReference();
//		
//		WinDef.BOOL cred = Advapi32DLL.INSTANCE.CredEnumerate(Filter, Flags,
//				ReturnLength, Credentials);
//		System.out.println("cred: " + cred);
//		DWORD len = ReturnLength.getValue();
//		System.out.println("len: " + len);
//		Pointer value = Credentials.getPointer();
//		Pointer value1 = Credentials.getValue();
//		long aLong = value.getInt(0);
//		String dump = value.dump(0, 100);
//		System.out.println("ret: " + cred);
		
		// int x = Kernel32DLL.INSTANCE.WinExec("Cpp.exe", 1);
		// BPState curState = null;

		// Environment env = curState.getEnvironement();
		// Stack stack = env.getStack();
		// Memory memory = env.getMemory();
		// Register register = env.getRegister();
		// Program program = Program.getProgram();

		long x = 10;
		// Long x = (long) Kernel32.INSTANCE.GetFileAttributes("Cpp.exe");
		// x =
		// Kernel32DLL.INSTANCE.GetProcAddress(Kernel32DLL.INSTANCE.LoadLibraryA("kernel32.dll"),
		// "GetLastError");
		// x = (long) Kernel32.INSTANCE.GetLastError();
		// Kernel32.INSTANCE.DeleteFile("C:\\Users\\SEGFRY\\Desktop\\SQL.sql");
		// SYSTEMTIME lpSystemTime = new SYSTEMTIME();
		// Kernel32.INSTANCE.GetSystemTime(lpSystemTime);

		// OSVERSIONINFO lpVersionInfo = new OSVERSIONINFO();
		// Kernel32.INSTANCE.GetVersionEx(lpVersionInfo);

		/** ReadFile & GetFileType API **/
		// HANDLE hFile = Kernel32.INSTANCE.CreateFile("E:\\text.txt",
		// -2147483648, 1, null, 3, 128, null); // for read
		// System.out.println(Kernel32.INSTANCE.GetFileType(hFile));
		// ByteBuffer lpBuffer = ByteBuffer.allocate(1024);
		// OVERLAPPED lpOverlapped = new OVERLAPPED();
		//
		// boolean ret = Kernel32.INSTANCE.ReadFile(hFile, lpBuffer, 9, new
		// IntByReference(0), lpOverlapped);
		// try {
		// String str = new String(lpBuffer.array(), "UTF-8");
		// System.out.println("String: " + str);
		// } catch (UnsupportedEncodingException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

		/** Write API **/
		// HANDLE hFile = Kernel32.INSTANCE.CreateFile("E:\\text2.txt",
		// 1073741824, 0, null, 1, 128, null); //for write
		// String str = "Anh khong thuoc ve...";
		// boolean ret = Kernel32.INSTANCE.WriteFile(hFile, str.getBytes(),
		// str.getBytes().length, new IntByReference(0),
		// new OVERLAPPED());
		// Kernel32DLL.INSTANCE.SetEndOfFile(hFile);
		// Kernel32.INSTANCE.CloseHandle(hFile);

		/** OPEN REG **/
		// HKEYByReference phkResult = new HKEYByReference();
		// Advapi32.INSTANCE.RegOpenKeyEx(WinReg.HKEY_CURRENT_USER, "Console",
		// 0, WinNT.KEY_ALL_ACCESS, phkResult);

		/** POSTMESSEAGE **/
		// User32DLL.INSTANCE.PostMessage(new HWND(new Pointer(10)), 0, new
		// WPARAM(0), new LPARAM(10));
		// System.out.println(User32DLL.INSTANCE.SendMessage(new HWND(new
		// Pointer(10)), 0, new WPARAM(0), new LPARAM(10)).longValue());

		/** STRING **/
		// System.out.println(Kernel32DLL.INSTANCE.lstrcat(null, "World!!! "));
		// System.out.println(Kernel32DLL.INSTANCE.lstrcmp("abc", "aad"));
		// System.out.println(Kernel32DLL.INSTANCE.lstrlen(null));

		/** FILE **/
		// Kernel32.INSTANCE.CopyFile("E:\\text.txt", "E:\\text3.txt", false);
		// Kernel32.INSTANCE.MoveFile("E:\\text3.txt", "E:\\text1.txt");

		// Kernel32DLL.INSTANCE.ExitProcess(10000);

		// x =
		// Pointer.nativeValue(Kernel32DLL.INSTANCE.GetEnvironmentStrings());
		// x = Kernel32DLL.INSTANCE.FreeEnvironmentStrings(new
		// Pointer(x)).longValue();

		// System.out.println(Kernel32DLL.INSTANCE.GetCommandLine());
		// System.out.println(Kernel32DLL.INSTANCE.IsDebuggerPresent());

		// STARTUPINFO lpStartupInfo = new STARTUPINFO();
		// Kernel32DLL.INSTANCE.GetStartupInfo(lpStartupInfo);
		// System.out.println("Address: " +
		// Pointer.nativeValue(lpStartupInfo.lpTitle) + "lpTitle: " + (new
		// Pointer(Pointer.nativeValue(lpStartupInfo.lpTitle))).getWideString(0));
		// x = Pointer.nativeValue(hD.getPointer());

		// HANDLE hD = Kernel32DLL.INSTANCE.GetStdHandle(new DWORD(-12));

		// x = Kernel32.INSTANCE.GetVersion().longValue();

		// User32DLL.INSTANCE.MessageBox(null, "Click any button!", "Hello", new
		// UINT(310));

		// Kernel32DLL.INSTANCE.SetHandleCount(new UINT(1000000000));

		// WIN32_FIND_DATA FindFileData = new WIN32_FIND_DATA();
		// HANDLE hFind;
		// hFind = Kernel32DLL.INSTANCE.FindFirstFile(new WString("E:\\*"),
		// FindFileData);
		// BOOL ret = Kernel32DLL.INSTANCE.FindClose(hFind);

		// char[] lpBuffer = new char[100];
		// Kernel32DLL.INSTANCE.GetCurrentDirectory(new DWORD(100), lpBuffer);
		// System.out.println("GetCurrentDirectory: " + new String(lpBuffer));

		// char[] lpBuffer = new char[100];
		// Kernel32DLL.INSTANCE.GetSystemDirectory(lpBuffer, new UINT(100));
		// System.out.println("GetSystemDirectory: " + new String(lpBuffer));

		// char[] lpBuffer = new char[100];
		// Kernel32DLL.INSTANCE.GetWindowsDirectory(lpBuffer, new UINT(100));
		// System.out.println("GetWindowsDirectory: " + new String(lpBuffer));

		// char[] lpBuffer = new char[100];
		// IntByReference lpnSize = new IntByReference(100);
		// Kernel32.INSTANCE.GetComputerName(lpBuffer, lpnSize);
		// System.out.println("GetComputerName: " + new String(lpBuffer));

		// String lpDirectoryName = "C:\\";
		// LongByReference lpFreeBytesAvailable = new LongByReference();
		// LongByReference lpTotalNumberOfBytes = new LongByReference();
		// LongByReference lpTotalNumberOfFreeBytes = new LongByReference();
		// Kernel32.INSTANCE.GetDiskFreeSpaceEx(lpDirectoryName,
		// lpFreeBytesAvailable, lpTotalNumberOfBytes,
		// lpTotalNumberOfFreeBytes);
		// System.out.println("GetDiskFreeSpaceEx: " + lpDirectoryName +
		// " lpFreeBytesAvailable "
		// + lpFreeBytesAvailable.getValue() + " lpTotalNumberOfBytes " +
		// lpTotalNumberOfBytes.getValue()
		// + " lpTotalNumberOfFreeBytes " +
		// lpTotalNumberOfFreeBytes.getValue());

		// HANDLE hProcess = Kernel32.INSTANCE.GetCurrentProcess();
		// IntByReference Wow64Process = new IntByReference(1);
		// Kernel32.INSTANCE.IsWow64Process(hProcess, Wow64Process);
		// System.out.println("Wow64Process: " + Wow64Process.getValue());

		// char[] abc = ("abcd").toCharArray();
		// String str = "abcd";
		// System.out.println(User32DLL.INSTANCE.CharUpperBuff(abc, new
		// DWORD(2)));
		// System.out.println(abc);

		// RTL_CRITICAL_SECTION lpCriticalSection = new RTL_CRITICAL_SECTION();
		// //lpCriticalSection.DebugInfo = new LPVOID(0);
		// // lpCriticalSection.LockCount = new LONG(10);
		// // lpCriticalSection.LockSemaphore = new HANDLE();
		// // lpCriticalSection.OwningThread = new HANDLE();
		// // lpCriticalSection.RecursionCount = new LONG(100);
		// lpCriticalSection.SpinCount = new ULONG_PTR(10);
		// Kernel32DLL.INSTANCE.EnterCriticalSection(lpCriticalSection );

		// WString lpRootPathName = new WString("");
		// DWORDByReference lpSectorsPerCluster = new DWORDByReference(new
		// DWORD());
		// DWORDByReference lpBytesPerSector = new DWORDByReference(new
		// DWORD());
		// DWORDByReference lpNumberOfFreeClusters = new DWORDByReference(new
		// DWORD());
		// DWORDByReference lpTotalNumberOfClusters = new DWORDByReference(new
		// DWORD());
		// Kernel32DLL.INSTANCE.GetDiskFreeSpace(lpRootPathName,
		// lpSectorsPerCluster, lpBytesPerSector,
		// lpNumberOfFreeClusters, lpTotalNumberOfClusters);

		// String className = findClassName("kernel32", "DeleteFile");
		// if (className != null) {
		// try {
		// Class<?> clazz = Class.forName(className);
		// Constructor<?> ctor = clazz.getConstructor();
		// API api = (API) ctor.newInstance();
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// }

		// HANDLE hSnapshot = Kernel32DLL.INSTANCE.CreateToolhelp32Snapshot(new
		// DWORD(2), new DWORD(0));
		// PROCESSENTRY32 lppe = new PROCESSENTRY32();
		// BOOL ret = Kernel32DLL.INSTANCE.Process32First(hSnapshot, lppe );
		// if (ret.booleanValue()) {
		// System.out.println(lppe.toString(false));
		// System.out.println("lppe.szExeFile: " + new String(lppe.szExeFile));
		// }
		// while (Kernel32DLL.INSTANCE.Process32Next(hSnapshot,
		// lppe).booleanValue()) {
		// System.out.println(lppe.toString(false));
		// System.out.println("lppe.szExeFile: " + new String(lppe.szExeFile));
		// }

		// int currentID = Kernel32.INSTANCE.GetCurrentProcessId();
		//
		// HANDLE snap = Kernel32DLL.INSTANCE.CreateToolhelp32Snapshot(new
		// DWORD(4), new DWORD(0));
		//
		// THREADENTRY32 lpte = new THREADENTRY32();
		// lpte.dwSize = new DWORD(lpte.size());
		// if (Kernel32DLL.INSTANCE.Thread32First(snap, lpte).booleanValue()) {
		// while (Kernel32DLL.INSTANCE.Thread32Next(snap, lpte).booleanValue())
		// {
		// if (lpte.th32OwnerProcessID.intValue() == currentID)
		// {
		// DWORD dwDesiredAccess = new DWORD(2);
		// BOOL bInheritHandle = new BOOL(0);
		// DWORD dwThreadId = new DWORD(lpte.th32ThreadID.longValue());
		// HANDLE rezz = Kernel32DLL.INSTANCE.OpenThread(dwDesiredAccess,
		// bInheritHandle, dwThreadId);
		//
		// long value = (rezz == null) ? 0 :
		// Pointer.nativeValue(rezz.getPointer());
		// System.out.println("Return Value: " + value);
		// System.out.println("Error: " + Kernel32.INSTANCE.GetLastError());
		// }
		// }
		// } else {
		// }

		// String pMessage = new String("%1!*.*s! %4 %5!*s!");
		// String pArgs[] = { "4", "2", "Bill", // %1!*.*s! refers back to the
		// first insertion string in pMessage
		// "Bob", // %4 refers back to the second insertion string in pMessage
		// "6", "Bill" }; // %5!*s! refers back to the third insertion string in
		// pMessage
		// char buffer[] = new char[101];
		//
		//
		// Kernel32DLL.INSTANCE.FormatMessageW(new DWORD(9216),
		// pMessage,
		// null,
		// null,
		// buffer,
		// new DWORD(101),
		// pArgs);
		//
		// System.out.println(new String(buffer));
		//
		// char[] buf = new char[100];
		// long[] ar = {10, 20};
		// Kernel32DLL.INSTANCE.wsprintf(buf, "%s", ar);

		// HANDLE file = Kernel32.INSTANCE.CreateFile("Log.log", 1073741824, 0,
		// null, 3, 128, null);
		// x =
		// Kernel32DLL.INSTANCE._write(((int)Pointer.nativeValue(file.getPointer())),
		// null, new UINT(6));

		// HKEYByReference phkResult = new HKEYByReference();
		// LONG retz =
		// Advapi32DLL.INSTANCE.RegOpenKey(WinReg.HKEY_CURRENT_USER,"Software\\Microsoft\\Windows\\CurrentVersion\\Policies\\System",
		// phkResult);
		// x = retz.longValue();

		 IntByReference _Argc = new IntByReference();
		 com.sun.jna.Memory _Argv = new com.sun.jna.Memory(4);
		 com.sun.jna.Memory _Env = new com.sun.jna.Memory(4);
		 int _DoWildCard = 0;
		 _startupinfo _StartInfo = new _startupinfo();
		 MSVCRTDLL.INSTANCE.__getmainargs(_Argc, _Argv, _Env, _DoWildCard,
		 _StartInfo);
		 // PointerByReference
		 int narg = _Argc.getValue();
		 for (int i = 0; i < narg; i++) {
		 System.out.println(i + ": " + _Argv.getPointer(0).getPointer(i *
		 4).getString(0));
		 }
		
		 int c = 0;
		 while (true) {
		 try {
		 System.out.println(c + ": " + _Env.getPointer(0).getPointer(c *
		 4).getString(0));
		 c += 1;
		 } catch (Exception ex) {
		 System.out.println("BREAK;");
		 break;
		 }
		 }

		// x = MsCorEEDLL.INSTANCE._CorExeMain();
		// if (x > 0)
		// System.out.println(x);
		// Kernel32.INSTANCE.Gl
		// User32.INSTANCE.De
		// Ole32.INSTANCE.
		// Shell32.INSTANCE.SHG
		// Advapi32.INSTANCE.OpenThreadToken(ThreadHandle, DesiredAccess,
		// OpenAsSelf, TokenHandle)

		// DWORDByReference lpdwProcessId = new DWORDByReference();
		// x =
		// User32DLL.INSTANCE.GetWindowThreadProcessId(User32DLL.INSTANCE.GetShellWindow(),
		// lpdwProcessId).intValue();
		// System.out.println("GetWindowThreadProcessId: " + x);
		//
		// HANDLE ProcessHandle = Kernel32.INSTANCE.GetCurrentProcess();
		// int ProcessInformationClass = 0;
		// PROCESS_BASIC_INFORMATION.ByReference ProcessInformation = new
		// PROCESS_BASIC_INFORMATION.ByReference();
		// ULONG ProcessInformationLength = new
		// ULONG(ProcessInformation.size());
		//
		// x = NtdllDLL.INSTANCE.NtQueryInformationProcess(ProcessHandle,
		// ProcessInformationClass, ProcessInformation,
		// ProcessInformationLength, null);
		// System.out.println("NtQueryInformationProcess: " +
		// ProcessInformation);
		//
		// DWORDByReference info = new DWORDByReference();
		// x = NtdllDLL.INSTANCE.NtQueryInformationProcess(ProcessHandle, 0x1E,
		// info, new ULONG(4), null);
		//

		//
		// System.out.println(System.getProperty("jna.boot.library.name",
		// "jnidispatch"));

		// Kernel32.INSTANCE.GetSystemDefaultLCID()
		// User32.INSTANCE.GetAsyncKeyState(vKey)
		// GDI32.INSTANCE.Ge
		// Advapi32.INSTANCE.is
		// NtDll.INSTANCE.

		// HKEYByReference phkResult = new HKEYByReference();
		// int retzz = Advapi32.INSTANCE.RegOpenKeyEx(new HKEY(0x80000001),
		// "SOFTWARE\\SEGFRY\\TEST", 0,
		// (int)ERegKeySecuritynAccessRights.KEY_READ.getValue(), phkResult);
		// System.out.println(retzz);
		//
		// String str = "abc";
		// x = Advapi32.INSTANCE.RegSetValueEx(phkResult.getValue(), "testPath",
		// 0, EKeyValueType.REG_SZ.getValue(), str.toCharArray(), 7);

		// String lpFileName = "API_Note.txt";
		// int nBufferLength = 50;
		// char[] lpBuffer = new char[50];
		// Pointer lpFilePart = null;
		// Kernel32DLL.INSTANCE.GetFullPathName(lpFileName, nBufferLength,
		// lpBuffer, lpFilePart);

		// Pointer ret = MSVCRTDLL.INSTANCE.__p___initenv();
		// System.out.println(Pointer.nativeValue(ret.getPointer(0)));
		// int c = 0;
		// while (true) {
		// try {
		// String env = ret.getPointer(0).getPointer(c * 4).getString(0);
		// System.out.println(String.format("ret[%d]: %s", c, env));
		// c += 1;
		// } catch (Exception ex) {
		// break;
		// }
		// }
		//
		// System.out.println(100 + ":" + 10);
		//
		// IntByReference _Argc = new IntByReference();
		// com.sun.jna.Memory _Argv = new com.sun.jna.Memory(4);
		// com.sun.jna.Memory _Env = new com.sun.jna.Memory(4);
		// int _DoWildCard = 0;
		// _startupinfo _StartInfo = new _startupinfo();
		//
		// MSVCRTDLL.INSTANCE.__getmainargs(_Argc, _Argv, _Env, _DoWildCard,
		// null);
		// c = 0;
		// while (true) {
		// try {
		// String env = _Env.getPointer(0).getPointer(c * 4).getString(0);
		// System.out.println(String.format("_Env[%d]: %s", c, env));
		// c += 1;
		// } catch (Exception ex) {
		// break;
		// }
		// }

		// long pointer = funcOpen();
		// String argsm[] = new String[2];
		// argsm[0] = "\n%s";
		// argsm[1] = "test";
		// FILE fff = new FILE(new Pointer(pointer));
		// int ret = MSVCRTDLL.INSTANCE.fprintf(fff, argsm);
		// System.out.println(ret);
		// MSVCRTDLL.INSTANCE.fclose(new FILE(new Pointer(pointer)));

		// char[] buf = new char[255];
		// MSVCRTDLL.INSTANCE.sprintf(buf, "%s:%d", "abc", "1");
		// System.out.println(new String(buf));
		// x = 1;

		// LoadLibrary loadLibrary = new LoadLibrary();
		// long lib = loadLibrary.execute("msvbvm60");
		// System.out.println(lib);
		//
		// GetProcAddress getProcAddress = new GetProcAddress();
		//
		// HMODULE hModule = new HMODULE();
		// hModule.setPointer(new Pointer(lib));
		//
		// long proc = Kernel32DLLwithoutOption.INSTANCE.GetProcAddress(hModule,
		// new Pointer(0x204));
		// // long proc = getProcAddress.execute(lib, "MethCallEngine");
		// System.out.println(proc);

		// try {
		// countDownLatch.await();
		// } catch (InterruptedException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

		HANDLE ret = WininetDLL.INSTANCE.InternetOpen("WinInet Example", new DWORD(1), null,
				null, null);

		System.out.println(ret == null);
		System.out.println("OUT");
	}

	// static long funcOpen() {
	// FILE f =
	// MSVCRTDLL.INSTANCE.fopen("C:\\Workspace\\BE-PUMv2\\Storage\\C\\WINDOWS\\system32\\drivers\\etc\\hosts",
	// "a");
	// System.out.println("Error: " + Kernel32.INSTANCE.GetLastError());
	// Kernel32.INSTANCE.SetLastError(0);
	// long pointer = Pointer.nativeValue(f.getPointer());
	// return pointer;
	// }
}

interface Listener {
	void run();
}

class A {
	public static int a = 2;

	public void print() {
		System.out.println(a);
	}

	public A() {
	}

	public int foo1() {
		Kernel32.INSTANCE.GetModuleHandle("");
		int rett = Kernel32.INSTANCE.GetLastError();
		System.out.println("ID : " + Thread.currentThread().getId());
		return rett;
	}

	public int foo2() {
		int ret = Kernel32.INSTANCE.GetLastError();
		System.out.println("ID : " + Thread.currentThread().getId());
		return ret;
	}
}

class B extends A {
	public B() {
		a = 200;
	}
}

class NewThread1 extends Thread {
	private RTL_CRITICAL_SECTION _lpCriticalSection;

	public NewThread1(RTL_CRITICAL_SECTION lpCriticalSection) {
		this._lpCriticalSection = lpCriticalSection;
		start(); // báº¯t Ä‘áº§u thread
	}

	// This is the entry point for the Thread.
	@Override
	public void run() {
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("1: " + (new A()).foo1());
		this._lpCriticalSection.LockCount = new LONG(100);
	}
}

class NewThread2 extends Thread {
	private RTL_CRITICAL_SECTION _lpCriticalSection;

	public NewThread2(RTL_CRITICAL_SECTION lpCriticalSection) {
		this._lpCriticalSection = lpCriticalSection;
		start(); // báº¯t Ä‘áº§u thread
	}

	// This is the entry point for the Thread.
	@Override
	public void run() {
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("2: " + (new A()).foo2());
		this._lpCriticalSection.LockCount = new LONG(200);
	}
}