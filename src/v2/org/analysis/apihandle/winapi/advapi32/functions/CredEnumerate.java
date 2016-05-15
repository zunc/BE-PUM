/**
 * Project: BE_PUM_V1
 * Package name: v2.org.analysis.apihandle.be_pum.winapi.kernel32.functions
 * File name: RegOpenKeyEx.java
 * Created date: Feb 6, 2015
 * Description:
 */
package v2.org.analysis.apihandle.winapi.advapi32.functions;

import v2.org.analysis.apihandle.winapi.advapi32.Advapi32API;
import v2.org.analysis.system.registry.ERegKeySecuritynAccessRights;
import v2.org.analysis.system.registry.RegistryHandle;
import v2.org.analysis.value.LongValue;

import com.sun.jna.Pointer;
import com.sun.jna.platform.win32.Advapi32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinDef.DWORDByReference;
import com.sun.jna.platform.win32.WinReg.HKEY;
import com.sun.jna.platform.win32.WinReg.HKEYByReference;
import v2.org.analysis.apihandle.winapi.advapi32.Advapi32DLL;

/**
 * The RegOpenKeyEx function opens the specified registry key. Note that key
 * names are not case sensitive.
 * 
 * BOOL CredEnumerate(
 * _In_  LPCTSTR     Filter,
 * _In_  DWORD       Flags,
 * _Out_ DWORD       *Count,
 * _Out_ PCREDENTIAL **Credentials
);
 * 
 * @author zunc
 * 
 */
public class CredEnumerate extends Advapi32API {

	public CredEnumerate() {
		super();
		NUM_OF_PARMS = 5;
	}


	@Override
	public void execute() {
		long t1 = this.params.get(0);
		long t2 = this.params.get(1);
		long t3 = this.params.get(2);
		long t4 = this.params.get(3);

		String Filter = t1 != 0 ? this.memory.getPrintable(t1) : null;
		int Flags = (int) t2;
		DWORDByReference ReturnLength = (t3 == 0L) ? null : new DWORDByReference();
		Pointer Credentials = new Pointer(t4);
		
//		WinDef.BOOL ret = Advapi32DLL.INSTANCE.CredEnumerate(Filter, Flags,
//				1, Credentials);
		
		//memory.setDoubleWordMemoryValue(t5, new LongValue(keyResult));
	}

}
